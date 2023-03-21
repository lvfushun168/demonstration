package com.lfs.config.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.lfs.config.enums.DeleteEnum;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class FieldFillHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("createTime", LocalDateTime.now(),metaObject);
        this.setFieldValByName("updateTime", LocalDateTime.now(),metaObject);
        this.setFieldValByName("deleteFlag", DeleteEnum.NO,metaObject);
        this.setFieldValByName("delFlag", DeleteEnum.NO,metaObject);
        this.setFieldValByName("version", 1,metaObject);            //这个其实可以不要，一开始为null
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", LocalDateTime.now(),metaObject);
    }
}
