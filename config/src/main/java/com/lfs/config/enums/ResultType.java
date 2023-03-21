package com.lfs.config.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum ResultType {
    VOID(0, "void"),
    OBJECT(1,"object"),
    LIST(2,"list"),
    PAGE(3,"page"),
    ERROR(4,"error");

    private Integer type;
    private String name;

    public static ResultType getFromName(String name){
        return Arrays.stream(values()).filter(it -> it.name().equals(name)).findFirst().orElse(null);
    }
}
