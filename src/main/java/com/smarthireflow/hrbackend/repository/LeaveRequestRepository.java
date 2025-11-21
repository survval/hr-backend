package com.smarthireflow.hrbackend.repository;

import com.smarthireflow.hrbackend.model.Employee;
import com.smarthireflow.hrbackend.model.LeaveRequest;
import com.smarthireflow.hrbackend.model.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing LeaveRequest entities.  Includes
 * convenience methods to find leave requests by employee or
 * status.  More complex queries (date ranges, overlapping
 * leaves, etc.) can be added here as needed.
 */
@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmployee(Employee employee);
    List<LeaveRequest> findByStatus(LeaveStatus status);
}