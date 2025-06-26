
package com.example.chat_backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Payload for sending a new message.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMessageRequest {
    private Long senderId;
    private Long recipientId;
    private String content;
}
