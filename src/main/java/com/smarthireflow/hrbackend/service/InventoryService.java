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
        if (item.getStatus() == null) {
            item.setStatus(InventoryStatus.AVAILABLE);
        }
        return inventoryItemRepository.save(item);
    }

    public List<InventoryItem> findAll() {
        return inventoryItemRepository.findAll();
    }

    public InventoryItem assignItem(Long itemId, Long employeeId) {
        InventoryItem item = inventoryItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory item not found"));
        if (item.getStatus() != InventoryStatus.AVAILABLE) {
            throw new IllegalStateException("Item is not available for assignment");
        }
        Employee employee = employeeService.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        item.setAssignedTo(employee);
        item.setStatus(InventoryStatus.ASSIGNED);
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
        return inventoryItemRepository.save(existing);
    }

    public void deleteItem(Long itemId) {
        inventoryItemRepository.deleteById(itemId);
    }
}