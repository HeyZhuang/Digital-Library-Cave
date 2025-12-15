package com.itzixi.service;

import com.itzixi.bean.ChatRecord;
import com.itzixi.utils.ChatTypeEnum;

import java.util.List;

public interface ChatRecordService {

    /**
     * @Description: 保存用户和AI的聊天记录
     * @Author 陈壮壮
     * @param userName
     * @param message
     * @param chatType
     */
    public void saveChatRecord(String userName, String message, ChatTypeEnum chatType);

    /**
     * @Description: 查询用户和AI的历史聊天记录
     * @Author 陈壮壮
     * @param userName
     * @return List<ChatRecord>
     */
    public List<ChatRecord> getChatRecordList(String userName);

}
