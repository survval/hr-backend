package com.smarthireflow.hrbackend.controller;

import com.smarthireflow.hrbackend.model.Employee;
import com.smarthireflow.hrbackend.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for employee management.  Managers and system
 * engineers can create and update employee records.  All
 * authenticated users can view the list of employees.
 */
@RestController
@RequestMapping
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Retrieve all employees.  Accessible to all roles so that
     * employees can see who else is in the organisation.
     */
    @GetMapping("/employee/employees")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<List<Employee>> getAll() {
        return ResponseEntity.ok(employeeService.findAll());
    }

    /**
     * Create a new employee.  Only managers or system engineers can
     * create employees.  Password management is handled separately by
     * the existing authentication mechanism; this endpoint only
     * stores core employee data.
     */
    @PostMapping("/admin/employees")
    @PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<Employee> create(@RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.save(employee));
    }

    /**
     * Update an existing employee record.  Only managers or system
     * engineers can perform this action.
     */
    @PutMapping("/admin/employees/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<Employee> update(@PathVariable Long id, @RequestBody Employee updated) {
        // ensure the path ID is honoured
        updated.setId(id);
        return ResponseEntity.ok(employeeService.save(updated));
    }

    /**
     * Delete an employee record.  Only managers or system engineers
     * can perform this action.
     */
    @DeleteMapping("/admin/employees/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}