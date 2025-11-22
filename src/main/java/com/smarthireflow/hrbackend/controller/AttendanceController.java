package com.smarthireflow.hrbackend.controller;

import com.smarthireflow.hrbackend.model.AttendanceRecord;
import com.smarthireflow.hrbackend.service.AttendanceService;
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

import java.util.List;

/**
 * REST controller for employee attendance (time tracking).  Employees
 * can clock in and clock out, and view their own attendance history.
 */
@RestController
@RequestMapping("/employee/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final UserService userService;
    private final EmployeeRepository employeeRepository;

    public AttendanceController(AttendanceService attendanceService, UserService userService, EmployeeRepository employeeRepository) {
        this.attendanceService = attendanceService;
        this.userService = userService;
        this.employeeRepository = employeeRepository;
    }

    /**
     * Clock the current user in.  In a real application the employee
     * ID would be derived from the authenticated principal rather
     * than passed explicitly.  For the MVP this ID is provided as
     * a request parameter.
     */
    /**
     * Clock the authenticated employee in.  The employee id is derived
     * from the authenticated principal rather than being passed in
     * explicitly.  A manager may still use the variant with an
     * explicit id via {@link #clockInForEmployee(Long)}.
     */
    @PostMapping("/clock-in")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<AttendanceRecord> clockIn(Authentication auth) {
        UserEntity user = userService.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Employee employee = employeeRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
        AttendanceRecord record = attendanceService.clockIn(employee.getId());
        return ResponseEntity.ok(record);
    }

    /**
     * Clock a specific employee in (admin/manager use only).
     */
    @PostMapping("/clock-in/{employeeId}")
    @PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<AttendanceRecord> clockInForEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(attendanceService.clockIn(employeeId));
    }

    /**
     * Clock the current user out.  See clockIn for notes on
     * authentication; the employeeId parameter is used for MVP.
     */
    /**
     * Clock the authenticated employee out.  See notes on
     * {@link #clockIn(Authentication)} for deriving the employee id.
     */
    @PostMapping("/clock-out")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<AttendanceRecord> clockOut(Authentication auth) {
        UserEntity user = userService.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Employee employee = employeeRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
        AttendanceRecord record = attendanceService.clockOut(employee.getId());
        return ResponseEntity.ok(record);
    }

    /**
     * Clock a specific employee out (admin/manager use only).
     */
    @PostMapping("/clock-out/{employeeId}")
    @PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<AttendanceRecord> clockOutForEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(attendanceService.clockOut(employeeId));
    }

    /**
     * Retrieve attendance history for a specific employee.  Managers
     * may view records for any employee while employees can only
     * view their own.  Authorization can be further enforced via
     * method security annotations if required.
     */
    /**
     * Retrieve attendance records for the authenticated employee.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<List<AttendanceRecord>> getMyRecords(Authentication auth) {
        UserEntity user = userService.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Employee employee = employeeRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
        return ResponseEntity.ok(attendanceService.getAttendanceForEmployee(employee.getId()));
    }

    /**
     * Retrieve attendance records for a specific employee id.  Managers
     * and system engineers may view any employee's records, while
     * employees should use {@link #getMyRecords(Authentication)}.
     */
    @GetMapping("/{employeeId}")
    @PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<List<AttendanceRecord>> getRecordsForEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(attendanceService.getAttendanceForEmployee(employeeId));
    }
}