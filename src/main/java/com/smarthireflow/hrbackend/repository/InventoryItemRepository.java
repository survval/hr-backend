package com.smarthireflow.hrbackend.repository;

import com.smarthireflow.hrbackend.model.Employee;
import com.smarthireflow.hrbackend.model.InventoryItem;
import com.smarthireflow.hrbackend.model.InventoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for InventoryItem entities.  Exposes standard
 * CRUD operations and convenience methods for searching by
 * status or category.
 */
@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    List<InventoryItem> findByStatus(InventoryStatus status);
    List<InventoryItem> findByCategory(String category);

    /**
     * Find all items currently assigned to a given employee.
     *
     * @param assignedTo the employee
     * @return all inventory items assigned to that employee
     */
    List<InventoryItem> findByAssignedTo(Employee assignedTo);
}