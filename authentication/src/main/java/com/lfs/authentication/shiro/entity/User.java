package com.lfs.authentication.shiro.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lfs.config.config.UserTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@TableName(value = "user", autoResultMap=true)
public class User {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private String username;

    /**
     * 密码密文保存
     */
    @TableField(typeHandler = UserTypeHandler.class)
    private String password;

    private String phone;

    private String sex;

    private String age;

    private String address;

    private String crtUserId;

//    @TableField(value = "crt_time")
    private LocalDateTime crtTime;

    @TableField(exist = false)
    private Set<String> roles;

    @TableField(exist = false)
    private Set<String> permiss;
}
