package com.lfs.authentication.shiro.util;


import com.lfs.authentication.shiro.entity.User;
import com.lfs.config.enums.ErrorEnum;

import java.util.HashMap;

public class UserUtil {
    /**
     * 用户信息
     */
    private static final ThreadLocal<User> userInfo =new ThreadLocal<>();
    public static User get(){
        return userInfo.get();
    }
    public static void setUser(User user){
        userInfo.set(user);
    }


    /**
     * 方法信息
     */
    private static final ThreadLocal<String> functionInfo =new ThreadLocal<>();
    public static String getFunctionName(){
        return functionInfo.get();
    }
    public static void setFunctionName(String function){
        functionInfo.set(function);
    }


    /**
     * 方法信息
     */
    private static final ThreadLocal<HashMap<String,Object>> mapHistory =new ThreadLocal<>();
    public static HashMap getMapHistory(){
        return mapHistory.get();
    }
    public static void setMapHistory(HashMap<String,Object> map){
        mapHistory.set(map);
    }


    /**
     * 错误码
     */
    private static final ThreadLocal<ErrorEnum> errInfo =new ThreadLocal<>();
    public static ErrorEnum getError(){
        return errInfo.get();
    }
    public static void setErrInfo(ErrorEnum error){
        errInfo.set(error);
    }


    /**
     * 获取健康码开关
     */
    private static final ThreadLocal<String> cut =new ThreadLocal<>();
    public static String getCut(){
        return cut.get();
    }
    public static void setCut(String status) { cut.set(status);
    }
}
