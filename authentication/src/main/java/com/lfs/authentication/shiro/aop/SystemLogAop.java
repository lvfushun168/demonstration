package com.lfs.authentication.shiro.aop;

import com.lfs.authentication.shiro.entity.User;
import com.lfs.authentication.shiro.util.UserUtil;
import com.lfs.config.util.IpUtils;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * System log
 */
@Aspect
@Component
@Order(3)
@Slf4j
public class SystemLogAop {
    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 系统访问日志
     * 【因为使用@Async后会获取不到request所以只对mongodb实行异步】
     * 需要排除重定向、资源请求、静态资源请求的访问记录
     * @param joinPoint
     */
    @Before("(@annotation(org.springframework.web.bind.annotation.PostMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.DeleteMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.PutMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.GetMapping )" +
            "|| @annotation(org.springframework.web.bind.annotation.RequestMapping ) " +
//            "|| @annotation(java.lang.Override ) " +
            "||@annotation(com.lfs.system.annotation.FieldFill) )" +
//            "&& !execution(* com.lfs.controller.WebsocketController.*(..))" +
            "&& !execution(* com.lfs.authentication.shiro.controller.LoginController.unauthorized(..))" )
    public void before(JoinPoint joinPoint) {
        try {
            this.record(joinPoint);
        }catch (Exception e){
            log.error("记录接口访问日志异常：{}", e.getMessage());
        }
    }


    /**
     * 日志会记录访问者的浏览记录，如果未登录则记录IP
     * @param joinPoint
     */
    @SneakyThrows
    private void record(JoinPoint joinPoint){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        //宿主机访问地址
        String ipAddress = IpUtils.getIpAddr(request);
        String requestURI = request.getRequestURI();
        Map<String, String[]> parameterMap = request.getParameterMap();
        String name = joinPoint.getSignature().getName();
        UserUtil.setFunctionName(name);
        User user = UserUtil.get();
        CompletableFuture<SystemLog> future = CompletableFuture.supplyAsync(() ->
            mongoTemplate.save(SystemLog.builder().ipAddress(ipAddress).requestURI(requestURI).parameterMap(parameterMap).methodName(name).user(Optional.ofNullable(user).isPresent()?user.getId().toString():ipAddress).createTime(LocalDateTime.now()).build()));
        SystemLog save = future.handle((result, throwable) -> {
            if (throwable != null) {
                log.error("mongodb记录日志异常：{}", throwable.getMessage());
                return null;
            } else {
                return result;
            }
        }).get();
        log.info("接口访问记录：{}", save);
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Document(collection="system_log")
    private static class SystemLog {
        @Id
        private String id;
        private String ipAddress;
        private String requestURI;
        private Map<String,String[]> parameterMap;
        private String methodName;
        private String user;
        private LocalDateTime createTime;
    }

}

