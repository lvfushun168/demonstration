package com.lfs.config.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ResultSuccess extends Result {
    ResultSuccess(String content) {
        this.content = content;
    }

    private String content;
}
