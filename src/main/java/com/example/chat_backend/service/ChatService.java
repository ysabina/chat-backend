package com.example.chat_backend.service;

import com.example.chat_backend.model.ChatRoom;
import com.example.chat_backend.model.Message;
import com.example.chat_backend.model.User;
import com.example.chat_backend.repository.ChatRoomRepository;
import com.example.chat_backend.repository.MessageRepository;
import com.example.chat_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    /**
     * Finds (or creates) a private room for two users.
     */
    public ChatRoom getOrCreateRoom(Long a, Long b) {
        String name = Stream.of(a, b)
                .sorted()
                .map(Object::toString)
                .collect(Collectors.joining("_"));
        return chatRoomRepository.findByName(name)
                .orElseGet(() -> {
                    User ua = userRepository.findById(a)
                            .orElseThrow(() -> new IllegalArgumentException("User A not found"));
                    User ub = userRepository.findById(b)
                            .orElseThrow(() -> new IllegalArgumentException("User B not found"));
                    ChatRoom room = ChatRoom.builder().name(name).build();
                    room.getParticipants().add(ua);
                    room.getParticipants().add(ub);
                    return chatRoomRepository.save(room);
                });
    }

    /**
     * Send a message, auto‐creating a 1–1 room if needed.
     */
    public Message sendMessage(Long roomId, Long senderId, Long recipientId, String content) {
        ChatRoom room;
        if (roomId != null && roomId > 0) {
            room = chatRoomRepository.findById(roomId)
                    .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        } else {
            room = getOrCreateRoom(senderId, recipientId);
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));

        Message msg = Message.builder()
                .room(room)
                .sender(sender)
                .content(content)
                .delivered(false)
                .build();
        return messageRepository.save(msg);
    }

    // Legacy overload if you only want to call with a known room:
    public Message sendMessage(Long roomId, Long senderId, String content) {
        return sendMessage(roomId, senderId, null, content);
    }

    @Transactional(readOnly = true)
    public List<Message> getHistory(Long roomId) {
        return messageRepository.findByRoomIdOrderBySentAt(roomId);
    }

    @Transactional(readOnly = true)
    public List<ChatRoom> getUserRooms(Long userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return new ArrayList<>(u.getChatRooms());
    }

    @Transactional
    public Message sendPrivateMessage(Long senderId, Long recipientId, String content) {
        // will create the room if one doesn't exist
        ChatRoom room = getOrCreateRoom(senderId, recipientId);
        // then send into that room
        return sendMessage(room.getId(), senderId, content);
    }


}
