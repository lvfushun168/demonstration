package com.lfs.system.annotation;

import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
//@Documented
//@Constraint(validatedBy = {FieldValidator.class})
public @interface FieldFill {

    boolean required() default true;

    String content() default "";

    /**
     * enum class
     */
    Class<?> value() default Class.class;

    // 注解类型变量，下面几个是必须定义的，因为使用了@Constraint
    String message() default "错误的!这是不对的!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
