package com.smarthireflow.hrbackend.model;

/**
 * Possible statuses for an inventory item.  These values reflect
 * typical states in asset management: available, assigned to
 * someone, under maintenance or retired.  They can be
 * extended to match the specific needs of the organisation.
 */
public enum InventoryStatus {
    /**
     * Item is active and available for use or currently
     * assigned to an employee.  This status corresponds to
     * the "active" option in the frontend UI.
     */
    ACTIVE,

    /**
     * Item is undergoing maintenance or repair.  It should
     * not be assigned to employees while in this state.
     */
    MAINTENANCE,

    /**
     * Item has been returned to inventory and is available
     * for reassignment.  This status corresponds to the
     * "returned" option in the frontend UI.
     */
    RETURNED,

    /**
     * Item has been reported as lost and is no longer
     * available in the inventory.
     */
    LOST,

    /**
     * Item has been damaged beyond repair and is no
     * longer usable.
     */
    DAMAGED;
}