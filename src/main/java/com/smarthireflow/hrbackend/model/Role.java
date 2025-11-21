package com.smarthireflow.hrbackend.model;

/**
 * Enumeration of application roles.  These correspond to the
 * roles used in the Spring Security configuration (e.g. ROLE_EMPLOYEE,
 * ROLE_MANAGER, ROLE_SYSTEM_ENGINEER).  Having a strongly typed
 * enumeration helps avoid typos throughout the code base and
 * centralises the definition of allowed roles.  Values should
 * reflect the roles used in the frontend navigation items and
 * security configuration.
 */
public enum Role {
    EMPLOYEE,
    MANAGER,
    SYSTEM_ENGINEER
}