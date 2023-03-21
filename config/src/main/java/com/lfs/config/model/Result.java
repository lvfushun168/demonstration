package com.lfs.config.model;

import com.lfs.config.enums.ErrorEnum;
import com.lfs.config.enums.ResultType;
import com.lfs.config.vo.PageVo;
import lombok.Data;

import java.util.List;

@Data
public class Result<T> {
    protected Result() {
    }

    private Integer code = 10000;

    private String message = "success";

    private String type =  ResultType.VOID.getName();


    public static Result error(Integer code, String message){
        return new ResultError(code, message);
    }

    public static Result error(ErrorEnum errorEnum){
        return new ResultError(errorEnum.getCode(),errorEnum.getName());
    }



    public static Result success(){
        return new Result();
    }

    public static Result success(String content){
        return ResultSuccess.builder().content(content).build();
    }


    public static <T> ResultObject object(T result){
        return ResultObject.builder().result(result).type(ResultType.OBJECT.getName()).build();
    }


    public static <T> ResultList list(List<T> result){
        return ResultList.builder().results(result).type(ResultType.LIST.getName()).build();
    }


    public static ResultPage page(PageVo page){
        return ResultPage.builder().page(page).type(ResultType.PAGE.getName()).build();
    }
}
