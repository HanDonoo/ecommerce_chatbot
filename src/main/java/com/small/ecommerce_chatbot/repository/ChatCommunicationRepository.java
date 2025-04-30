package com.small.ecommerce_chatbot.repository;

import com.small.ecommerce_chatbot.entity.ChatCommunication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatCommunicationRepository extends JpaRepository<ChatCommunication, Long> {

    // 查询某用户的未读消息
    List<ChatCommunication> findByToIdAndIsRead(Long toId, Boolean isRead);

    // 按时间排序获取最新消息
    List<ChatCommunication> findTop10ByOrderByTimestampDesc();

    // 按时间排序获取最新消息
    @Query(value = "SELECT * FROM chat_communication c WHERE c.from_id = :fromId OR c.to_id = :fromId ORDER BY c.timestamp ASC", nativeQuery = true)
    List<ChatCommunication> findByFromIdOrderByTimestampAsc(Long fromId);

    // 自定义查询：根据发送人和接收人获取聊天记录
    @Query("SELECT c FROM ChatCommunication c WHERE c.fromId = :fromId AND c.toId = :toId ORDER BY c.timestamp DESC")
    List<ChatCommunication> findChatByFromIdAndToId(@Param("fromId") Long fromId, @Param("toId") Long toId);

    // 自定义查询：根据发送人获取聊天记录
    @Query(value = "SELECT * FROM chat_communication c WHERE c.from_id = :fromId ORDER BY c.timestamp DESC LIMIT 3", nativeQuery = true)
    List<ChatCommunication> findLatestChatCommunications(@Param("fromId") Long fromId);

}
