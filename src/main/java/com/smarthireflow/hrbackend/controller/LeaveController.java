package com.smarthireflow.hrbackend.controller;

import com.smarthireflow.hrbackend.model.LeaveRequest;
import com.smarthireflow.hrbackend.model.LeaveStatus;
import com.smarthireflow.hrbackend.model.LeaveType;
import com.smarthireflow.hrbackend.service.LeaveService;
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

    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    /**
     * Endpoint for employees to submit a new leave request.
     * The employeeId parameter is used temporarily in place of
     * deriving the ID from the authenticated principal.  The
     * start and end dates should be provided in ISO format (yyyy-MM-dd).
     */
    @PostMapping("/employee/leaves")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<LeaveRequest> createLeave(@RequestParam Long employeeId,
                                                    @RequestParam LeaveType type,
                                                    @RequestParam LocalDate startDate,
                                                    @RequestParam LocalDate endDate,
                                                    @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(leaveService.createLeaveRequest(employeeId, type, startDate, endDate, reason));
    }

    /**
     * Get all leave requests for a specific employee.
     */
    @GetMapping("/employee/leaves/{employeeId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<List<LeaveRequest>> getLeavesForEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(leaveService.findByEmployee(employeeId));
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