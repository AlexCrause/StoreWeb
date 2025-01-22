package com.example.frontendservice.client.userDTO;

import lombok.Data;

import java.util.UUID;

@Data
public class UserInfoDTO {

    private UUID id;
    private String username;
}
