package com.lfs.openai.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 提问记录
 */
@Service
public class MessageLogServiceImpl implements MessageLogService{

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public void saveMessage(WxMpXmlMessage message) {
        mongoTemplate.save(OpenAiLog.builder()
                        .createTime(LocalDateTime.now())
                        .question(message.getContent())
                        .messageType(message.getMsgType())
                        .sender(message.getFromUser())
                        .author(message.getToUser())
                .build());
    }



    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Document(collection="open_ai_log")
    private static class OpenAiLog {
        @Id
        private String id;
        private LocalDateTime createTime;
        /**
         * 提问内容
         */
        private String question;
        /**
         * 消息类型
         */
        private String messageType;
        /**
         * 发送人
         */
        private String sender;
        /**
         * 开发者账号
         */
        private String author;
    }
}
