package com.example.authservice.init;

import com.example.authservice.model.Role;
import com.example.authservice.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Data
@Component
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private final RoleRepository roleRepository;


    @PostConstruct
    public void init() {
        logger.info("Initializing roles...");
        createRoleIfNotExist("USER");
        createRoleIfNotExist("ADMIN");
    }

    private void createRoleIfNotExist(String roleName) {
        if (!roleRepository.findByName(roleName).isPresent()) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
            logger.info("Role {} created", roleName);
        } else {
            logger.info("Role {} already exists", roleName);
        }
    }
}
