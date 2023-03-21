package com.lfs.authentication.shiro.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lfs.authentication.shiro.entity.Permission;
import com.lfs.authentication.shiro.entity.RolePermission;
import com.lfs.authentication.shiro.entity.UserRole;
import com.lfs.authentication.shiro.repository.PermissionRepository;
import com.lfs.authentication.shiro.repository.RolePermissionRepository;
import com.lfs.authentication.shiro.repository.UserRoleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class PermissionService {
    UserRoleRepository userRoleRepository;
    RolePermissionRepository rolePermissionRepository;
    PermissionRepository permissionRepository;


    public Set<String> queryUserPermissByUserId(Integer userId){
        LambdaQueryWrapper<UserRole> userQuery = new LambdaQueryWrapper<>();
        userQuery.eq(UserRole::getUserId,userId);
        List<UserRole> userRoles = userRoleRepository.selectList(userQuery);
        List<Integer> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());

        LambdaQueryWrapper<RolePermission> permissionQuery = new LambdaQueryWrapper<>();
        permissionQuery.in(RolePermission::getRoleId,roleIds);
        List<RolePermission> rolePermissions = rolePermissionRepository.selectList(permissionQuery);
        List<String> permissions = rolePermissions.stream().map(RolePermission::getPermissId)
                .map(permissionId-> permissionId.toString())
                .collect(Collectors.toList());

        return new HashSet<>(permissions);
    }


    public List<Permission> findAllNextPermissionByPermissionId(Integer id){
        List<Permission> permissionList = permissionRepository.findAllNextPermission(id);
        return permissionList;
    }

}
