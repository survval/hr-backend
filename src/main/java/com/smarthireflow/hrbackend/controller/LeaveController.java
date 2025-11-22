package com.smarthireflow.hrbackend.controller;

import com.smarthireflow.hrbackend.model.LeaveRequest;
import com.smarthireflow.hrbackend.model.LeaveStatus;
import com.smarthireflow.hrbackend.model.LeaveType;
import com.smarthireflow.hrbackend.service.LeaveService;
import com.smarthireflow.hrbackend.user.service.UserService;
import com.smarthireflow.hrbackend.user.entity.UserEntity;
import com.smarthireflow.hrbackend.model.Employee;
import com.smarthireflow.hrbackend.repository.EmployeeRepository;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for managing leave requests.  Employees can
 * submit new leave requests and view their own requests.  Managers
 * can view all requests and approve or reject them.
 */
@RestController
@RequestMapping
public class LeaveController {

    private final LeaveService leaveService;
    private final UserService userService;
    private final EmployeeRepository employeeRepository;

    public LeaveController(LeaveService leaveService, UserService userService, EmployeeRepository employeeRepository) {
        this.leaveService = leaveService;
        this.userService = userService;
        this.employeeRepository = employeeRepository;
    }

    /**
     * Endpoint for employees to submit a new leave request.
     * The employeeId parameter is used temporarily in place of
     * deriving the ID from the authenticated principal.  The
     * start and end dates should be provided in ISO format (yyyy-MM-dd).
     */
    /**
     * Submit a new leave request.  This endpoint derives the current
     * employee from the authenticated user's email rather than requiring
     * the employeeId in the request.  The start and end dates should be
     * provided in ISO format (yyyy-MM-dd).
     */
    @PostMapping("/employee/leaves")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<LeaveRequest> createLeave(Authentication auth,
                                                    @RequestParam LeaveType type,
                                                    @RequestParam LocalDate startDate,
                                                    @RequestParam LocalDate endDate,
                                                    @RequestParam(required = false) String reason) {
        // Resolve the current user from authentication
        UserEntity user = userService.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        // Look up the Employee record by email
        Employee employee = employeeRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
        LeaveRequest req = leaveService.createLeaveRequest(employee.getId(), type, startDate, endDate, reason);
        return ResponseEntity.ok(req);
    }

    /**
     * Get all leave requests for a specific employee id.
     * System engineers and managers may view other employees' leaves.
     */
    @GetMapping("/employee/leaves/{employeeId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<List<LeaveRequest>> getLeavesForEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(leaveService.findByEmployee(employeeId));
    }

    /**
     * Get leave requests for the authenticated employee.
     */
    @GetMapping("/employee/leaves")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<List<LeaveRequest>> getMyLeaves(Authentication auth) {
        UserEntity user = userService.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Employee employee = employeeRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
        return ResponseEntity.ok(leaveService.findByEmployee(employee.getId()));
    }

    /**
     * Managers and system engineers can view all leave requests.
     */
    @GetMapping("/admin/leaves")
    @PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<List<LeaveRequest>> getAllLeaves() {
        return ResponseEntity.ok(leaveService.findAll());
    }

    /**
     * Approve a leave request.  Only managers or system engineers
     * should be allowed to invoke this.
     */
    @PatchMapping("/admin/leaves/{id}/approve")
    @PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<LeaveRequest> approve(@PathVariable Long id) {
        return ResponseEntity.ok(leaveService.approveRequest(id));
    }

    /**
     * Reject a leave request.
     */
    @PatchMapping("/admin/leaves/{id}/reject")
    @PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<LeaveRequest> reject(@PathVariable Long id) {
        return ResponseEntity.ok(leaveService.rejectRequest(id));
    }
}