-- Simple Test Data for Progress Tracking

-- Drop old tables
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

-- Insert Simple Work Items (1 per project for easy testing)
INSERT INTO assignWorkItems (assignProjectId, workItemName) VALUES
(1, 'Golden Tower Project'),
(2, 'Skyline Office Project'),
(3, 'Maha Bridge Project'),
(4, 'Yangon Factory Project'),
(5, 'Al-Amin Mosque Project');

-- Insert Simple Tasks with different completion rates
INSERT INTO assignTasks (assignWorkItemId, taskName, taskStatus) VALUES
-- Golden Tower: 50% completed (5 tasks, 2 completed)
(1, 'Foundation Work', 4), -- Completed
(1, 'Ground Floor', 4), -- Completed
(1, 'First Floor', 2), -- In Progress
(1, 'Second Floor', 2), -- In Progress
(1, 'Roof Work', 1), -- Pending

-- Skyline Office: 80% completed (5 tasks, 4 completed)
(2, 'Site Clearing', 4), -- Completed
(2, 'Foundation', 4), -- Completed
(2, 'Ground Floor', 4), -- Completed
(2, 'First Floor', 4), -- Completed
(2, 'Second Floor', 2), -- In Progress

-- Maha Bridge: 20% completed (5 tasks, 1 completed)
(3, 'Bridge Foundation', 4), -- Completed
(3, 'Bridge Pillars', 2), -- In Progress
(3, 'Bridge Deck', 1), -- Pending
(3, 'Bridge Railings', 1), -- Pending
(3, 'Bridge Painting', 1), -- Pending

-- Yangon Factory: 60% completed (5 tasks, 3 completed)
(4, 'Factory Foundation', 4), -- Completed
(4, 'Factory Structure', 4), -- Completed
(4, 'Equipment Installation', 4), -- Completed
(4, 'Quality Control', 2), -- In Progress
(4, 'Final Inspection', 1), -- Pending

-- Al-Amin Mosque: 40% completed (5 tasks, 2 completed)
(5, 'Mosque Foundation', 4), -- Completed
(5, 'Prayer Hall', 4), -- Completed
(5, 'Minaret', 2), -- In Progress
(5, 'Dome', 1), -- Pending
(5, 'Finishing Work', 1); -- Pending

-- Verify results
SELECT 
    p.assignProjectId,
    p.projectInstanceName,
    COUNT(at.taskId) as total_tasks,
    SUM(CASE WHEN at.taskStatus = 4 THEN 1 ELSE 0 END) as completed_tasks,
    ROUND(SUM(CASE WHEN at.taskStatus = 4 THEN 1 ELSE 0 END) * 100.0 / COUNT(at.taskId), 1) as completion_percentage
FROM assignprojectdetails p
LEFT JOIN assignWorkItems awi ON p.assignProjectId = awi.assignProjectId
LEFT JOIN assignTasks at ON awi.workItemId = at.assignWorkItemId
GROUP BY p.assignProjectId, p.projectInstanceName
ORDER BY p.assignProjectId;
