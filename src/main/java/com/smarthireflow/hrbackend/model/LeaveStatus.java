package com.smarthireflow.hrbackend.model;

/**
 * Status of a leave request.  New requests start as PENDING and
 * can be moved to APPROVED or REJECTED by a manager.  These
 * statuses map directly to values shown in the frontend mock
 * data such as "Pending", "Approved" or "Rejected".
 */
public enum LeaveStatus {
    PENDING,
    APPROVED,
    REJECTED
}