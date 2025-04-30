package com.small.ecommerce_chatbot.service;

import com.small.ecommerce_chatbot.entity.ChatCommunication;
import com.small.ecommerce_chatbot.repository.ChatCommunicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatCommunicationService {

    @Autowired
    private ChatCommunicationRepository chatCommunicationRepository;

    public ChatCommunicationService(ChatCommunicationRepository chatCommunicationRepository) {
        this.chatCommunicationRepository = chatCommunicationRepository;
    }

    // 获取用户的未读消息
    public List<ChatCommunication> getUnreadMessages(Long toId) {
        return chatCommunicationRepository.findByToIdAndIsRead(toId, false);
    }

    // 获取最新的聊天记录
    public List<ChatCommunication> getLatestMessages() {
        return chatCommunicationRepository.findTop10ByOrderByTimestampDesc();
    }

    // 获取两个人之间的聊天记录
    public List<ChatCommunication> getChatBetweenUsers(Long fromId, Long toId) {
        return chatCommunicationRepository.findChatByFromIdAndToId(fromId, toId);
    }

    // 获取最新的聊天记录
    public List<ChatCommunication> findLatestChatCommunications(Long fromId) {
        return chatCommunicationRepository.findByFromIdOrderByTimestampAsc(fromId);
    }
}
