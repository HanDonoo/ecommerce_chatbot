package com.small.ecommerce_chatbot.service;

import com.small.ecommerce_chatbot.entity.Address;
import com.small.ecommerce_chatbot.entity.Order;
import com.small.ecommerce_chatbot.entity.OrderItem;
import com.small.ecommerce_chatbot.repository.AddressRepository;
import com.small.ecommerce_chatbot.repository.OrderItemRepository;
import com.small.ecommerce_chatbot.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private AddressRepository addressRepository;

    public Order getOrderDetails(String orderId) {
        return Optional.ofNullable(orderRepository.findByOrderId(orderId))
                .map(order -> {
                    // 查询订单项
                    List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getOrderId());
                    order.setItems(orderItems);

                    // 查询账单地址 & 收货地址
                    Address billingAddress = addressRepository.findById(order.getBillingAddressId()).orElse(null);
                    order.setBillingAddress(billingAddress);

                    Address shippingAddress = addressRepository.findById(order.getBillingAddressId()).orElse(null);
                    order.setShippingAddress(shippingAddress);

                    return order;
                }).orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
    }
}


