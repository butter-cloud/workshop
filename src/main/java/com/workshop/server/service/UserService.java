package com.workshop.server.service;

import com.workshop.server.controller.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public boolean registerUser(String nickname, String role, String sessionId) {
        log.info("[UserService] Registering user with nickname: {}", nickname);

        try {
//            String id = UUID.randomUUID().toString();
//            log.info("[UserService] Created User ID: {}", id);

            String status = "supporter".equals(role) ? "idle" : "requesting";

            Map<String, String> userInfo = Map.of(
                    "name", nickname,
                    "id", sessionId,
                    "role", role,
                    "status", status
            );

            redisTemplate.opsForHash().putAll("user:" + nickname, userInfo);    // ex) user:jay, {}

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 사용자 조회
    public Map<Object, Object> getUser(String nickname) {
        return redisTemplate.opsForHash().entries("user:" + nickname);
    }

    // 사용자 상태 업데이트
    public void updateUserStatus(String nickname, String status) {
        redisTemplate.opsForHash().put("user:" + nickname, "status", status);
    }
}
