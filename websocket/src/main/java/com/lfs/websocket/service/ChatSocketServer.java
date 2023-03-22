package com.lfs.websocket.service;

import com.alibaba.fastjson.JSON;
import com.lfs.websocket.dto.Message;
import com.lfs.websocket.entity.ChatRecordEntity;
import com.lfs.websocket.repository.ChatRecordRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


@Slf4j
@Component
@ServerEndpoint(value = "/ws/chat")
public class ChatSocketServer {
    public static final AtomicInteger onlineCount = new AtomicInteger(0);
    public static Map<String, Session> clients = new ConcurrentHashMap<>();

    static ChatRecordRepository chatRecordRepository;

    static MongoTemplate loginRecordMongoTemplate;

    @Autowired
    ChatRecordRepository repository;

    @Autowired
    MongoTemplate mongoTemplate;

    private static String roomId;



    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        String username = session.getRequestParameterMap().get("username").stream().findFirst().orElse("");
        log.info("有新的客户端上线 sessionid={} username={}", session.getId(),username);
        clients.put(username,session);
        int cnt = onlineCount.incrementAndGet(); // 在线数加1
        log.info("有连接加入，当前在线人数为：{}", cnt);

        if (StringUtils.isEmpty(roomId)) {
            roomId = UUID.randomUUID().toString();
            log.info("创建新的房间:{}",roomId);
        }
//        CompletableFuture.runAsync(()-> );
        loginRecordMongoTemplate.save(RoomUsersLog.builder().roomId(roomId).usersName(username).createTime(LocalDateTime.now()).build());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        //session的hashcode会变化  不能直接移除
        //clients.remove(session);
        AtomicReference<String> key = new AtomicReference<>("");
        clients.forEach((name,se)->{
            if(se.equals(session)){
                key.set(name);
            }
        });
        String str = key.get();
        clients.remove(str);
        int cnt = onlineCount.decrementAndGet();
        log.info("在线人数为：{}", cnt);

        //如果移除的是最后一个连接，则清除房间id
        if (0==cnt){
            roomId = null;
            log.info("当前房间id已清除");
        }
    }


    /**
     * 出现错误
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误：{}，Session ID： {}",error.getMessage(),session.getId());
        error.printStackTrace();
    }


    /**
     * 发送消息，username为空就群发
     */
    public static void SendMessage(Message message) {
        //username为空就群发
        if(StringUtils.isBlank(message.getTo())){
            clients.forEach((str,session)->{
                if(session.isOpen()){
                    try {
                        session.getBasicRemote().sendText(JSON.toJSONString(message));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else{
            Session session = clients.get(message.getTo());
            if(session!=null && session.isOpen()){
                try {
                    session.getBasicRemote().sendText(JSON.toJSONString(message));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                log.warn("没有找到你指定用户：{}",message.getTo());
            }
        }
        CompletableFuture.runAsync(()-> new ChatSocketServer().messageRecord(message));
    }

    private void messageRecord(Message message){
        chatRecordRepository.insert(ChatRecordEntity.builder()
                        .context(message.getMessage())
                        .name(message.getFrom())
                        .createTime(LocalDateTime.now())
                        .targets(StringUtils.isNotEmpty(message.getTo())?message.getTo() : StringUtils.join(new ArrayList<>(clients.keySet()),","))
                        .build());
    }


    @PostConstruct
    private void initAutowired(){
        chatRecordRepository =  repository;
        loginRecordMongoTemplate = mongoTemplate;
    }


    /**
     * 记录登录者信息
     * 包括是否同一房间，是否在线，是否被踢出等
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Document(collection="chatroom_login_log")
    private static class RoomUsersLog{
        @Id
        private String id;
        private String roomId;
        private String usersName;
        private LocalDateTime createTime;

    }
}
