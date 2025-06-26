package com.example.chat_backend.controller;

import com.example.chat_backend.dto.MessageDto;
import com.example.chat_backend.model.Message;
import com.example.chat_backend.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketChatController {
    private final ChatService chatService;

    @MessageMapping("/chat.send.{roomId}")
    @SendTo("/topic/{roomId}")
    public MessageDto sendMessage(@DestinationVariable Long roomId, MessageDto incoming) {
        Message saved = chatService.sendMessage(roomId,
                // Convert sender username to ID via UserService or JWT principal
                null,
                incoming.getContent());
        return MessageDto.builder()
                .id(saved.getId())
                .roomId(roomId)
                .sender(saved.getSender().getUsername())
                .content(saved.getContent())
                .sentAt(saved.getSentAt())
                .build();
    }
}
