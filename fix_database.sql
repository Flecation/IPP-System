-- Fix Database Structure and Insert Data

-- Drop old tables if they exist
DROP TABLE IF EXISTS assignTasks;
DROP TABLE IF EXISTS assignWorkItems;

-- Create Work Items Table
CREATE TABLE assignWorkItems (
    workItemId INT PRIMARY KEY AUTO_INCREMENT,
    assignProjectId INT NOT NULL,
    workItemName VARCHAR(255) NOT NULL,
    createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Tasks Table
CREATE TABLE assignTasks (
    taskId INT PRIMARY KEY AUTO_INCREMENT,
    assignWorkItemId INT NOT NULL,
    taskName VARCHAR(255) NOT NULL,
    taskStatus INT DEFAULT 1, -- 1=Pending, 2=In Progress, 3=Review, 4=Completed
    isCancel BOOLEAN DEFAULT FALSE,
    createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert Work Items
INSERT INTO assignWorkItems (assignProjectId, workItemName) VALUES
(1, 'Foundation Work'),
(1, 'Building Structure'),
(1, 'Electrical Systems'),
(2, 'Site Preparation'),
(2, 'Office Building'),
(2, 'Electrical Installation'),
(3, 'Bridge Foundation'),
(3, 'Bridge Structure'),
(3, 'Bridge Deck'),
(4, 'Factory Building'),
(4, 'Equipment Installation'),
(4, 'Quality Control Systems'),
(5, 'Prayer Hall'),
(5, 'Minaret Construction'),
(5, 'Dome Construction');

-- Insert Tasks
INSERT INTO assignTasks (assignWorkItemId, taskName, taskStatus) VALUES
-- Golden Tower (Project ID: 1)
(1, 'Foundation Excavation', 4), -- Completed
(2, 'Steel Framework', 3), -- In Progress
(3, 'Electrical Wiring', 2), -- In Progress

-- Skyline Office (Project ID: 2)
(4, 'Land Clearing', 4), -- Completed
(5, 'Office Construction', 3), -- In Progress
(6, 'Electrical Installation', 2), -- In Progress

-- Maha Bridge (Project ID: 3)
(7, 'Bridge Foundation', 2), -- In Progress
(8, 'Bridge Structure', 1), -- Pending
(9, 'Bridge Deck', 1), -- Pending

-- Yangon Factory (Project ID: 4)
(10, 'Factory Structure', 3), -- In Progress
(11, 'Equipment Installation', 2), -- In Progress
(12, 'QC Systems', 1), -- Pending

-- Al-Amin Mosque (Project ID: 5)
(13, 'Prayer Hall Foundation', 2), -- In Progress
(14, 'Minaret Construction', 1), -- Pending
(15, 'Dome Structure', 1); -- Pending

-- Verify data
SELECT 'Work Items Count:' as info, COUNT(*) as count FROM assignWorkItems
UNION ALL
SELECT 'Tasks Count:', COUNT(*) FROM assignTasks
UNION ALL
SELECT 'Completed Tasks:', COUNT(*) FROM assignTasks WHERE taskStatus = 4
UNION ALL
SELECT 'In Progress Tasks:', COUNT(*) FROM assignTasks WHERE taskStatus = 2
UNION ALL
SELECT 'Pending Tasks:', COUNT(*) FROM assignTasks WHERE taskStatus = 1;
