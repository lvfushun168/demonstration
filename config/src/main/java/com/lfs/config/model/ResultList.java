package com.lfs.config.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ResultList extends Result {
    ResultList(String type, List<?> results) {
        this.type = type;
        this.results = results;
    }

    private String type;

    private List<?> results;
}
