package com.smarthireflow.hrbackend.model;

/**
 * Represents the condition of an inventory item.  These values
 * mirror the options presented in the frontend UI: excellent,
 * good, fair and poor.  Storing the condition as an enum
 * provides type safety and makes it easy to validate and
 * transform values when mapping between the API and the
 * database.
 */
public enum InventoryCondition {
    EXCELLENT,
    GOOD,
    FAIR,
    POOR
}