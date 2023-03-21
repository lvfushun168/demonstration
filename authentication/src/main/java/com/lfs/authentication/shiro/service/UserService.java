package com.lfs.authentication.shiro.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lfs.authentication.shiro.dto.CreateAccountDto;
import com.lfs.authentication.shiro.entity.User;
import com.lfs.authentication.shiro.entity.UserRole;
import com.lfs.authentication.shiro.repository.UserRepository;
import com.lfs.authentication.shiro.repository.UserRoleRepository;
import com.lfs.config.model.OperationException;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
@AllArgsConstructor
public class UserService {
    UserRepository userRepository;
    UserRoleRepository userRoleRepository;

    public User queryByUserName(String username){
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,username);
        User user = userRepository.selectOne(queryWrapper);
        return user;
    }


    @Transactional(rollbackFor = Exception.class)
    public void createAccount(CreateAccountDto dto){
        String username = dto.getUsername();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User user = userRepository.selectOne(queryWrapper);
        if (Optional.ofNullable(user).isPresent()) {
            throw new OperationException(114514,"已存在的账号");
        }
        User userEntity = new User();
        BeanUtils.copyProperties(dto,userEntity);
        userEntity.setCrtTime(LocalDateTime.now());
        userRepository.insert(userEntity);
        userRoleRepository.insert(UserRole.builder()
                        .userId(userEntity.getId())
                        .roleId(dto.getRoleId())
                .build());

    }
}
