package com.swufe.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDTO {

    @Size(max = 50, message = "用户名长度不能超过50个字符")
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    @Size(max = 50, message = "密码长度不能超过50个字符")
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).{6,50}$", message = "密码必须包含字母和数字，且至少6个字符")
    private String password;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    @NotBlank(message = "手机号码不能为空")
    private String phoneNumber;
}
