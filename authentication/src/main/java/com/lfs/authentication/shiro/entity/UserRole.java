package com.lfs.authentication.shiro.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@TableName(value = "user_role")
public class UserRole {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private Integer roleId;
}
