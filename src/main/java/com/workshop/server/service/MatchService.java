package com.workshop.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class MatchService {

    private static final Logger log = LoggerFactory.getLogger(MatchService.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void noticeIdleSupporter() {

        Set<String> allSupporters = redisTemplate.opsForSet().members("idle_supporters");

        if (allSupporters == null || allSupporters.isEmpty()) {
            log.info("[NoticeService] No supporters found.");
        } else {
            log.info("[NoticeService] All supporters: {}", allSupporters);
        }

        // allSupporters 에게 알림
    }

    public String matchSupporter(String supporterId, String clientId) {
        boolean isMember = redisTemplate.opsForSet().isMember("request_clients", clientId);
        log.info("[MatchService] Checking if client with clientId {} is a member of request_clients", clientId);

        getCurrentSets();

        if (isMember) {
            log.info("[MatchService] Client with clientId {} found", clientId);

            // client는 request clients 에서 제거
            redisTemplate.opsForSet().remove("request_clients", clientId);

            // supporter는 idle_supporters 에서 제거
            redisTemplate.opsForSet().remove("idle_supporters", supporterId);

            getCurrentSets();
            String roomId = createRoom(supporterId, clientId);

            log.info("[MatchService] Matched supporter with supporterId {} and client with clientId {} in room {}", supporterId, clientId, roomId);

            return roomId;
        } else {
            log.info("[MatchService] Client with clientId {} not found", clientId);
            return null;
        }
    }

    public void getCurrentSets() {
        Set<String> idleSupporters = redisTemplate.opsForSet().members("idle_supporters");
        Set<String> requestClients = redisTemplate.opsForSet().members("request_clients");

        log.info("[MatchService] Current idle_supporters: {}", idleSupporters);
        log.info("[MatchService] Current request_clients: {}", requestClients);
    }

    public String createRoom(String supporterId, String clientId) {
        log.info("[MatchService] Creating room for supporter with supporterId {} and clientId {}", supporterId, clientId);

        String roomId = redisTemplate.opsForValue().increment("room:id", 1).toString();
        String roomKey = "room:" + roomId;

        redisTemplate.opsForHash().put(roomKey, "supporterId", supporterId);
        redisTemplate.opsForHash().put(roomKey, "clientId", clientId);

        log.info("[MatchService] Room created with roomId {}", roomId);

        return roomId;
    }

}
