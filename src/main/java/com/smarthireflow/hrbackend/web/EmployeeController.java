package com.smarthireflow.hrbackend.web;

import com.smarthireflow.hrbackend.user.entity.UserEntity;
import com.smarthireflow.hrbackend.user.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private final UserService userService;

    public EmployeeController(UserService userService) {
        this.userService = userService;
    }

    // 👇 Employee / Manager / SystemEngineer can see ONLY their own profile
    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','SYSTEM_ENGINEER')")
    public UserEntity getOwnProfile(Authentication auth) {
        String email = auth.getName(); // comes from JWT 'sub'
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // 👇 Existing swipe-in endpoint (all roles can record own swipe)
    @PostMapping("/attendance/swipe-in")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','SYSTEM_ENGINEER')")
    public String swipeIn() {
        return "Swipe in recorded";
    }
}
