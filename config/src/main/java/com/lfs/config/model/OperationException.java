package com.lfs.config.model;

import com.lfs.config.enums.ErrorEnum;
import lombok.Data;


@Data
/**
 * 统一异常处理
 */
public class OperationException extends RuntimeException{

    private Integer code;

    /**
     * 自定义异常
     * @param code
     * @param message
     */
    public OperationException(Integer code,String message){
        super(message);
        this.code = code;
    }

    /**
     * 已定义异常
     * @param errorEnum
     */
    public OperationException (ErrorEnum errorEnum){
        super(errorEnum.getName());
        this.code = errorEnum.getCode();
    }

    /**
     * 已定义异常+增补信息
     * @param errorEnum
     * @param supplement
     */
    public OperationException (ErrorEnum errorEnum,String supplement){
        super(errorEnum.getName()+supplement);
        this.code = errorEnum.getCode();
    }
}
