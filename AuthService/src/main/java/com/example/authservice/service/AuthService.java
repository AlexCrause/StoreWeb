package com.example.authservice.service;

import com.example.authservice.exception.InvalidCredentialsException;
import com.example.authservice.exception.UserNotFoundException;
import com.example.authservice.model.User;
import com.example.authservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Константа для роли пользователя
    private static final String DEFAULT_ROLE = "ROLE_USER";

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Регистрация нового пользователя.
     *
     * @param user объект пользователя
     * @return зарегистрированный пользователь
     */
    public User registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new InvalidCredentialsException("Username is already taken");
        }
        // Устанавливаем роль по умолчанию
        user.setRole(DEFAULT_ROLE);
        // Хешируем пароль перед сохранением
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Поиск пользователя по ID.
     *
     * @param userId ID пользователя
     * @return объект пользователя
     * @throws UserNotFoundException если пользователь не найден
     */
    public User findById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }

    /**
     * Поиск пользователя по имени.
     *
     * @param username имя пользователя
     * @return объект пользователя
     * @throws UserNotFoundException если пользователь не найден
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }

    /**
     * Удаление пользователя по ID.
     *
     * @param userId ID пользователя
     */
    public void deleteById(UUID userId) {
        userRepository.deleteById(userId);
    }

    /**
     * Проверка учетных данных пользователя.
     *
     * @param username имя пользователя
     * @param password пароль
     * @return true, если учетные данные верны
     * @throws InvalidCredentialsException если учетные данные неверны
     */
    public boolean authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        // Сравниваем пароль
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
        return true;
    }
}
