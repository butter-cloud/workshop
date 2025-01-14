package com.workshop.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String name;
    private String id;
    private String role;        // supporter, client
    private String status;      // idle, requesting, meeting
}
