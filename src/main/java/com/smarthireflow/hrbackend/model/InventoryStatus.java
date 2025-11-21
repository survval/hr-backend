package com.smarthireflow.hrbackend.model;

/**
 * Possible statuses for an inventory item.  These values reflect
 * typical states in asset management: available, assigned to
 * someone, under maintenance or retired.  They can be
 * extended to match the specific needs of the organisation.
 */
public enum InventoryStatus {
    AVAILABLE,
    ASSIGNED,
    IN_MAINTENANCE,
    RETIRED
}