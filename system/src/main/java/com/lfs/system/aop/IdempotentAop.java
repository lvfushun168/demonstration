package com.lfs.system.aop;

import com.lfs.config.model.OperationException;
import com.lfs.config.util.CacheMapUtil;
import com.lfs.config.util.IpUtils;
import com.lfs.system.annotation.Idempotent;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.concurrent.TimeUnit;

/**
 * 幂等切面
 */
@Aspect
@Component
public class IdempotentAop implements CacheMapUtil {


    public static final String REQUEST_KEY = "RequestKey:";

    @Resource
    private RedisTemplate stringRedisTemplate;

    @Pointcut("@annotation(com.lfs.system.annotation.Idempotent)")
    public void Point() {}

    @Around(value = "Point()")
    public Object apiIdempotentCheck(ProceedingJoinPoint pjp) throws Throwable {

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);

        //宿主机访问地址
        String ipAddr = IpUtils.getIpAddr(request);
        String requestURI = request.getRequestURI();
        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        Idempotent annotation = method.getAnnotation(Idempotent.class);

        //无注解直接放行
        if (annotation == null) {
            return pjp.proceed();
        }

        //获取请求参数
        Object arg = pjp.getArgs()[0];
        String apiName = null;
        String parameterName = null;
        String apiMethodName = null;
        String key = null;

        //接口方法名称
        apiMethodName = method.getName();
        Parameter[] parameters = method.getParameters();

        //获取接口间隔时间
        long l = annotation.expireTime();
        Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
        apiName = getRequestName(requestURI, apiName, declaredAnnotations);
        Parameter parameter = parameters[0];

        //接口参数名称
        parameterName = parameter.getName();

        //如果用户自定义则默认使用修改后的规则
        if (StringUtils.isNotBlank(annotation.key())) {
            //TODO 后续对用户设置自定义key进行校验
            key = annotation.key();
        } else {
            key = REQUEST_KEY + ipAddr + ":" + apiName + ":" + apiMethodName + ":" + parameterName;
        }

        //如果当前key存在直接抛出异常
        if (stringRedisTemplate.hasKey(key)) {
            throw new OperationException(500,annotation.message());
        }

        if (arg != null && !"".equals(arg)) {
            stringRedisTemplate.opsForValue().set(key, arg.toString(), l, TimeUnit.SECONDS);
            requestKey.add(key);
        } else {
            stringRedisTemplate.opsForValue().set(key, "1", l, TimeUnit.SECONDS);
            requestKey.add(key);
        }

        //接口执行
        Object proceed = pjp.proceed();

        //默认不删除 如果修改则使用修改后的配置
        if (annotation.delKey()) {
            stringRedisTemplate.delete(key);
        }
        return proceed;
    }

    private String getRequestName(String requestURI, String apiName, Annotation[] declaredAnnotations) {
        //获取接口请求类型以及接口api
        for (Annotation declaredAnnotation : declaredAnnotations) {
            if (declaredAnnotation instanceof RequestMapping) {
                RequestMapping requestMapping = (RequestMapping) declaredAnnotation;
                String requestType = requestMapping.method()[0].name();
                apiName = requestType + ":" + requestURI;
            }
            if (declaredAnnotation instanceof DeleteMapping) {
                apiName = "DELETE:" + requestURI;
            }
            if (declaredAnnotation instanceof GetMapping) {
                apiName = "GET:" + requestURI;
            }
            if (declaredAnnotation instanceof PutMapping) {
                apiName = "PUT:" + requestURI;
            }
            if (declaredAnnotation instanceof PostMapping) {
                apiName = "POST:" + requestURI;
            }
        }
        return apiName;
    }
}
