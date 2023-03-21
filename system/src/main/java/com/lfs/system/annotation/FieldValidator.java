package com.lfs.system.annotation;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

/**
 * 这里泛型选择object对所有类型进行校验
 */
@Slf4j
public class FieldValidator implements ConstraintValidator<FieldFill,String> {

    private boolean require = false;

    private FieldFill fieldFill;

    @Override
    public void initialize(FieldFill constraintAnnotation) {
        require = constraintAnnotation.required();
        fieldFill = constraintAnnotation;
    }

    /**
     * 返回true放行，false报错message
     * @param field
     * @param constraintValidatorContext
     * 【这里false抛出的异常也是MethodArgumentNotValidException】
     * @return
     */
    @Override
    public boolean isValid(String field, ConstraintValidatorContext constraintValidatorContext) {
        // 注解属性为true就开始校验
        if (require) {
            //创建者为空则获取当前用户id作为创建者，如果当前未登录则设置为system
            if (StringUtils.isEmpty(field)){
//                Object o = modifyProperty(fieldFill.value());
                return false;
            }else {
                log.info("创建者【{}】试图创建一个账户",field);
                return true;
            }
        }
        return true;
    }


    public  <T> Object modifyProperty(Class value) {

//        Class<?> value = fieldFill.value();
        String className = value.getName();
        Field[] fields = value.getDeclaredFields();
        Object obj = null;
        try {
            obj = value.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        for (Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();
            try {
                FieldFill filedAnnotation = field.getAnnotation(FieldFill.class);
                if (null!=filedAnnotation){
                    field.set(obj,"default");
                }
            }catch (Exception exception){
                log.error(exception.getMessage());
            }
            System.err.println(name);
        }
        return obj;
    }

}
