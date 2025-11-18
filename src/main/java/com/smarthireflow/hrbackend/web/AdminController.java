package com.smarthireflow.hrbackend.web;

import com.smarthireflow.hrbackend.user.entity.UserEntity;
import com.smarthireflow.hrbackend.user.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    // Existing example: create employee (stub)
    @PostMapping("/employee")
    @PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ENGINEER')")
    public String createEmployee() {
        return "Employee created by manager/admin";
    }

    // NEW: Manager/SystemEngineer can fetch employees
    @GetMapping("/employees")
    @PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ENGINEER')")
    public List<UserEntity> getEmployees(Authentication auth) {
        // Later you can filter based on manager's team using auth.getName()
        return userService.findAll();
    }
}
