package com.smarthireflow.hrbackend.repository;

import com.smarthireflow.hrbackend.model.AttendanceRecord;
import com.smarthireflow.hrbackend.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for AttendanceRecord entities.  Provides
 * convenience methods to find records by employee and date.
 */
@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    List<AttendanceRecord> findByEmployee(Employee employee);

    List<AttendanceRecord> findByEmployeeAndDate(Employee employee, LocalDate date);
}