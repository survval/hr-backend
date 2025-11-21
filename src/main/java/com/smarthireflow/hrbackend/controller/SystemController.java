package com.smarthireflow.hrbackend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system")
public class SystemController {
    @GetMapping("/status")
    @PreAuthorize("hasRole('SYSTEM_ENGINEER')")
    public String status() {
        return "Access granted: System Engineer (Super Admin) can view system metrics.";
    }
}
