package com.lfs.system.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NodeDto {

    private String event;

    private String url;

    private String nodeName;

    private String content;

    private String handler;

    private String previous;

    private String next;

    private String fallback;

    private LocalDateTime expireTime;
}
