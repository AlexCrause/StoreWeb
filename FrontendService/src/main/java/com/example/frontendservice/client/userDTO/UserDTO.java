package com.example.frontendservice.client.userDTO;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class UserDTO {

    @NotNull
    @NotEmpty
    private UUID id;
    @NotNull
    @NotEmpty
    private String username;
    @NotNull
    @NotEmpty
    private String password;


    public UserDTO(String username, String password, UUID id) {
        this.username = username;
        this.password = password;

        this.id = id;
    }

    public UserDTO() {

    }

    public @NotNull @NotEmpty UUID getId() {
        return id;
    }

    public void setId(@NotNull @NotEmpty UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
