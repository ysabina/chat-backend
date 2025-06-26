package com.example.chat_backend.service;

import com.example.chat_backend.model.User;
import com.example.chat_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register a new user with encoded password.
     * @throws IllegalArgumentException if username exists
     */
    @Transactional
    public User register(String username, String rawPassword) {
        userRepository.findByUsername(username).ifPresent(u -> {
            throw new IllegalArgumentException("Username already taken");
        });
        String encoded = passwordEncoder.encode(rawPassword);
        User user = User.builder()
                .username(username)
                .password(encoded)
                .online(false)
                .build();
        return userRepository.save(user);
    }

    /**
     * Retrieves active usernames.
     */
    @Transactional(readOnly = true)
    public List<String> getActiveUsernames() {
        return userRepository.findByOnlineTrue()
                .stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
    }

    /**
     * Sets user's online status.
     */
    @Transactional
    public void setOnlineStatus(Long userId, boolean online) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setOnline(online);
            userRepository.save(user);
        });
    }

    /**
     * Loads UserDetails for Spring Security.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities("USER")
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
