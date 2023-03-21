package com.lfs.openai.controller;

import com.lfs.authentication.shiro.api.AuthApi;
import com.lfs.openai.service.ChatGptServiceImpl;
import com.lfs.openai.service.MessageLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@Slf4j
public class test {

    @Resource
    private ChatGptServiceImpl chatGptService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    MessageLogService messageLogService;


    @Autowired
    AuthApi authApi;
    
    
    @GetMapping("/lfs/gpt/test/{text}")
    public String main(@PathVariable String text) {
        String reply= "内容较多，请在1分钟后回复【继续】以继续接收后续消息";
        if (text.equals("继续")) {
            String answer = redisTemplate.opsForValue().get("15923041345");
//            String flag = redisTemplate.opsForValue().get("15923041345-1");
            if (StringUtils.isNotEmpty(answer)) {
                reply = answer;
                redisTemplate.delete("15923041345");
            }else {
//                if (Objects.equals(flag, "正在写入")){
                    reply = "LFS小助手正在思考，请稍候。";
//                }else if (Objects.equals(flag, "写入完毕")){
//                    reply = "上次会话时间过长，请重新输入问题。";
//                }
            }
        }else {
            redisTemplate.opsForValue().set("15923041345-1", "正在写入",600,TimeUnit.SECONDS);
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> chatGptService.reply(text, "15923041345"));
//                    .whenComplete((res, exception) -> redisTemplate.opsForValue().set("15923041345-1", "写入完毕",3600,TimeUnit.SECONDS));
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
        return reply;
    }


    /**
     * just feign test
     * @return
     */
    @GetMapping("/testApi")
    public String testApi(){
        return authApi.test();
    }
}
