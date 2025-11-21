package com.smarthireflow.hrbackend.controller;

import com.smarthireflow.hrbackend.model.InventoryItem;
import com.smarthireflow.hrbackend.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for inventory management.  All authenticated users
 * can view the inventory, but only managers and system engineers
 * may create, update or delete items.
 */
@RestController
@RequestMapping
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * Retrieve all inventory items.  Accessible to all roles.
     */
    @GetMapping("/employee/inventory")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<List<InventoryItem>> getAll() {
        return ResponseEntity.ok(inventoryService.findAll());
    }

    /**
     * Create a new inventory item.  Only managers or system
     * engineers can perform this action.
     */
    @PostMapping("/admin/inventory")
    @PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<InventoryItem> create(@RequestBody InventoryItem item) {
        return ResponseEntity.ok(inventoryService.createItem(item));
    }

    /**
     * Update an existing inventory item.  Only managers or system
     * engineers can perform this action.
     */
    @PutMapping("/admin/inventory/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<InventoryItem> update(@PathVariable Long id, @RequestBody InventoryItem updated) {
        return ResponseEntity.ok(inventoryService.updateItem(id, updated));
    }

    /**
     * Delete an inventory item.  Only managers or system engineers
     * can perform this action.
     */
    @DeleteMapping("/admin/inventory/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        inventoryService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Assign an inventory item to an employee.  Only managers or
     * system engineers can perform this action.
     */
    @PatchMapping("/admin/inventory/{itemId}/assign/{employeeId}")
    @PreAuthorize("hasAnyRole('MANAGER','SYSTEM_ENGINEER')")
    public ResponseEntity<InventoryItem> assign(@PathVariable Long itemId, @PathVariable Long employeeId) {
        return ResponseEntity.ok(inventoryService.assignItem(itemId, employeeId));
    }
}