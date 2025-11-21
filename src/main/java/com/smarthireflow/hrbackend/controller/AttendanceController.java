package com.smarthireflow.hrbackend.controller;

import com.smarthireflow.hrbackend.model.AttendanceRecord;
import com.smarthireflow.hrbackend.service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for employee attendance (time tracking).  Employees
 * can clock in and clock out, and view their own attendance history.
 */
@RestController
@RequestMapping("/employee/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    /**
     * Clock the current user in.  In a real application the employee
     * ID would be derived from the authenticated principal rather
     * than passed explicitly.  For the MVP this ID is provided as
     * a request parameter.
     */
    @PostMapping("/clock-in/{employeeId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<AttendanceRecord> clockIn(@PathVariable Long employeeId) {
        return ResponseEntity.ok(attendanceService.clockIn(employeeId));
    }

    /**
     * Clock the current user out.  See clockIn for notes on
     * authentication; the employeeId parameter is used for MVP.
     */
    @PostMapping("/clock-out/{employeeId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<AttendanceRecord> clockOut(@PathVariable Long employeeId) {
        return ResponseEntity.ok(attendanceService.clockOut(employeeId));
    }

    /**
     * Retrieve attendance history for a specific employee.  Managers
     * may view records for any employee while employees can only
     * view their own.  Authorization can be further enforced via
     * method security annotations if required.
     */
    @GetMapping("/{employeeId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<List<AttendanceRecord>> getRecords(@PathVariable Long employeeId) {
        return ResponseEntity.ok(attendanceService.getAttendanceForEmployee(employeeId));
    }
}