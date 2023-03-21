package com.lfs.config.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

public interface SendEmail {

    void send(String address,String subject,String content);


    @Service
    class SendEmailFrom126 implements SendEmail {
        @Value("${spring.mail.username}")
        private String username;

        private final JavaMailSender sender;

        public SendEmailFrom126(JavaMailSender sender) {
            this.sender = sender;
        }

        @Override
        public void send(String address, String subject, String content) {
            // 创建简单邮件消息对象
            SimpleMailMessage message = new SimpleMailMessage();
            // 设置发送者邮箱
            message.setFrom(username);
            // 设置接收者邮箱
            message.setTo(address);
            // 设置主题
            message.setSubject(subject);
            // 设置文本内容
            message.setText(content);
            // 发送邮件
            sender.send(message);

        }
    }
}
