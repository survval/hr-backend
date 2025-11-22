package com.smarthireflow.hrbackend.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a single attendance record for an employee.  This
 * entity captures the date of the record along with clock‑in and
 * clock‑out timestamps.  The status property indicates whether
 * the record is still in progress or completed.  The association
 * with Employee allows queries such as "all attendance records
 * for a given employee" and leverages the relationship when
 * generating reports.
 */
@Entity
@Table(name = "attendance_records")
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Employee employee;

    private LocalDate date;

    private LocalDateTime clockInTime;

    private LocalDateTime clockOutTime;

    @Enumerated(EnumType.STRING)
    private AttendanceStatus status;

    public AttendanceRecord() {
    }

    public AttendanceRecord(Employee employee, LocalDate date, LocalDateTime clockInTime) {
        this.employee = employee;
        this.date = date;
        this.clockInTime = clockInTime;
        this.status = AttendanceStatus.IN_PROGRESS;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDateTime getClockInTime() {
        return clockInTime;
    }

    public void setClockInTime(LocalDateTime clockInTime) {
        this.clockInTime = clockInTime;
    }

    public LocalDateTime getClockOutTime() {
        return clockOutTime;
    }

    public void setClockOutTime(LocalDateTime clockOutTime) {
        this.clockOutTime = clockOutTime;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }
}