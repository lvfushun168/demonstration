package com.lfs.config.enums;

import lombok.AllArgsConstructor;

import java.util.Arrays;

/**
 * 健康码颜色枚举
 */
@AllArgsConstructor
public enum QrColorEnum {
    GREEN("green"),
    YELLOW("yellow"),
    RED("red"),
    UNKNOWN("unknown");

    private String color;


    public String getColor() {
        return color;
    }


    public static QrColorEnum getQrColor(String color){
        return Arrays.stream(values()).filter(it->color.equals(it.getColor())).findFirst().orElse(UNKNOWN);
    }
}
