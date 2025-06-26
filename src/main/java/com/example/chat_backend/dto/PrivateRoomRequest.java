package com.example.chat_backend.dto;

import lombok.Data;

@Data
public class PrivateRoomRequest {
    private Long userAId;
    private Long userBId;
}
