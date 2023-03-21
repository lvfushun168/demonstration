package com.lfs.config.config;

import com.lfs.config.util.SpringUtil;
import org.apache.ibatis.cache.Cache;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

//TODO 部署在服务器上时,启动直接报反射失败的问题（严重）
//但是3.18日的测试又恢复了正常。问题几乎可以确定就是出在某种条件的【package】上，此时打出来的包即便在本地也无法java -jar启动，但是idea可以
//在5.16日的测试中发现竟然是重启电脑之后就可正常打包
public class MybatisRedisCache implements Cache{

    // 读写锁
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

    //不属于component，这里没办法自动装配
    private RedisTemplate<String, Object> redisTemplate = (RedisTemplate<String, Object>) SpringUtil.getBean("redisTemplate");

    private String id;





    public MybatisRedisCache(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("Cache instances require an ID");
        }
        this.id = id;
    }



    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void putObject(Object key, Object value) {
        if (value != null) {
            redisTemplate.opsForValue().set(key.toString(), value);
        }
    }

    @Override
    public Object getObject(Object key) {
        if (key != null) {
            return redisTemplate.opsForValue().get(key.toString());
        }
        return null;
    }

    @Override
    public Object removeObject(Object key) {
        if (key != null) {
            redisTemplate.delete(key.toString());
        }
        return null;
    }

    @Override
    public void clear() {
        Set<String> keys = redisTemplate.keys("*:" + this.id + "*");
        if (!CollectionUtils.isEmpty(keys)) {
            redisTemplate.delete(keys);
        }
    }

    @Override
    public int getSize() {
        Long size = redisTemplate.execute(RedisServerCommands::dbSize);
        return size.intValue();
    }


    @Override
    public ReadWriteLock getReadWriteLock() {
        return this.readWriteLock;
    }


}
