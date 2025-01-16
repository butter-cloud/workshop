package com.workshop.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.chime.ChimeClient;
import software.amazon.awssdk.services.chime.model.CreateAttendeeRequest;
import software.amazon.awssdk.services.chime.model.CreateAttendeeResponse;
import software.amazon.awssdk.services.chime.model.CreateMeetingRequest;
import software.amazon.awssdk.services.chime.model.CreateMeetingResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class MeetingController {

    @Value("${aws.region}")
    private String region;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final ChimeClient chimeClient;

    public MeetingController(ChimeClient chimeClient) {
        this.chimeClient = chimeClient;
    }

    @RequestMapping("/meeting")
    public ResponseEntity<Map<String, Object>> createMeeting(@RequestBody Map<String, String> request) {
        log.info("[MeetingController] request : {}", request);

        String userName = "testUser";

        // 미팅 생성
        CreateMeetingResponse meetingResponse = chimeClient.createMeeting(CreateMeetingRequest.builder()
                .clientRequestToken(UUID.randomUUID().toString())
                .mediaRegion(region)
                .build());

        log.info("[MeetingController] Meeting created with meetingId : {}", meetingResponse.meeting().meetingId());

        // 참석자 생성
//        CreateAttendeeResponse attendeeResponse = chimeClient.createAttendee(CreateAttendeeRequest.builder()
//                .meetingId(meetingResponse.meeting().meetingId())
//                .externalUserId("user-12345") // 사용자 고유 ID
//                .build());

        // 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();
        response.put("meeting", meetingResponse.meeting());
        // response.put("attendee", attendeeResponse.attendee());

        return ResponseEntity.ok(response);
    }

}
