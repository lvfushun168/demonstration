package com.lfs.websocket.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lfs.websocket.entity.ChatRecordEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRecordRepository extends BaseMapper<ChatRecordEntity> {
}
