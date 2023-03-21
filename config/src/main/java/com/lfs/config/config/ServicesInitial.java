package com.lfs.config.config;

import com.lfs.config.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 通用的初始化配置
 */
@Component
public class ServicesInitial {
    @Autowired
    RedisTemplate<String,String> redisTemplate;


    private static final String username = "lfs";

    /**
     * redis指定账号生成永久token
     */
    @PostConstruct
    public void RedisInitial(){
        String value = redisTemplate.opsForValue().get(username);
        if (StringUtils.isEmpty(value)){
            String token = JwtUtil.tokenGenerate(username);
            redisTemplate.opsForValue().set(username,token);
        }
    }
}
