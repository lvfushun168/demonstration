package com.lfs.workflow.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WorkflowEntity {

    @Id
    private String id;

    private String event;

    private List<String> relatedUsersId;

    private List<NodeEntity> nodeList ;

    private String currentPosition;

    private String creator;

    private Boolean deleteStatus;
}
