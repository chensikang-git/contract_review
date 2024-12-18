package com.swufe.userservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.swufe.chatlaw.DistributedCache;
import com.swufe.chatlaw.core.UserContext;
import com.swufe.chatlaw.core.UserInfoDTO;
import com.swufe.chatlaw.exception.ClientException;
import com.swufe.chatlaw.exception.ServiceException;
import com.swufe.chatlaw.toolkit.JWTUtil;
import com.swufe.userservice.dao.entity.UserDO;
import com.swufe.userservice.dto.UserLoginDTO;
import com.swufe.userservice.dto.UserRegisterDTO;
import com.swufe.userservice.dto.UserUpdateDTO;
import com.swufe.userservice.dto.resp.GetUserDetailRespDTO;
import com.swufe.userservice.service.UserLoginService;
import com.swufe.userservice.dao.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.swufe.chatlaw.errorcode.BaseErrorCode.*;
import static com.swufe.userservice.common.constant.RedisKeyConstant.USER_LOGIN_KEY;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserLoginServiceImpl implements UserLoginService {

    private final UserMapper userMapper;
    private final DistributedCache distributedCache;
    private final FileStorageService fileStorageService;


    /**
     *  用户注册
     */
    @Override
    public void register( UserRegisterDTO userRegisterDTO) {
        //根据用户名查询用户
        UserDO userDO =distributedCache.get(
                USER_LOGIN_KEY+userRegisterDTO.getUsername(),
                UserDO.class,
                () -> userMapper.selectOne(
                         new LambdaQueryWrapper<UserDO>().eq(UserDO::getUsername, userRegisterDTO.getUsername())
                ),
                7200,
                TimeUnit.SECONDS
        );

        //如果 userDO 不为空，ifPresent 会执行 Lambda 表达式的内容，抛出异常
        Optional.ofNullable(userDO).ifPresent(e -> {
            throw new ClientException(USER_REGISTER_NAMED_ERROR);
        });

        // 使用 BeanUtil 将 UserLoginDTO 转换为 UserDO 对象
        UserDO newUser = BeanUtil.toBean(userRegisterDTO, UserDO.class);

        // 插入到数据库
        int insert = userMapper.insert(newUser);
        // 判断插入是否成功
        if (!SqlHelper.retBool(insert)) {
            throw new ServiceException(INSERT_ERROR);
        }
    }

    /**
     *  用户登录
     */
    @Override
    public String login(UserLoginDTO userLoginDTO) {
        //根据用户名查询用户
        //当数据库查询到结果时，distributedCache.get() 会将结果（即 UserDO 对象）存入缓存
        UserDO userDO = distributedCache.get(
                USER_LOGIN_KEY + userLoginDTO.getUsername(),
                UserDO.class,
                () -> userMapper.selectOne(
                        new LambdaQueryWrapper<UserDO>().eq(UserDO::getUsername, userLoginDTO.getUsername())
                ),
                7200,
                TimeUnit.SECONDS
        );

        // 判断用户是否存在
        Optional.ofNullable(userDO)
                .ifPresentOrElse(
                        u -> {
                            // 如果用户存在，验证密码是否正确
                            if(!StrUtil.equals(userDO.getPassword(),userLoginDTO.getPassword())){
                                throw new ClientException(USER_PASSWORD_ERROR);
                            }
                        },
                        () -> {
                            // 如果用户不存在，抛出异常
                            throw new ClientException(USER_NAME_NOT_EXIST_ERROR);
                        }
                );

        return JWTUtil.generateAccessToken(new UserInfoDTO(userDO.getUserId(),userDO.getUsername()));
    }

    /**
     * 修改用户信息
     */
    @Override
    public void updateUser(UserUpdateDTO userUpdateDTO) {
        String username = UserContext.getUsername();
        // 根据userName从缓存中获取用户信息
        UserDO userDO = distributedCache.get(
                USER_LOGIN_KEY + username,
                UserDO.class,
                () -> userMapper.selectOne(
                        new LambdaQueryWrapper<UserDO>().eq(UserDO::getUsername, userUpdateDTO.getUsername())
                ),
                7200, // 缓存过期时间为 2 小时
                TimeUnit.SECONDS
        );
//        System.out.println("userDO:!!!!"+userDO);

        Optional.ofNullable(userDO)
                .ifPresentOrElse(
                        user -> {
                            user = BeanUtil.toBean(userUpdateDTO, UserDO.class);
                            //从thread local取出id，然后用id来查询数据库
                            Long userId= UserContext.getUserId();
                            user.setUserId(userId);
                            int updateCount =  userMapper.updateById(user);
                            if (!SqlHelper.retBool(updateCount)) {
                                throw new ServiceException(INSERT_ERROR);
                            }

                        },
                        () -> {
                            // 用户不存在，抛出异常
                            throw new ServiceException(USER_NAME_NOT_EXIST_ERROR);
                        }
                );
        delUserCache(username);


    }

    /**
     * 用户上传头像
     */

    @Override
    public String uploadPicture(MultipartFile file) {
        // 检查文件是否为空
//        System.out.println("file!!!!!!!!!:"+file.getOriginalFilename());
        if (file == null || file.isEmpty()) {
            throw new ClientException(PICTURE_EMPTY_ERROR);
        }
        return Optional.ofNullable(fileStorageService.of(file).upload())
                .map(FileInfo::getUrl) // 假设 getUrl() 是获取上传后 URL 的方法
                .orElseThrow(() -> new ServiceException(TENCENT_COS_ERROR));

    }

    @Override
    public GetUserDetailRespDTO getUserDetail() {
        String username = UserContext.getUsername();
        Optional. ofNullable(username)
                .orElseThrow(() -> new ClientException(USER_NAME_NOT_EXIST_ERROR));


        UserDO userDO = distributedCache.get(
                USER_LOGIN_KEY + username,
                UserDO.class,
                () -> userMapper.selectOne(
                        new LambdaQueryWrapper<UserDO>().eq(UserDO::getUsername, username)
                ),
                7200,
                TimeUnit.SECONDS
        );
        // 检查用户是否存在
        if (userDO == null) {
            throw new ClientException(USER_NAME_NOT_EXIST_ERROR);
        }
        return GetUserDetailRespDTO.builder()
                .username(userDO.getUsername())
                .password(userDO.getPassword())
                .phoneNumber(userDO.getPhoneNumber())
                .userPic(userDO.getUserPic())
                .build();


    }

//    /**
//     * 注销用户登录
//     */
//    @Override
//    public void logout(UserLogoutDTO userLogoutDTO) {
//
//        Long userId = UserContext.getUserId();
//        delUserCache(userId);
//
//    }

    //清理缓存
    private void delUserCache(String  userName) {
        boolean isRemoved = distributedCache.delete(USER_LOGIN_KEY + userName);
//        System.out.println("!!!!!!!!!!!!!"+isRemoved);
        //清理失败
        if (!isRemoved) {
            throw new ServiceException(REDIS_CLEAN_ERROR);
        }
    }

}
