package com.lfs.workflow.repository;

import com.lfs.workflow.entity.WorkflowEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WorkflowRepository extends MongoRepository<WorkflowEntity,String> {

    WorkflowEntity findByDeleteStatusAndEvent(Boolean deleteStatus,String event);
}
