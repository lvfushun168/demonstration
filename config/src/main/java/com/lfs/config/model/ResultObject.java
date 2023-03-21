package com.lfs.config.model;

import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class ResultObject<T> extends Result {
    ResultObject(T result, String type) {
        this.result = result;
        this.type = type;
    }

    private T result;

    private String type;
}
