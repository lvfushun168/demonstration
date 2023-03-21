package com.lfs.workflow.service;

import com.lfs.authentication.shiro.util.UserUtil;
import com.lfs.config.enums.ErrorEnum;
import com.lfs.config.model.OperationException;
import com.lfs.system.dto.NodeDto;
import com.lfs.system.dto.WorkflowDto;
import com.lfs.workflow.entity.NodeEntity;
import com.lfs.workflow.entity.WorkflowEntity;
import com.lfs.workflow.repository.NodeRepository;
import com.lfs.workflow.repository.WorkflowRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 1.调用接口获取
 */
@Service
@AllArgsConstructor
public class WorkflowService {


    NodeRepository nodeRepository;
    WorkflowRepository workflowRepository;
    MongoTemplate mongoTemplate;




    public void createNode(NodeDto nodeDto){
        NodeEntity nodeEntity = new NodeEntity();
        BeanUtils.copyProperties(nodeDto,nodeEntity);
        nodeRepository.save(nodeEntity);
    }




    @Transactional(rollbackFor = Exception.class)
    public String creatWorkflow(List<NodeDto> list){
        if (CollectionUtils.isEmpty(list)){
            return null;
        }
        AtomicReference<String> currentPosition = new AtomicReference("0");
        String event = UUID.randomUUID().toString();
        List<NodeEntity> nodeList = this.findNodeByEventOrId(null,null);
        List<NodeEntity> collect = list.stream().map(nodeDto -> {
            NodeEntity nodeEntity = new NodeEntity();
            BeanUtils.copyProperties(nodeDto, nodeEntity);

            String next = nodeDto.getNext();
            if (StringUtils.isNotEmpty(next)){
                NodeEntity nextEntity = nodeList.stream().filter(entity -> entity.getId().equals(next)).findFirst().orElse(null);
                nodeEntity.setNext(nextEntity);
            }

            String previous = nodeDto.getPrevious();
            if (StringUtils.isNotEmpty(previous)){
                NodeEntity previousEntity = nodeList.stream().filter(entity -> entity.getId().equals(previous)).findFirst().orElse(null);
                nodeEntity.setPrevious(previousEntity);
            }else {
                String id = UUID.randomUUID().toString();
                nodeEntity.setId(id);
                currentPosition.set(id);
            }

            String fallback = nodeDto.getFallback();
            NodeEntity fallbackEntity = nodeList.stream().filter(entity -> entity.getId().equals(StringUtils.isNotEmpty(fallback)?fallback:previous)).findFirst().orElse(null);
            nodeEntity.setFallback(fallbackEntity);

            nodeEntity.setEvent(event);
            return nodeEntity;
        }).collect(Collectors.toList());

        List<String> relatedUsersList = list.stream().map(NodeDto::getHandler).collect(Collectors.toList());
        relatedUsersList.add(UserUtil.get().getId().toString());
        workflowRepository.save(WorkflowEntity.builder()
                        .currentPosition(currentPosition.get())
                        .relatedUsersId(relatedUsersList)
                        .nodeList(collect)
                .build());
        return event;
    }

    /**
     * show entire workflow
     * @param event
     * @return
     */
    public WorkflowDto showWorkflowDiagram(String event){
        WorkflowEntity workflow = workflowRepository.findByDeleteStatusAndEvent(false, event);
        return this.workflowEntityToDto(workflow);
    }


    /**
     * handle workflow
     */
    public WorkflowDto handleWorkflow(String event,Boolean handle){
        WorkflowEntity workflow = workflowRepository.findByDeleteStatusAndEvent(false, event);
        String currentPosition = workflow.getCurrentPosition();
        WorkflowDto workflowDto = this.workflowEntityToDto(workflow);
        if (currentPosition==null){
            return workflowDto;
        }
        List<NodeEntity> nodeList = workflow.getNodeList();
        String temp = currentPosition;
        NodeEntity nodeEntity = nodeList.stream().filter(entity -> entity.getId().equals(temp)).findFirst().orElseThrow(() -> new OperationException(ErrorEnum.UNKNOWN));
        if (handle){    //approval
            NodeEntity next = nodeEntity.getNext();
            if (Optional.ofNullable(next).isPresent()){
                currentPosition = next.getId();
            }else {
                currentPosition = null;
            }
        }else { //disapproval
            NodeEntity fallback = nodeEntity.getFallback();
            if (!Optional.ofNullable(fallback).isPresent()){
                fallback = nodeEntity.getPrevious();
            }
            currentPosition = fallback.getId();
        }
        workflowDto.setCurrentPosition(currentPosition);
        return workflowDto;
    }


    /**
     * show current workflow point
     */
    public WorkflowDto showWorkflowPoint(String event){
        WorkflowEntity workflow = workflowRepository.findByDeleteStatusAndEvent(false,event);
        List<String> relatedUsersList = workflow.getRelatedUsersId();
        if (!relatedUsersList.contains(UserUtil.get().getId().toString())){
            throw new RuntimeException();
        }
        return this.workflowEntityToDto(workflow);
    }



    /**
     * the node related to event
     * find out all node to reduce the number of queries
     * @param event
     * @return
     */
    private List<NodeEntity> findNodeByEventOrId(String event,String id){
        Criteria criteria = new Criteria();
        criteria.and("deleteStatus").is(false);
        if (StringUtils.isNotEmpty(event)){
            criteria.and("event").is(event);
        }
        if (StringUtils.isNotEmpty(event)){
            criteria.and("id").is(id);
        }
        Query query = new Query();
        query.addCriteria(criteria);
        return mongoTemplate.find(query, NodeEntity.class);
    }



    private WorkflowDto workflowEntityToDto(WorkflowEntity workflow){
        List<NodeEntity> entityList = workflow.getNodeList();
        List<NodeDto> collect = entityList.stream().map(entity -> {
            NodeDto dto = new NodeDto();
            BeanUtils.copyProperties(entity, dto);
            dto.setPrevious(entity.getPrevious().getId());
            dto.setNext(entity.getNext().getId());
            dto.setPrevious(entity.getPrevious().getId());
            return dto;
        }).collect(Collectors.toList());
        return WorkflowDto.builder().id(workflow.getId()).currentPosition(workflow.getCurrentPosition()).relatedUsersId(workflow.getRelatedUsersId()).nodeList(collect).build();
    }
}
