package com.small.ecommerce_chatbot.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.small.ecommerce_chatbot.controller.OllamaOkHttpClient;
import com.small.ecommerce_chatbot.entity.ChatCommunication;
import com.small.ecommerce_chatbot.entity.Order;
import com.small.ecommerce_chatbot.entity.User;
import com.small.ecommerce_chatbot.processor.NLPProcessor;
import com.small.ecommerce_chatbot.repository.ChatCommunicationRepository;
import com.small.ecommerce_chatbot.repository.UserRepository;
import com.small.ecommerce_chatbot.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Autowired
    private NLPProcessor nlpProcessor;

    @Autowired
    private ChatCommunicationRepository chatCommunicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderService orderService;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            String payload = message.getPayload();
            Long userId = Long.parseLong(session.getUri().getQuery().split("=")[1]);
            log.info("message is:{}", JSON.toJSONString(message));
            // 解析用户消息
            Map<String, String> messageData = JSON.parseObject(payload, new TypeReference<Map<String, String>>() {});
            String userMessageText = messageData.get("text");

            // 保存用户消息到数据库
            User user = userRepository.findById(userId).orElse(null);
            ChatCommunication userMessage = new ChatCommunication();
            userMessage.setFromId(userId);
            userMessage.setFromName(user.getUsername());
            userMessage.setToId(1L);
            userMessage.setToName("System");
            userMessage.setContent(userMessageText);
            chatCommunicationRepository.save(userMessage);

            // 查询最近的聊天历史
            List<ChatCommunication> chatHistory = chatCommunicationRepository.findLatestChatCommunications(userId);
            String chatContext = buildChatContext(chatHistory);

            // 使用 CoreNLP 分析用户输入
            List<String> analysisResults = nlpProcessor.analyzeText(userMessageText);

            // 生成系统回复
            String systemReply = generateSystemReply(userMessageText, chatContext, analysisResults);

            // 保存系统回复到数据库
            ChatCommunication systemMessage = new ChatCommunication();
            systemMessage.setFromId(1L);
            systemMessage.setFromName("System");
            systemMessage.setToId(userId);
            systemMessage.setToName(user.getUsername());
            systemMessage.setContent(systemReply);
            chatCommunicationRepository.save(systemMessage);

            // 构造 JSON 回复
            Map<String, String> responseData = new HashMap<>();
            responseData.put("message", systemReply);
            String jsonResponse = JSON.toJSONString(responseData);

            // 发送消息到前端
            session.sendMessage(new TextMessage(jsonResponse));
        } catch (Exception e) {
            System.err.println("Error handling message: " + e.getMessage());
        }
    }

    private String generateSystemReply(String userMessageText, String chatContext, List<String> analysisResults) {
        // 使用分析结果进行回复增强
        String intent = classifyIntent(userMessageText, chatContext);

        switch (intent) {
            case "ORDER_QUERY":
                return "It seems you're asking about an order. Can you provide your order number?";
            case "LOGISTICS_QUERY":
                return "It seems you're asking about shipment tracking. Please provide your tracking number.";
            case "PROVIDE_ORDER_NUMBER":
                Order order = queryOrderByOrderNumber(userMessageText);
                if (Objects.nonNull(order)) {
                    return "Thank you for providing your order number. Please click the link query your order. http://localhost:3000/order/" + order.getOrderId();
                }
                return "Sorry, we can't find the order. Please provide the correct order number";
            case "NEW_PRODUCT":
                return "✨ New arrivals are here! Click the link below to explore and be amazed! 😊. http://localhost:3000/new-product";
            case "POLICIES":
                return "Hey there! We’ve got all the details about returns, shipping, payments, and warranty right here: http://localhost:3000/policies Need more help? Just ask! 😊";
            case "PROVIDE_TRACKING_NUMBER":
                String trackingNumber = extractTrackingNumber(userMessageText);
                return "Thank you for providing your tracking number " + trackingNumber + ". Let me check the shipment status for you...";
            default:
                return OllamaOkHttpClient.queryOllama(userMessageText);
        }
    }

    private String buildDynamicReply(String userMessageText, List<String> analysisResults) {
        StringBuilder responseBuilder = new StringBuilder("Here's what I understood from your message:\n\n");

        // 提取情感分析结果
        String sentiment = analysisResults.stream()
                .filter(result -> result.startsWith("Sentiment:"))
                .map(result -> result.replace("Sentiment:", "").trim())
                .findFirst()
                .orElse("Neutral");

        // 提取命名实体识别结果
        List<String> entities = analysisResults.stream()
                .filter(result -> result.startsWith("Word:"))
                .filter(result -> result.contains("NER:"))
                .map(result -> result.split(",")[0].replace("Word:", "").trim()).collect(Collectors.toList());

        // 根据情感调整回复语气
        if ("Positive".equalsIgnoreCase(sentiment)) {
            responseBuilder.append("You seem happy! 😊 ");
        } else if ("Negative".equalsIgnoreCase(sentiment)) {
            responseBuilder.append("I'm sorry to hear that. Let me try to help. 😔 ");
        } else {
            responseBuilder.append("Let me help with your query. ");
        }

        // 添加提取的实体信息
        if (!entities.isEmpty()) {
            responseBuilder.append("I noticed you mentioned: ");
            responseBuilder.append(String.join(", ", entities)).append(". ");
        }

        // 提供通用的帮助提示
        responseBuilder.append("\n\nIt seems I may need more information to assist you. Could you clarify or provide additional details?");

        // 如果没有特定的分析结果，补充默认信息
        if (entities.isEmpty() && sentiment.equalsIgnoreCase("Neutral")) {
            responseBuilder.append(" I'm here to assist with orders, shipping, discounts, or product recommendations.");
        }

        return responseBuilder.toString();
    }

    private String classifyIntent(String userInput, String chatContext) {
        userInput = userInput.toLowerCase();

        // 优先判断是否有订单号和物流单号提供的上下文
        if (chatContext.toLowerCase().contains("order") && userInput.matches(".*\\d+.*")) {
            return "PROVIDE_ORDER_NUMBER";
        } else if (chatContext.toLowerCase().contains("logistics") && userInput.matches(".*\\d{8,}.*")) {
            return "PROVIDE_TRACKING_NUMBER";
        } else if (chatContext.toLowerCase().contains("product") && userInput.toLowerCase().contains("new") && !userInput.toLowerCase().contains("police")) {
            return "NEW_PRODUCT";
        } else if (chatContext.toLowerCase().contains("polices") && userInput.toLowerCase().contains("new")) {
            return "POLICIES";
        }

        // 默认意图匹配
        if (userInput.contains("order") || userInput.contains("status")) {
            return "ORDER_QUERY";
        } else if (userInput.contains("track") || userInput.contains("shipment")) {
            return "LOGISTICS_QUERY";
        } else if (userInput.contains("discount") || userInput.contains("offer")) {
            return "DISCOUNT_QUERY";
        } else if (userInput.contains("new") || userInput.contains("recommend")) {
            return "NEW_PRODUCT_RECOMMENDATION";
        } else if (userInput.contains("new") || userInput.contains("new product")) {
            return "NEW_PRODUCT";
        } else if (userInput.contains("policies") || userInput.contains("policie")) {
            return "POLICIES";
        } else {
            return "UNKNOWN";
        }
    }

    private Order queryOrderByOrderNumber(String input) {
        String orderNumber = input.replaceAll("\\D+", "");

        Order order = orderService.getOrderDetails(orderNumber);

        return order;
    }

    private String extractTrackingNumber(String input) {
        return input.replaceAll("\\D+", ""); // 提取纯数字部分作为物流单号
    }

    private String fetchOrderStatus(String orderNumber) {
        return "Your order " + orderNumber + " is currently being prepared and will be shipped soon.";
    }

    private String fetchShipmentStatus(String trackingNumber) {
        return "Your shipment " + trackingNumber + " is currently in transit and will be delivered tomorrow.";
    }

    private String buildChatContext(List<ChatCommunication> chatHistory) {
        StringBuilder contextBuilder = new StringBuilder();
        for (ChatCommunication chat : chatHistory) {
            contextBuilder.append(chat.getFromName()).append(": ").append(chat.getContent()).append("\n");
        }
        return contextBuilder.toString();
    }

    private void saveUserMessage(Long userId, String username, String content) {
        ChatCommunication userMessage = new ChatCommunication();
        userMessage.setFromId(userId);
        userMessage.setFromName(username);
        userMessage.setToId(1L); // 系统ID
        userMessage.setToName("System");
        userMessage.setContent(content);
        userMessage.setRead(Boolean.TRUE);
        chatCommunicationRepository.save(userMessage);
    }

    private void saveSystemMessage(Long userId, String username, String content) {
        ChatCommunication systemMessage = new ChatCommunication();
        systemMessage.setFromId(1L); // 系统ID
        systemMessage.setFromName("System");
        systemMessage.setToId(userId);
        systemMessage.setToName(username);
        systemMessage.setContent(content);
        systemMessage.setRead(Boolean.TRUE);
        chatCommunicationRepository.save(systemMessage);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("User disconnected: ");
        String userId = session.getUri().getQuery().split("=")[1];
        sessions.remove(userId);
        System.out.println("User disconnected: " + userId);
    }
}
