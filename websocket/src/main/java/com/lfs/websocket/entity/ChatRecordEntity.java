package com.lfs.websocket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@TableName(value = "chat_record")
public class ChatRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 发起聊天的人
     */
    private String name;

    /**
     * 接收聊天的人（群发逗号拼接字符串）
     */
    private String targets;

    private String context;

    private LocalDateTime createTime;
}
