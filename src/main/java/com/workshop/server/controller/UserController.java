package com.workshop.server.controller;

import com.workshop.server.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/login")
    public ResponseEntity<String> login(
            @RequestParam String nickname,
            @RequestParam String role,
            HttpServletRequest request
    ) {

        HttpSession session = request.getSession();
        String sessionId = session.getId();

        log.info("[UserController] Getting user sessionId : {}", sessionId);
        log.info("[UserController] Logging in user with nickname: {}", nickname);

        try {
            userService.registerUser(nickname, role, sessionId);
            return ResponseEntity.ok().body("Login Success");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Login Failed");
        }
    }

    @RequestMapping("/user")
    public String user (@RequestParam String nickname, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String sessionId = session.getId();

        log.info("[UserController] Getting user sessionId : {}", sessionId);
        log.info("[UserController] Getting user info : {}", nickname);
        try {
            return userService.getUser(nickname).toString();
        } catch (Exception e) {
            return "User not found";
        }
    }

}
