package com.example.authservice.service;

import com.example.authservice.model.User;
import com.example.authservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

//    public void register(String username, String password, String role) {
//        User user = new User();
//        user.setUsername(username);
//        user.setPassword(passwordEncoder.encode(password));
//        user.setRole(role);
//        userRepository.save(user);
//    }

    public User registerUser(User user) {
        user.setRole("ROLE_USER");
        return userRepository.save(user);
    }
}
