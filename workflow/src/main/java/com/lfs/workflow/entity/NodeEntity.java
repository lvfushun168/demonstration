package com.lfs.workflow.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NodeEntity {

    @Id
    private String id;

    private String event;

    private String url;

    private String nodeName;

    private String content;

    private String handler;

    private Integer conductStatus;

    private NodeEntity previous;

    private NodeEntity next;

    private NodeEntity fallback;

    private LocalDateTime expireTime;

    private Boolean deleteStatus =false;
}
