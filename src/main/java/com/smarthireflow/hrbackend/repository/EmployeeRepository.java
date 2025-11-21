package com.smarthireflow.hrbackend.repository;

import com.smarthireflow.hrbackend.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Employee entities.  Extending JpaRepository
 * provides basic CRUD operations out of the box.  Additional
 * query methods can be defined here as needed (e.g. findByEmail,
 * findByDepartment, etc.).
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);
}