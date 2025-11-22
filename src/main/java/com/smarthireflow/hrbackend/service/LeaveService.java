package com.smarthireflow.hrbackend.service;

import com.smarthireflow.hrbackend.model.*;
import com.smarthireflow.hrbackend.repository.LeaveRequestRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for leave management.  Provides functionality for
 * employees to create leave requests and for managers to
 * approve or reject requests.  Additional logic such as
 * validation of overlapping dates or leave balances can be
 * incorporated here in the future.
 */
@Service
public class LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeService employeeService;

    public LeaveService(LeaveRequestRepository leaveRequestRepository, EmployeeService employeeService) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeService = employeeService;
    }

    /**
     * Create a new leave request for the given employee.
     */
    public LeaveRequest createLeaveRequest(Long employeeId, LeaveType type, LocalDate startDate, LocalDate endDate, String reason) {
        Employee employee = employeeService.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        LeaveRequest request = new LeaveRequest(employee, type, startDate, endDate, reason);
        return leaveRequestRepository.save(request);
    }

    /**
     * Approve a leave request.  Only managers should call this.
     */
    public LeaveRequest approveRequest(Long requestId) {
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found"));
        request.setStatus(LeaveStatus.APPROVED);
        return leaveRequestRepository.save(request);
    }

    /**
     * Reject a leave request.  Only managers should call this.
     */
    public LeaveRequest rejectRequest(Long requestId) {
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found"));
        request.setStatus(LeaveStatus.REJECTED);
        return leaveRequestRepository.save(request);
    }

    /**
     * Find all leave requests.
     */
    public List<LeaveRequest> findAll() {
        return leaveRequestRepository.findAll();
    }

    /**
     * Find leave requests by employee.
     */
    public List<LeaveRequest> findByEmployee(Long employeeId) {
        Employee employee = employeeService.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        return leaveRequestRepository.findByEmployee(employee);
    }

    /**
     * Find leave requests by status.
     */
    public List<LeaveRequest> findByStatus(LeaveStatus status) {
        return leaveRequestRepository.findByStatus(status);
    }
}