package com.lfs.config.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ErrorEnum {

    UNKNOWN(10001,"未知错误"),
    REPETITIVE(10002,"名称重复"),
    USERNAME(10003,"用户名为空"),
    PASSWORD(10004,"密码为空"),
    UNLOGIN(10005,"请先登录"),
    NOAUTH(10006,"没有权限"),
    ACCOUNTERROR(10007,"账号错误"),
    PASSWORDERROR(10008,"密码错误"),
    NOTOKEN(10009,"没有令牌"),
    ERRORTOKEN(10010,"错误令牌"),
    USERERR(10011,"账号错误"),
    PASSWORDERR(10012,"密码错误"),
    LOGINERR(10013,"登陆状态异常");


    private Integer code;
    private String name;

    public static ErrorEnum getEnumFromName(String name){
        return Arrays.stream(ErrorEnum.values()).filter(errorEnum -> errorEnum.name.equals(name)).findFirst().orElse(null);
    }

}
