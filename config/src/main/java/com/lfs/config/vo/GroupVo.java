package com.lfs.config.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GroupVo {
    private Long id;

    private String groupName;

    private String groupDesc;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
