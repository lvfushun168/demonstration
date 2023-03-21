package com.lfs.system.aop;

import com.lfs.system.annotation.FieldFill;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * 字段赋值切面
 */
@Aspect
@Configuration
@Slf4j
@Order(2)
public class FieldFillAop {

    @SneakyThrows
    @Before("@annotation(org.springframework.web.bind.annotation.PostMapping) || @annotation(org.springframework.web.bind.annotation.DeleteMapping) || @annotation(org.springframework.web.bind.annotation.PutMapping) || @annotation(org.springframework.web.bind.annotation.GetMapping )|| @annotation(org.springframework.web.bind.annotation.RequestMapping ) ||@annotation(com.lfs.system.annotation.FieldFill)")
    public void before(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (null==arg){
                continue;
            }
            Field[] declaredFields = arg.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                Annotation[] annotations = field.getAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation instanceof FieldFill) {
                        FieldFill fieldFill = (FieldFill) annotation;
                        boolean required = fieldFill.required();//获取注解的值
                        if (required) {
                            field.setAccessible(true);
                            if (field.get(arg) == null) {
                                try {
                                    String content = fieldFill.content();
                                    field.set(arg, StringUtils.isEmpty(content) ? "visitor" : content);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }

    }


}
