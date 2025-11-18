package com.smarthireflow.hrbackend;

import com.smarthireflow.hrbackend.user.Role;
import com.smarthireflow.hrbackend.user.entity.UserEntity;
import com.smarthireflow.hrbackend.user.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SeedData {

  @Bean
  CommandLineRunner init(UserService userService, PasswordEncoder enc) {
    return args -> {
      if (userService.count() == 0) {
        userService.save(UserEntity.builder()
            .email("se@hr.local")
            .passwordHash(enc.encode("admin123"))
            .role(Role.SYSTEM_ENGINEER)
            .fullName("System Engineer")
            .build());
        userService.save(UserEntity.builder()
            .email("manager@hr.local")
            .passwordHash(enc.encode("admin123"))
            .role(Role.MANAGER)
            .fullName("Manager Mary")
            .build());
        userService.save(UserEntity.builder()
            .email("emp@hr.local")
            .passwordHash(enc.encode("admin123"))
            .role(Role.EMPLOYEE)
            .fullName("Employee Evan")
            .build());
      }
    };
  }
}
