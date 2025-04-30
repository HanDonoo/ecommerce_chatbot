package com.small.ecommerce_chatbot.repository;

import com.small.ecommerce_chatbot.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findByOrderId(String orderId);
}
