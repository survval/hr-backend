package com.smarthireflow.hrbackend.model;

import jakarta.persistence.*;

/**
 * Entity representing a single employee.  In the existing
 * frontend mock data the Employee object contains an id, name,
 * email, department and role.  This entity captures those
 * fields and uses JPA annotations so Spring Data will
 * automatically persist and retrieve employee records from
 * the database.
 */
@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    /**
     * Optional department name.  A simple string is used here
     * rather than a separate Department entity to keep the model
     * minimal; if departments need to be managed independently
     * a Department entity can be added later.
     */
    private String department;

    @Enumerated(EnumType.STRING)
    private Role role;

    public Employee() {
    }

    public Employee(String name, String email, String department, Role role) {
        this.name = name;
        this.email = email;
        this.department = department;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}