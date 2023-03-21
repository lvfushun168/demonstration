package com.lfs.config.vo;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PageVo {

    private Integer pageNum;

    private Integer pageSize;

    private Long total;

    private Long pages;

    private Boolean hasNext;

    private Boolean hasPrevious;

    private List<?> dataList;

    public static PageVo setPage(Page page,List<?> dataList,Integer pageNum,Integer pageSize){
        return PageVo.builder()
                .pageNum(pageNum)
                .pageSize(pageSize)
                .total(page.getTotal())
                .pages(page.getPages())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .dataList(dataList)
                .build();
    }
}
