package com.smarthireflow.hrbackend.model;

/**
 * Status of an attendance/time entry.  An entry begins IN_PROGRESS
 * when an employee clocks in and becomes COMPLETED when they clock
 * out for the day.  Additional statuses can be added (e.g. ABSENT)
 * if needed in the future.
 */
public enum AttendanceStatus {
    IN_PROGRESS,
    COMPLETED
}