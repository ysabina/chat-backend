package com.example.chat_backend.controller;

import com.example.chat_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/active")
    public List<String> activeUsers() {
        return userService.getActiveUsernames();
    }
}