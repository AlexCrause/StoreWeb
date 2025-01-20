package com.example.authservice.model.dto;

import lombok.Data;

import java.util.UUID;
@Data
public class UserInfoDTO {

    private UUID id;
    private String username;
}
