package com.smarthireflow.hrbackend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Represents an asset or item in the organisation's inventory.  Each
 * inventory item may be assigned to an employee, have a category
 * (e.g. Laptop, Headset), a status (available, assigned, etc.), a
 * condition and the date it was purchased.  The association to
 * Employee allows us to query which items are allocated to an
 * individual.
 */
@Entity
@Table(name = "inventory_items")
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;

    private String category;

    @Enumerated(EnumType.STRING)
    private InventoryStatus status;

    private String condition;

    @ManyToOne
    private Employee assignedTo;

    private LocalDate purchaseDate;

    public InventoryItem() {
    }

    public InventoryItem(String itemName, String category, InventoryStatus status, String condition, Employee assignedTo, LocalDate purchaseDate) {
        this.itemName = itemName;
        this.category = category;
        this.status = status;
        this.condition = condition;
        this.assignedTo = assignedTo;
        this.purchaseDate = purchaseDate;
    }

    public Long getId() {
        return id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public InventoryStatus getStatus() {
        return status;
    }

    public void setStatus(InventoryStatus status) {
        this.status = status;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Employee getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Employee assignedTo) {
        this.assignedTo = assignedTo;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}