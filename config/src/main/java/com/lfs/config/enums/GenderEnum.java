package com.lfs.config.enums;

import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public enum GenderEnum {

    MALE(0, "男"),

    FEMALE(1,"女"),

    UNKNOWN(2,"未知");

    private Integer type;
    private String name;



    public Integer getType() {
        return type;
    }
    public String getName() {
        return name;
    }




    public static GenderEnum getFromName(String name){
        return Arrays.stream(values()).filter(it -> it.name().equals(name)).findFirst().orElse(GenderEnum.UNKNOWN);
    }
}
