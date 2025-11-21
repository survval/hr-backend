package com.smarthireflow.hrbackend.service;

import com.smarthireflow.hrbackend.model.*;
import com.smarthireflow.hrbackend.repository.AttendanceRecordRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service encapsulating business logic for attendance records.
 * Provides methods to clock in/out and to query records for a
 * particular employee.  It ensures that a user cannot clock in
 * twice on the same day without clocking out first and updates
 * the record status when clocking out.
 */
@Service
public class AttendanceService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final EmployeeService employeeService;

    public AttendanceService(AttendanceRecordRepository attendanceRecordRepository,
                             EmployeeService employeeService) {
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.employeeService = employeeService;
    }

    /**
     * Clock the given employee in.  If an attendance record for
     * today already exists and is still in progress, it simply
     * returns that record.  Otherwise a new record is created.
     */
    public AttendanceRecord clockIn(Long employeeId) {
        Employee employee = employeeService.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
        LocalDate today = LocalDate.now();
        List<AttendanceRecord> todays = attendanceRecordRepository.findByEmployeeAndDate(employee, today);
        if (!todays.isEmpty()) {
            AttendanceRecord existing = todays.get(0);
            if (existing.getStatus() == AttendanceStatus.IN_PROGRESS) {
                return existing;
            }
        }
        AttendanceRecord record = new AttendanceRecord(employee, today, LocalDateTime.now());
        return attendanceRecordRepository.save(record);
    }

    /**
     * Clock out the employee by updating the first in‑progress record
     * for today.  Throws if no record is found.
     */
    public AttendanceRecord clockOut(Long employeeId) {
        Employee employee = employeeService.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
        LocalDate today = LocalDate.now();
        List<AttendanceRecord> todays = attendanceRecordRepository.findByEmployeeAndDate(employee, today);
        if (todays.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No attendance record found for employee to clock out");
        }
        AttendanceRecord record = todays.get(0);
        record.setClockOutTime(LocalDateTime.now());
        record.setStatus(AttendanceStatus.COMPLETED);
        return attendanceRecordRepository.save(record);
    }

    /**
     * Returns all attendance records for a specific employee.
     */
    public List<AttendanceRecord> getAttendanceForEmployee(Long employeeId) {
        Employee employee = employeeService.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        return attendanceRecordRepository.findByEmployee(employee);
    }

    public List<AttendanceRecord> findAll() {
        return attendanceRecordRepository.findAll();
    }
}