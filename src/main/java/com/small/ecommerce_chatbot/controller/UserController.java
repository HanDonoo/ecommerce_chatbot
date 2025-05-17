package com.small.ecommerce_chatbot.controller;

import com.small.ecommerce_chatbot.entity.User;
import com.small.ecommerce_chatbot.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {

        User savedUser = userRepository.save(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        userRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return userRepository.findById(id).map(
                existUser -> {
                    existUser.setUsername(user.getUsername());
                    existUser.setEmail(user.getEmail());
                    User updateUser = userRepository.save(existUser);
                    return new ResponseEntity<>(updateUser, HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> patchUser(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return userRepository.findById(id).map(
                user -> {
                    updates.forEach((key,value) ->{
                        switch (key){
                            case "name":
                                user.setUsername((String) value);
                                break;
                            case "email":
                                user.setEmail((String) value);
                                break;
                        }
                    });
                    User updatedUser = userRepository.save(user);
                    return new ResponseEntity<>(updatedUser, HttpStatus.OK);
                }
        ).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
