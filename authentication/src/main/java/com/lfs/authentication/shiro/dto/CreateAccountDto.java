package com.lfs.authentication.shiro.dto;


import com.lfs.system.annotation.FieldFill;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CreateAccountDto {

    @NotBlank(message = "名称不能为空")
    private String name;

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    private String sex;

    private String age;

    private String address;

    @FieldFill(message = "必须要有crtUserId")
    private String crtUserId;

    private Integer roleId = 1;

}
