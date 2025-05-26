package com.small.ecommerce_chatbot.service;

import org.junit.Test;

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

    @Test
    public void test(){
        User user1 = new User.Builder()
                .withName("Kevin")
                .withAge(19)
                .withEmail("aa@aa.com")
                .build();
        ExecutorService poolExecutor1 = Executors.newSingleThreadExecutor();
    }
}
