package com.small.ecommerce_chatbot.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserTest {

    public static void main(String[] args) {

        User user = new User.Builder()
                .withName("Kevin")
                .withAge(19)
                .withEmail("aa@aa.com")
                .build();
        ExecutorService poolExecutor = Executors.newSingleThreadExecutor();


    }
}
