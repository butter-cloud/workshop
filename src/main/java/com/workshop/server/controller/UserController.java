package com.workshop.server.controller;

import com.workshop.server.common.WebResponse;
import com.workshop.server.dto.UserDTO;
import com.workshop.server.exception.UserAlreadyExistsException;
import com.workshop.server.service.MatchService;
import com.workshop.server.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private MatchService matchService;

    @RequestMapping("/login")
    public ResponseEntity<WebResponse<?>> login(
            @RequestBody UserDTO userDTO
    ){
        log.info("[UserController] userDTO : {}", userDTO.toString());

        try {
            // redis에 유저 등록
            String registeredUserId = userService.registerUser(userDTO.getUserName(), userDTO.getRole());
            if (registeredUserId != null) {

                log.info("[UserController] User with userId {} registered", registeredUserId);
                return ResponseEntity.ok().body(WebResponse.success(registeredUserId, "User registered successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(WebResponse.failure("Error registering user"));
            }
        } catch (UserAlreadyExistsException e) {
            log.info("[UserController] User with name {} already exists", userDTO.getUserName());
            return ResponseEntity.ok().body(WebResponse.failure(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(WebResponse.failure("Error registering user"));
        }
    }

    @RequestMapping("/user")
    public String user (@RequestParam String userId) {

        log.info("[UserController] Getting user with userId : {}", userId);

        try {
            log.info("[UserController] Getting user with userId : {}", userId);
            String user = userService.getUser(userId).toString();
            log.info("User: {}", user);
            return user;
        } catch (Exception e) {
            return "User not found";
        }
    }

    @RequestMapping("/supporter/accept")
    public String acceptRequest (@RequestParam String supporterId, @RequestParam String clientId) {
        log.info("[UserController] Supporter with userId {} accepted request of {}!", supporterId, clientId);
        try {
            String matchedRoomId = matchService.matchSupporter(supporterId, clientId);
            if (matchedRoomId == null) {
                return "Request not accepted";
            }
            return matchedRoomId;
        } catch (Exception e) {
            return "Request not accepted";
        }
    }

}
