package com.swufe.userservice.dto.resp;

import com.swufe.chatlaw.base.BaseDO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetUserDetailRespDTO {



    @Size(max = 50, message = "用户名长度不能超过50个字符")
    private String username;

    @Size(max = 50, message = "密码长度不能超过50个字符")
    private String password;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    private String phoneNumber;

    @Pattern(regexp = "^(https?|ftp)://[^\s/$.?#].[^\s]*$", message = "图片URL格式不正确")
    private String userPic;
}
