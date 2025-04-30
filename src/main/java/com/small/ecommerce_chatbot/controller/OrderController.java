package com.small.ecommerce_chatbot.controller;

import com.small.ecommerce_chatbot.entity.Order;
import com.small.ecommerce_chatbot.response.Response;
import com.small.ecommerce_chatbot.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/{orderId}")
    public Response<Order> getOrderDetail(@PathVariable String orderId) {
        Order order = orderService.getOrderDetails(orderId);
        return Response.success(order);
    }
}
