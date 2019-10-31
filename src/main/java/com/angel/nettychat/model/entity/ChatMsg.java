package com.angel.nettychat.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChatMsg implements Serializable {
    private String senderId;		// 发送者的用户id
    private String receiverId;		// 接受者的用户id
    private String msg;				// 聊天内容
    private String msgId;			// 用于消息的签收
}
