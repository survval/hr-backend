package com.smarthireflow.hrbackend.service;

import com.smarthireflow.hrbackend.model.Employee;
import com.smarthireflow.hrbackend.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing employees.  Encapsulates CRUD
 * operations and centralises any business rules surrounding
 * employee management.  Using a service layer makes it easy to
 * add caching or transactional behaviour in the future.
 */
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> findById(Long id) {
        return employeeRepository.findById(id);
    }

    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    public void delete(Long id) {
        employeeRepository.deleteById(id);
    }
}