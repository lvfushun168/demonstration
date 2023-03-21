package com.lfs.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WorkflowDto {
    private String id;

    private String event;

    private List<String> relatedUsersId;

    private List<NodeDto> nodeList ;

    private String creator;

    private String currentPosition;

}
