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
public class LfsVo {

    private Long id;

    private String name;

    private Integer age;

    private String gender;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String personDesc;

}
