package com.lfs.authentication.shiro.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lfs.authentication.shiro.entity.UserRole;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends BaseMapper<UserRole> {
}
