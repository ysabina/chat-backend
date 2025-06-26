package com.example.chat_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
public class MessageDto {
    private Long id;
    private Long roomId;
    private String sender;
    private String content;
    private Instant sentAt;
}