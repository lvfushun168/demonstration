package com.lfs.openai.controller;

import com.lfs.openai.service.ChatGptServiceImpl;
import com.lfs.openai.service.MessageLogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * 微信事件推送及被动回复消息
 *
 * @author lfs
 */
@Slf4j
@RestController
@RequestMapping("lfs/response")
@AllArgsConstructor
public class WxMpMsgController {
    private ChatGptServiceImpl chatGptService;
    private RedisTemplate<String,String> redisTemplate;
    private MessageLogService messageLogService;

    /**
     * 被动回复用户消息
     *
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping
    public String post(HttpServletRequest request) throws IOException, WxErrorException {

        //获取消息流,并解析xml
        WxMpXmlMessage message = WxMpXmlMessage.fromXml(request.getInputStream());
        log.info(message.toString());
        //消息类型
        String messageType = message.getMsgType();
        log.info("消息类型:" + messageType);
        //发送者帐号
        String fromUser = message.getFromUser();
        log.info("发送者账号：" + fromUser);
        //开发者微信号
        String touser = message.getToUser();
        log.info("开发者微信：" + touser);
        //文本消息  文本内容
        String text = message.getContent();
        log.info("文本消息：" + text);
        // 事件推送
        if (messageType.equals("event")) {
            log.info("event：" + message.getEvent());
            // 关注
            if (message.getEvent().equals("subscribe")) {
                log.info("用户关注：{}", fromUser);
                WxMpXmlOutTextMessage texts = WxMpXmlOutTextMessage
                        .TEXT()
                        .toUser(fromUser)
                        .fromUser(touser)
                        .content("我是LFS小助手,你的私人医生/金融分析师/数学老师/导游/翻译官/IT专家/百科全书")
                        .build();
                String result = texts.toXml();
                log.info("响应给用户的消息：" + result);
                return result;
            }
            // 取消关注
            if (message.getEvent().equals("unsubscribe")) {
                log.info("用户取消关注：{}", fromUser);
            }
            // 点击菜单
            if (message.getEvent().equals("CLICK")) {
                log.info("用户点击菜单：{}", message.getEventKey());
            }
            // 点击菜单
            if (message.getEvent().equals("VIEW")) {
                log.info("用户点击菜单：{}", message.getEventKey());
            }
            // 已关注用户扫描带参数二维码
            if (message.getEvent().equals("scancode_waitmsg")) {
                log.info("用户扫描二维码：{}", fromUser);
            }
            // 获取位置信息
            if (message.getEvent().equals("LOCATION")) {
                log.info("用户发送位置信息：经度：{}，纬度：{}", message.getLatitude(), message.getLongitude());
            }
            return null;
        }
        //文本消息
        if (messageType.equals("text")) {
            String reply= "内容较多,LFS正在整理，请在1分钟后回复【继续】以继续接收后续消息～";
            if (text.equals("继续")) {
                String answer = redisTemplate.opsForValue().get(fromUser);
                if (StringUtils.isNotEmpty(answer)) {
                    reply = answer;
                }else {
                    reply = "LFS小助手正在思考，请稍候。";
                }
            }else {
                //保存日志
                CompletableFuture.runAsync(()->messageLogService.saveMessage(message));
                CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> chatGptService.reply(text, fromUser));
                try {
                    reply = future.get(3, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    log.error("处理中断....");
                } catch (ExecutionException e) {
                    log.error("处理失败....");
                } catch (TimeoutException e) {
                    log.error("处理超时....");
                }
            }
            WxMpXmlOutTextMessage texts = WxMpXmlOutTextMessage
                    .TEXT()
                    .toUser(fromUser)
                    .fromUser(touser)
                    .content(reply)
                    .build();
            String result = texts.toXml();
            log.info("响应给用户的消息：" + result);
            redisTemplate.delete(fromUser);
            return result;
        }
        //图片消息
        if (messageType.equals("image")) {
            WxMpXmlOutTextMessage texts = WxMpXmlOutTextMessage
                    .TEXT()
                    .toUser(fromUser)
                    .fromUser(touser)
                    .content("已接收到您发的图片信息")
                    .build();
            String result = texts.toXml();
            result.replace("你发送的消息为： ", "");
            log.info("响应给用户的消息：" + result);
            return result;
        }
        /**
         * 语音消息
         */
        if (messageType.equals("voice")) {
            WxMpXmlOutTextMessage texts = WxMpXmlOutTextMessage
                    .TEXT()
                    .toUser(fromUser)
                    .fromUser(touser)
                    .content("已接收到您发的语音信息")
                    .build();
            String result = texts.toXml();
            log.info("响应给用户的消息：" + result);
            return result;
        }
        /**
         * 视频
         */
        if (messageType.equals("video")) {
            WxMpXmlOutTextMessage texts = WxMpXmlOutTextMessage
                    .TEXT()
                    .toUser(fromUser)
                    .fromUser(touser)
                    .content("已接收到您发的视频信息")
                    .build();
            String result = texts.toXml();
            log.info("响应给用户的消息：" + result);
            return result;
        }
        /**
         * 小视频
         */
        if (messageType.equals("shortvideo")) {
            WxMpXmlOutTextMessage texts = WxMpXmlOutTextMessage
                    .TEXT()
                    .toUser(fromUser)
                    .fromUser(touser)
                    .content("已接收到您发的小视频信息")
                    .build();
            String result = texts.toXml();
            log.info("响应给用户的消息：" + result);
            return result;
        }
        /**
         * 地理位置信息
         */
        if (messageType.equals("location")) {
            WxMpXmlOutTextMessage texts = WxMpXmlOutTextMessage
                    .TEXT()
                    .toUser(fromUser)
                    .fromUser(touser)
                    .content("已接收到您发的地理位置信息")
                    .build();
            String result = texts.toXml();
            log.info("响应给用户的消息：" + result);
            return result;
        }
        /**
         * 链接信息
         */
        if (messageType.equals("link")) {
            WxMpXmlOutTextMessage texts = WxMpXmlOutTextMessage
                    .TEXT()
                    .toUser(fromUser)
                    .fromUser(touser)
                    .content("已接收到您发的链接信息")
                    .build();
            String result = texts.toXml();
            log.info("响应给用户的消息：" + result);
            return result;
        }
        return null;
    }

//    public void kefuMessage(String toUser, String content) throws WxErrorException {
//        WxMpKefuMessage message = new WxMpKefuMessage();
//        message.setToUser(toUser);
//        message.setMsgType("text");
//        message.setContent(content);
//
//        wxMpService.getKefuService().sendKefuMessage(message);
//    }

}
