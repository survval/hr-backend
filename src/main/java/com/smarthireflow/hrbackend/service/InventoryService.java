package com.smarthireflow.hrbackend.service;

import com.smarthireflow.hrbackend.model.*;
import com.smarthireflow.hrbackend.repository.InventoryItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for managing inventory items.  Handles common
 * operations such as creating, updating and assigning items to
 * employees.  Business rules (e.g. cannot assign an item that is
 * not available) are enforced here.
 */
@Service
public class InventoryService {

    private final InventoryItemRepository inventoryItemRepository;
    private final EmployeeService employeeService;

    public InventoryService(InventoryItemRepository inventoryItemRepository,
                            EmployeeService employeeService) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.employeeService = employeeService;
    }

    public InventoryItem createItem(InventoryItem item) {
        // If no status is provided default to ACTIVE (item is in the
        // inventory and ready to be assigned).  This aligns with
        // the "active" option in the UI.  Previous implementations
        // used AVAILABLE; this new enum better matches the frontend.
        if (item.getStatus() == null) {
            item.setStatus(InventoryStatus.ACTIVE);
        }
        // New items are not yet assigned so ensure assignedTo,
        // assignedDate and returnDate are null.
        item.setAssignedTo(null);
        item.setAssignedDate(null);
        item.setReturnDate(null);
        return inventoryItemRepository.save(item);
    }

    public List<InventoryItem> findAll() {
        return inventoryItemRepository.findAll();
    }

    public InventoryItem assignItem(Long itemId, Long employeeId) {
        InventoryItem item = inventoryItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory item not found"));
        // Only items that are not lost or damaged may be assigned.  If an
        // item is currently assigned (ACTIVE) we still allow reassignment
        // but we treat it as a new assignment and reset the dates.
        if (item.getStatus() == InventoryStatus.LOST || item.getStatus() == InventoryStatus.DAMAGED) {
            throw new IllegalStateException("Item cannot be assigned because it is lost or damaged");
        }
        Employee employee = employeeService.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        item.setAssignedTo(employee);
        // Set assignment details
        item.setStatus(InventoryStatus.ACTIVE);
        item.setAssignedDate(java.time.LocalDate.now());
        item.setReturnDate(null);
        return inventoryItemRepository.save(item);
    }

    public InventoryItem updateItem(Long itemId, InventoryItem updated) {
        InventoryItem existing = inventoryItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory item not found"));
        existing.setItemName(updated.getItemName());
        existing.setCategory(updated.getCategory());
        existing.setCondition(updated.getCondition());
        existing.setPurchaseDate(updated.getPurchaseDate());
        existing.setStatus(updated.getStatus());
        existing.setAssignedTo(updated.getAssignedTo());
        existing.setSerialNumber(updated.getSerialNumber());
        existing.setCost(updated.getCost());
        existing.setWarranty(updated.getWarranty());
        existing.setNotes(updated.getNotes());
        existing.setValue(updated.getValue());
        existing.setAssignedDate(updated.getAssignedDate());
        existing.setReturnDate(updated.getReturnDate());
        return inventoryItemRepository.save(existing);
    }

    /**
     * Return an inventory item from its current assignee.  The item
     * will be marked as RETURNED and its assignment information
     * cleared.  Only items currently in ACTIVE state may be
     * returned.
     *
     * @param itemId the id of the item to return
     * @return the updated item
     */
    public InventoryItem returnItem(Long itemId) {
        InventoryItem item = inventoryItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory item not found"));
        if (item.getStatus() != InventoryStatus.ACTIVE) {
            throw new IllegalStateException("Only active items can be returned");
        }
        item.setAssignedTo(null);
        item.setStatus(InventoryStatus.RETURNED);
        item.setReturnDate(java.time.LocalDate.now());
        return inventoryItemRepository.save(item);
    }

    public void deleteItem(Long itemId) {
        inventoryItemRepository.deleteById(itemId);
    }
}