-- Quick Setup for Progress Tracking

-- Create Work Items Table
CREATE TABLE IF NOT EXISTS assignWorkItems (
    workItemId INT PRIMARY KEY AUTO_INCREMENT,
    assignProjectId INT NOT NULL,
    workItemName VARCHAR(255) NOT NULL,
    createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Tasks Table  
CREATE TABLE IF NOT EXISTS assignTasks (
    taskId INT PRIMARY KEY AUTO_INCREMENT,
    assignWorkItemId INT NOT NULL,
    taskName VARCHAR(255) NOT NULL,
    taskStatus INT DEFAULT 1, -- 1=Pending, 2=In Progress, 3=Review, 4=Completed
    isCancel BOOLEAN DEFAULT FALSE,
    createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (assignWorkItemId) REFERENCES assignWorkItems(workItemId)
);

-- Insert Sample Work Items
INSERT INTO assignWorkItems (assignProjectId, workItemName) VALUES
(1, 'Foundation Work'),
(1, 'Structural Framework'), 
(1, 'Electrical Systems'),
(1, 'Plumbing Systems'),
(1, 'Interior Finishing'),
(2, 'Site Preparation'),
(2, 'Building Structure'),
(2, 'Electrical Installation'),
(2, 'Plumbing Installation'),
(2, 'Interior Finishing'),
(3, 'Bridge Foundation'),
(3, 'Bridge Structure'),
(3, 'Bridge Deck'),
(4, 'Factory Building'),
(4, 'Equipment Installation'),
(4, 'Quality Control Systems'),
(5, 'Prayer Hall'),
(5, 'Minaret Construction'),
(5, 'Dome Construction');

-- Insert Sample Tasks
INSERT INTO assignTasks (assignWorkItemId, taskName, taskStatus) VALUES
-- Golden Tower (Project ID: 1)
(1, 'Foundation Excavation', 4), -- Completed
(2, 'Steel Framework', 3), -- In Progress
(3, 'Electrical Wiring', 2), -- In Progress
(4, 'Plumbing Installation', 2), -- In Progress
(5, 'Interior Painting', 1), -- Pending

-- Skyline Office (Project ID: 2)  
(6, 'Land Clearing', 4), -- Completed
(7, 'Building Foundation', 3), -- In Progress
(8, 'Electrical Installation', 2), -- In Progress
(9, 'Plumbing Installation', 2), -- In Progress
(10, 'Interior Finishing', 1), -- Pending

-- Maha Bandoola Bridge (Project ID: 3)
(11, 'Bridge Foundation', 2), -- In Progress
(12, 'Bridge Structure', 1), -- Pending
(13, 'Bridge Deck', 1), -- Pending

-- Yangon Factory (Project ID: 4)
(14, 'Factory Structure', 3), -- In Progress
(15, 'Equipment Installation', 2), -- In Progress
(16, 'QC Systems', 1), -- Pending

-- Al-Amin Mosque (Project ID: 5)
(17, 'Prayer Hall Foundation', 2), -- In Progress
(18, 'Minaret Construction', 1), -- Pending
(19, 'Dome Structure', 1); -- Pending
