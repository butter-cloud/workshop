package com.workshop.server.service;

import com.workshop.server.exception.UserAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public boolean registerUser(String nickname, String role, String sessionId) {
        log.info("[UserService] Registering user with nickname: {}", nickname);

        String status = "supporter".equals(role) ? "idle" : "requesting";
        String redisKey = "user:" + nickname;

        log.info("[UserService] Registering user with redisKey: {}", redisKey);

        Map<String, String> userInfo = Map.of(
                "nickname", nickname,
                "id", sessionId,
                "role", role,
                "status", status
        );

        if (redisTemplate.opsForHash().hasKey("user:" + nickname, "nickname")) {
            log.info("[UserService] User with nickname {} already exists", nickname);
            throw new UserAlreadyExistsException("User with nickname " + nickname + " already exists.");
        } else {
            redisTemplate.opsForHash().putAll(redisKey, userInfo);
            return true;
        }
    }

    // 모든 사용자 조회
    public Map<Object, Object> getAllUsers() {
        return redisTemplate.opsForHash().entries("user:");
    }

    // 사용자 조회
    public Map<Object, Object> getUser(String nickname) {
        return redisTemplate.opsForHash().entries("user:" + nickname);
    }

    // 사용자 상태 업데이트
    public void updateUserStatus(String sessionId, String status) {
        redisTemplate.opsForHash().put("user:" + sessionId, "status", status);
    }
}
