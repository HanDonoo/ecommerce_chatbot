package com.small.ecommerce_chatbot.controller;

import com.alibaba.fastjson.JSON;
import com.small.ecommerce_chatbot.entity.User;
import com.small.ecommerce_chatbot.response.Response;
import com.small.ecommerce_chatbot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Response<Boolean> registerUser(@Valid @RequestBody User user) {

        User registeredUser = userService.registerUser(user.getEmail(), user.getPassword());

        if(Objects.nonNull(registeredUser)){
            return Response.success(Boolean.TRUE);
        }
        return Response.failure(500,"register error");
    }


    @PostMapping("/login")
    public Response<Long> login(@RequestBody User loginUser) {
        String email = loginUser.getEmail();
        String password = loginUser.getPassword();

        try {
            User user = userService.login(email, password);
            log.info("登录用户信息:{}", JSON.toJSONString(loginUser));
            return Response.success(user.getId());
        } catch (IllegalArgumentException e) {
            return Response.failure(500,"login error");
        }
    }
}
