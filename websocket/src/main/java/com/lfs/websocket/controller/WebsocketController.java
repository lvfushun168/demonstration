package com.lfs.websocket.controller;


import com.lfs.config.model.Result;
import com.lfs.websocket.dto.Message;
import com.lfs.websocket.service.ChatSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.Set;

@RestController
@RequestMapping("/ws")
@Slf4j
public class WebsocketController {

    /**
     * 静态首页
     * @return
     */
    @GetMapping("/index")
    public ModelAndView index(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("forward:/chat.html");
        return modelAndView;
    }


    @GetMapping("/chat/send")
    @ResponseBody
    public Result send(String from, String to, String message){
        ChatSocketServer.SendMessage(new Message(from,to,message,new Date()));
        return Result.success();
    }

    //在线用户列表
    @GetMapping("/getOnlineUser")
    @ResponseBody
    public Set getOnlineUser() {
        Set<String> users = ChatSocketServer.clients.keySet();
        return users;
    }
}
