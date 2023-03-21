package com.lfs.authentication.shiro.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@TableName(value = "role")
public class Role {

    private Integer id;

    private String roleName;

    private String description;

    private String crtUserId;

    private LocalDateTime crtTime;

}
