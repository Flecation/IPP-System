-- Progress Tracking Tables for IPP System

-- 1. Work Items Table
CREATE TABLE IF NOT EXISTS assignWorkItems (
    workItemId INT PRIMARY KEY AUTO_INCREMENT,
    assignProjectId INT NOT NULL,
    workItemName VARCHAR(255) NOT NULL,
    workItemDescription TEXT,
    estimatedCost DECIMAL(15,2) DEFAULT 0,
    createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (assignProjectId) REFERENCES assignprojectdetails(assignProjectId)
);

-- 2. Tasks Table
CREATE TABLE IF NOT EXISTS assignTasks (
    taskId INT PRIMARY KEY AUTO_INCREMENT,
    assignWorkItemId INT NOT NULL,
    taskName VARCHAR(255) NOT NULL,
    taskStatus INT DEFAULT 1, -- 1=Pending, 2=In Progress, 3=Review, 4=Completed
    taskCost DECIMAL(15,2) DEFAULT 0,
    estimatedDuration INT DEFAULT 1, -- in days
    actualDuration INT DEFAULT 0,
    isCancel BOOLEAN DEFAULT FALSE,
    createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completedDate TIMESTAMP NULL,
    FOREIGN KEY (assignWorkItemId) REFERENCES assignWorkItems(workItemId)
);

-- 3. Project Progress Summary Table (Optional - for faster queries)
CREATE TABLE IF NOT EXISTS projectProgress (
    progressId INT PRIMARY KEY AUTO_INCREMENT,
    assignProjectId INT NOT NULL UNIQUE,
    totalTasks INT DEFAULT 0,
    doneTasks INT DEFAULT 0,
    totalDuration INT DEFAULT 0,
    remainingDays INT DEFAULT 0,
    earnedValue DECIMAL(15,2) DEFAULT 0,
    plannedValue DECIMAL(15,2) DEFAULT 0,
    lastUpdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (assignProjectId) REFERENCES assignprojectdetails(assignProjectId)
);
