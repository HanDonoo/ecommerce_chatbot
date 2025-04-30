package com.small.ecommerce_chatbot.controller;

import com.small.ecommerce_chatbot.entity.ChatCommunication;
import com.small.ecommerce_chatbot.response.Response;
import com.small.ecommerce_chatbot.service.ChatCommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatCommunicationController {

    @Autowired
    private ChatCommunicationService chatCommunicationService;

    // 获取用户的未读消息
    @GetMapping("/unread/{toId}")
    public List<ChatCommunication> getUnreadMessages(@PathVariable Long toId) {
        return chatCommunicationService.getUnreadMessages(toId);
    }

    // 获取最新聊天记录
    @GetMapping("/latest")
    public List<ChatCommunication> getLatestMessages() {
        return chatCommunicationService.getLatestMessages();
    }

    // 获取两个人之间的聊天记录
    @GetMapping("/between")
    public List<ChatCommunication> getChatBetweenUsers(@RequestParam Long fromId, @RequestParam Long toId) {
        return chatCommunicationService.getChatBetweenUsers(fromId, toId);
    }

    @GetMapping("/history")
    public Response<List<ChatCommunication>> getChatHistory(@RequestParam Long userId) {
        List<ChatCommunication> chatHistory = chatCommunicationService.findLatestChatCommunications(userId);
        return Response.success(chatHistory);
    }

}
