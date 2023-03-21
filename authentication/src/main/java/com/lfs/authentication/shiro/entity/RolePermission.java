package com.lfs.authentication.shiro.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@TableName(value = "role_permiss")
public class RolePermission {

    private Integer id;

    private Integer roleId;

    private Integer permissId;
}
