package com.lfs.workflow.repository;

import com.lfs.workflow.entity.NodeEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NodeRepository extends MongoRepository<NodeEntity,String> {
}
