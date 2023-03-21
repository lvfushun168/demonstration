package com.lfs.system.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    /**
     * 超过请求幂等性提示信息
     */
    String message() default "请勿重复提交";

    /**
     * 该请求的唯一key 不填则为接口方法名称+接口api+接口参数值
     */
    String key() default "";

    /**
     * key的过期时间 等于请求最小间隔时长
     */
    long expireTime() default 1L;

    /**
     * 请求结束后是否删除key 推荐 false 让他自动过期 true 则变相认为不适用幂等性拦截
     */
    boolean delKey() default false;
}
