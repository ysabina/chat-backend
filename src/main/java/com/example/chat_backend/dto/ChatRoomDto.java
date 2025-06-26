package com.example.chat_backend.dto;

import com.example.chat_backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class ChatRoomDto {
    private Long id;
    private String name;
    private Instant createdAt;
    private Set<String> participants;
}