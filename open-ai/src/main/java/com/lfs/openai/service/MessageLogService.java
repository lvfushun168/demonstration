package com.lfs.openai.service;

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;

/**
 * 提问记录
 */
public interface MessageLogService {

    /**
     * 存储记录
     * @param message
     */
    void saveMessage(WxMpXmlMessage message);
}
