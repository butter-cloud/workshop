package com.workshop.server.controller;

import com.workshop.server.common.WebResponse;
import com.workshop.server.dto.UserDTO;
import com.workshop.server.exception.UserAlreadyExistsException;
import com.workshop.server.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
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
    public ResponseEntity<WebResponse<?>> login(
            @RequestBody UserDTO userDTO,
            HttpServletRequest request
    ){
        log.info("[UserController] userDTO : {}", userDTO.toString());
        HttpSession session = request.getSession();
        String sessionId = session.getId();

        log.info("[UserController] Getting user sessionId : {}", sessionId);
        log.info("[UserController] Logging in user with nickname: {}", userDTO.getNickname());

        try {
            userService.registerUser(userDTO.getNickname(), userDTO.getRole(), sessionId);
            log.info("[UserController] User with nickname {} registered", userDTO.getNickname());
            return ResponseEntity.ok().body(WebResponse.success("User registered successfully"));
        } catch (UserAlreadyExistsException e) {
            log.info("[UserController] User with nickname {} already exists", userDTO.getNickname());
            return ResponseEntity.ok().body(WebResponse.failure(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(WebResponse.failure("Error registering user"));
        }
    }

    @RequestMapping("/user")
    public String user (@RequestParam String nickname) {

        log.info("[UserController] Getting user with nickname : {}", nickname);

        try {
            return userService.getUser(nickname).toString();
        } catch (Exception e) {
            return "User not found";
        }
    }

    @RequestMapping("/user/all")
    public String allUsers () {
        return userService.getAllUsers().toString();
    }

}
