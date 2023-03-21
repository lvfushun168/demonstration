package com.lfs.authentication.shiro.controller;

import com.lfs.config.model.Result;
import com.lfs.config.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/system")
@AllArgsConstructor
@Slf4j

/**
 * 登出接口
 */
public class LogoutController {
    RedisTemplate redisTemplate;

    /**
     * 登出
     * 1.如果非正常token直接返回成功
     * 2.如果正常token则清空redis的token
     * @return
     */
    @GetMapping("/logout")
    public Result logout(ServletRequest request){
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader("Authorization");
        String bearer = token.replace("Bearer ", "");
        String username = JwtUtil.getUsername(bearer);
        if (StringUtils.isNotEmpty(username)){
            redisTemplate.delete(username);
        }
        return Result.success();
    }
}
