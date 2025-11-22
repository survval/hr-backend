package com.smarthireflow.hrbackend.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing a leave request submitted by an employee.
 * It captures the requesting employee, the type and status of the
 * leave, the start and end dates, an optional reason, and the
 * timestamp of when the request was created.  The status field
 * allows managers to approve or reject requests.  Additional
 * auditing fields (approvedBy, approvedAt, etc.) could be added
 * later if required.
 */
@Entity
@Table(name = "leave_requests")
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Employee employee;

    @Enumerated(EnumType.STRING)
    private LeaveType type;

    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

    @Column(length = 1000)
    private String reason;

    private LocalDateTime createdAt;

    public LeaveRequest() {
    }

    public LeaveRequest(Employee employee, LeaveType type, LocalDate startDate, LocalDate endDate, String reason) {
        this.employee = employee;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = LeaveStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LeaveType getType() {
        return type;
    }

    public void setType(LeaveType type) {
        this.type = type;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LeaveStatus getStatus() {
        return status;
    }

    public void setStatus(LeaveStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}