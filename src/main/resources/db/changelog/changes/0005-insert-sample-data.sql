-- Insert sample employees (idempotent by email)
INSERT INTO employees (name, email, department, role)
SELECT 'Alice Johnson', 'alice@example.com', 'HR', 'EMPLOYEE'
WHERE NOT EXISTS (SELECT 1 FROM employees WHERE email = 'alice@example.com');

INSERT INTO employees (name, email, department, role)
SELECT 'Bob Smith', 'bob@example.com', 'IT', 'MANAGER'
WHERE NOT EXISTS (SELECT 1 FROM employees WHERE email = 'bob@example.com');

-- Insert sample inventory items
INSERT INTO inventory_items (
    item_name, category, status, condition, assigned_to_id,
    purchase_date, serial_number, cost, assigned_date, return_date,
    warranty, notes, value
) VALUES (
    'Laptop X', 'Hardware', 'ACTIVE', 'GOOD',
    (SELECT id FROM employees WHERE email = 'alice@example.com'),
    DATE '2024-12-01', 'SN001', 1500.00, DATE '2025-01-10', NULL,
    '3 years', 'Work laptop', 1200.00
), (
    'Headset Y', 'Accessory', 'RETURNED', 'EXCELLENT',
    NULL,
    DATE '2023-07-10', 'SN002', 200.00, NULL, DATE '2024-06-15',
    '2 years', 'Spare headset', 100.00
);

-- Insert sample leave requests
INSERT INTO leave_requests (
    employee_id, type, start_date, end_date, status, reason, created_at
) VALUES (
    (SELECT id FROM employees WHERE email = 'alice@example.com'),
    'VACATION', DATE '2025-02-01', DATE '2025-02-10', 'APPROVED', 'Vacation trip', CURRENT_TIMESTAMP
), (
    (SELECT id FROM employees WHERE email = 'bob@example.com'),
    'SICK', DATE '2025-03-05', DATE '2025-03-07', 'PENDING', 'Flu', CURRENT_TIMESTAMP
);

-- Insert sample attendance records
INSERT INTO attendance_records (
    employee_id, date, clock_in_time, clock_out_time, status
) VALUES (
    (SELECT id FROM employees WHERE email = 'alice@example.com'),
    DATE '2025-02-01', TIMESTAMP WITH TIME ZONE '2025-02-01 09:00:00+00', TIMESTAMP WITH TIME ZONE '2025-02-01 17:00:00+00', 'COMPLETED'
), (
    (SELECT id FROM employees WHERE email = 'bob@example.com'),
    DATE '2025-02-01', TIMESTAMP WITH TIME ZONE '2025-02-01 10:00:00+00', NULL, 'IN_PROGRESS'
);
