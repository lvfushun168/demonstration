package com.lfs.authentication.shiro.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lfs.authentication.shiro.entity.UserRole;
import com.lfs.authentication.shiro.repository.UserRoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RoleService {
    UserRoleRepository userRoleRepository;



    public Set<String> queryUserRoleByUserId(Integer userId){
        LambdaQueryWrapper<UserRole> userQuery = new LambdaQueryWrapper<>();
        userQuery.eq(UserRole::getUserId,userId);
        List<UserRole> userRoles = userRoleRepository.selectList(userQuery);
        List<String> roleIds = userRoles.stream().map(UserRole::getRoleId)
                .map(roleId-> roleId.toString())
                .collect(Collectors.toList());

        return new HashSet<>(roleIds);
    }
}
