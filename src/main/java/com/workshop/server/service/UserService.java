package com.workshop.server.service;

import com.workshop.server.exception.UserAlreadyExistsException;
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

    @Autowired
    private MatchService noticeService;

    public String registerUser(String userName, String role) {
        log.info("[UserService] Registering user with name: {}", userName);

        String userId = UUID.randomUUID().toString();
        String redisKey = "user:" + userId;
        String userNamesKey = "userNames";
        log.info("[UserService] Registering user with redisKey: {}", redisKey);

        // 닉네임이 사용중인지 확인
        if (redisTemplate.opsForSet().isMember(userNamesKey, userName)) {
            log.info("[UserService] User with userName {} already exists", userName);
            throw new UserAlreadyExistsException("User with userName " + userName + " already exists.");
        }

        Map<String, String> userInfo = Map.of(
                "userId", userId,
                "userName", userName,
                "role", role
        );

        redisTemplate.opsForHash().putAll(redisKey, userInfo);
        redisTemplate.opsForSet().add(userNamesKey, userName);

        if (role.equals("supporter")) {
            log.info("[UserService] Adding supporter with userId {} to idle_supporters", userId);
            redisTemplate.opsForSet().add("idle_supporters", userId);
        } else if (role.equals("client")) {
            noticeService.noticeIdleSupporter();
            redisTemplate.opsForSet().add("request_clients", userId);
        } else {
            log.warn("[UserService] Invalid role: {}", role);
            throw new IllegalArgumentException("Invalid role: " + role);
        }

        return userId;
    }

    // 사용자 조회
    public Map<Object, Object> getUser(String userId) {
        String redisKey = "user:" + userId;
        log.info("[UserService] Getting user with key: {}", redisKey);

        if (!redisTemplate.hasKey(redisKey)) {
            log.warn("[UserService] User with userId {} does not exist", userId);
            throw new UserAlreadyExistsException("User with userId " + userId + " does not exist.");
        }
        return redisTemplate.opsForHash().entries(redisKey);
    }

    // 사용자 nickname 조회
    public String getUserName(String userId) {
        String redisKey = "user:" + userId;
        return (String) redisTemplate.opsForHash().get("user:" + userId, "userName");
    }
}
