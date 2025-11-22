package com.smarthireflow.hrbackend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

import com.smarthireflow.hrbackend.model.InventoryCondition;
import lombok.Getter;
import lombok.Setter;

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
@Getter
@Setter
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;

    private String category;

    @Enumerated(EnumType.STRING)
    private InventoryStatus status;

    @Enumerated(EnumType.STRING)
    private InventoryCondition condition;

    @ManyToOne
    private Employee assignedTo;

    /**
     * Date on which the item was assigned to an employee.  When
     * an item is first created it is unassigned and this field
     * will be null.  It is set to the current date when
     * assignItem is called in the service layer.
     */
    private LocalDate assignedDate;

    /**
     * Date on which the item was returned by an employee.  This
     * is set when returnItem is called in the service layer.  It
     * will be null while the item is assigned or has never been
     * returned.
     */
    private LocalDate returnDate;

    /**
     * Manufacturer serial number or other unique identifier.
     */
    private String serialNumber;

    /**
     * Purchase cost of the asset.  A simple Double is used here
     * rather than BigDecimal because we are not performing any
     * monetary calculations; if that changes a BigDecimal may
     * be more appropriate.
     */
    private Double cost;

    private LocalDate purchaseDate;

    /**
     * Details about the warranty for this asset (e.g. expiry
     * date, terms).  Kept as a String to allow free-form
     * description.
     */
    private String warranty;

    /**
     * Additional notes about the item, such as its condition,
     * repairs or comments from employees.  Increased column
     * length to accommodate longer text.
     */
    @Column(length = 2000)
    private String notes;

    /**
     * Estimated current value of the item.  Separate from
     * purchase cost to allow tracking of depreciation.  A
     * simple Double is used for the same reasons as cost.
     */
    private Double value;

    public InventoryItem() {
    }

    public InventoryItem(String itemName, String category, InventoryStatus status, InventoryCondition condition, Employee assignedTo, LocalDate purchaseDate) {
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

    public InventoryCondition getCondition() {
        return condition;
    }

    public void setCondition(InventoryCondition condition) {
        this.condition = condition;
    }

    public Employee getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Employee assignedTo) {
        this.assignedTo = assignedTo;
    }

    public LocalDate getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(LocalDate assignedDate) {
        this.assignedDate = assignedDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getWarranty() {
        return warranty;
    }

    public void setWarranty(String warranty) {
        this.warranty = warranty;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}