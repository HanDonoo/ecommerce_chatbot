package com.small.ecommerce_chatbot.service;


import com.small.ecommerce_chatbot.entity.User;
import com.small.ecommerce_chatbot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(email);
        user.setEmail(email);
        user.setPassword(password);

        return userRepository.save(user);
    }

    public User login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // 验证密码
            if (password.equals(user.getPassword())) {
                return user;
            } else {
                throw new IllegalArgumentException("Invalid password!");
            }
        } else {
            throw new IllegalArgumentException("User not found!");
        }
    }

}
