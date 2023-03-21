package com.lfs.config.model;

import com.lfs.config.vo.PageVo;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ResultPage extends Result {
    ResultPage(PageVo page, String type) {
        this.page = page;
        this.type = type;
    }

    private PageVo page;

    private String type;
}
