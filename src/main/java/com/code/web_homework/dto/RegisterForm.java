package com.code.web_homework.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 用户注册表单对象。
 */
public class RegisterForm {

    /** 登录账号 */
    @NotBlank(message = "请填写登录账号")
    @Size(min = 4, max = 20, message = "账号长度需在 4-20 个字符之间")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]*$", message = "账号需以字母开头，只能包含字母、数字和下划线")
    private String username;

    /** 登录密码 */
    @NotBlank(message = "请填写登录密码")
    @Size(min = 6, max = 20, message = "密码长度需在 6-20 个字符之间")
    private String password;

    /** 确认密码 */
    @NotBlank(message = "请再次输入密码")
    private String confirmPassword;

    /** 真实姓名 */
    @NotBlank(message = "请填写真实姓名")
    private String realName;

    /** 性别 1男 2女 */
    private String gender = "1";

    /** 手机号码 */
    @NotBlank(message = "请填写手机号码")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /** 电子邮箱 */
    @Pattern(regexp = "^$|^[\\w.-]+@[\\w-]+(\\.[\\w-]+)+$", message = "邮箱格式不正确")
    private String email;

    /** 注册身份 USER 普通会员 / HOST 房东 */
    private String roleCode = "USER";

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }
}
