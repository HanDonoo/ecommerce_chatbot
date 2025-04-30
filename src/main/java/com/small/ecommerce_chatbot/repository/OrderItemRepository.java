package com.small.ecommerce_chatbot.repository;

import com.small.ecommerce_chatbot.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(String orderId);
}
