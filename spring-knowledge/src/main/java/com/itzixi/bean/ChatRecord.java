package com.itzixi.bean;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @ClassName ChatRecord 数据库表所对应的持久层对象
 * @Author 陈壮壮
 * @Version 1.0
 * @Description ChatRecord
 **/
@Data
@ToString
public class ChatRecord {

    private String id;
    private String content;
    private String chatType;
    private LocalDateTime chatTime;
    private String familyMember;

}
