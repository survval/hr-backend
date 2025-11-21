package com.smarthireflow.hrbackend;

import com.smarthireflow.hrbackend.model.Employee;
import com.smarthireflow.hrbackend.service.EmployeeService;
import com.smarthireflow.hrbackend.user.Role;
import com.smarthireflow.hrbackend.user.entity.UserEntity;
import com.smarthireflow.hrbackend.user.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeedData {

  @Bean
  CommandLineRunner init(UserService userService, EmployeeService employeeService, PasswordEncoder enc) {
    return args -> {
      // Seed auth users if missing
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

      // Seed domain employees if table is empty (idempotent baseline)
      if (employeeService.findAll().isEmpty()) {
        Employee se = new Employee("System Engineer", "se@hr.local", "Engineering", com.smarthireflow.hrbackend.model.Role.SYSTEM_ENGINEER);
        Employee mgr = new Employee("Manager Mary", "manager@hr.local", "Management", com.smarthireflow.hrbackend.model.Role.MANAGER);
        Employee emp = new Employee("Employee Evan", "emp@hr.local", "Operations", com.smarthireflow.hrbackend.model.Role.EMPLOYEE);
        for (Employee e : List.of(se, mgr, emp)) {
          employeeService.save(e);
        }
      }
    };
  }
}
