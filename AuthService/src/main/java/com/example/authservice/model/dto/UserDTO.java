package com.example.authservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
//@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class UserDTO {

    @NotNull
    @NotEmpty
    private String username;
    @NotNull
    @NotEmpty
    private String password;
    //private String role;

    public UserDTO(String username, String password) {
        this.username = username;
        this.password = password;
       // this.role = role;
    }

    public UserDTO() {

    }

    //    public String getRole() {
//        return role;
//    }
//
//    public void setRole(String role) {
//        this.role = role;
//    }
}
