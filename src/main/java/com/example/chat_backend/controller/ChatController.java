package com.example.chat_backend.controller;

import com.example.chat_backend.dto.ChatRoomDto;
import com.example.chat_backend.dto.CreateMessageRequest;
import com.example.chat_backend.dto.MessageDto;
import com.example.chat_backend.dto.PrivateRoomRequest;
import com.example.chat_backend.model.User;
import com.example.chat_backend.service.ChatService;
import com.example.chat_backend.model.ChatRoom;
import com.example.chat_backend.model.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/rooms/{userId}")
    public List<ChatRoomDto> getUserRooms(@PathVariable Long userId) {
        return chatService.getUserRooms(userId)
                .stream()
                .map(room -> ChatRoomDto.builder()
                        .id(room.getId())
                        .name(room.getName())
                        .createdAt(room.getCreatedAt())
                        .participants(
                                room.getParticipants().stream()
                                        .map(p -> p.getUsername())
                                        .collect(Collectors.toSet())
                        )
                        .build()
                )
                .collect(Collectors.toList());
    }

    @GetMapping("/rooms/{roomId}/messages")
    public List<MessageDto> getRoomHistory(@PathVariable Long roomId) {
        return chatService.getHistory(roomId)
                .stream()
                .map(msg -> MessageDto.builder()
                        .id(msg.getId())
                        .roomId(msg.getRoom().getId())
                        .sender(msg.getSender().getUsername())
                        .content(msg.getContent())
                        .sentAt(msg.getSentAt())
                        .build()
                )
                .collect(Collectors.toList());
    }

    @PostMapping("/rooms/{roomId}/messages")
    public MessageDto sendMessage(
            @PathVariable Long roomId,
            @RequestParam Long senderId,
            @RequestParam String content
    ) {
        Message msg = chatService.sendMessage(roomId, senderId, content);
        return MessageDto.builder()
                .id(msg.getId())
                .roomId(roomId)
                .sender(msg.getSender().getUsername())
                .content(msg.getContent())
                .sentAt(msg.getSentAt())
                .build();
    }

    @PostMapping("/private")
    public MessageDto sendPrivateMessage(@RequestBody CreateMessageRequest req) {
        Message msg = chatService.sendPrivateMessage(
                req.getSenderId(),
                req.getRecipientId(),
                req.getContent()
        );
        return MessageDto.builder()
                .id(msg.getId())
                .roomId(msg.getRoom().getId())
                .sender(msg.getSender().getUsername())
                .content(msg.getContent())
                .sentAt(msg.getSentAt())
                .build();
    }


}
