package com.lfs.config.model;

import com.lfs.config.enums.ResultType;
import lombok.Data;

@Data
public class ResultError extends Result {

    ResultError(Integer code, String message) {
        super.setCode(code);
        super.setMessage(message);
        super.setType(ResultType.ERROR.getName());
    }

}
