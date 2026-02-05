-- Sample Data Insert for Progress Tracking

-- Insert Work Items for each project
INSERT INTO assignWorkItems (assignProjectId, workItemName, workItemDescription, estimatedCost) VALUES
-- Golden Tower Construction (Project ID: 1)
(1, 'Foundation Work', 'Building foundation and base structure', 45000000),
(1, 'Structural Framework', 'Steel structure and columns', 36000000),
(1, 'Electrical Systems', 'Electrical wiring and systems', 27000000),
(1, 'Plumbing Systems', 'Water and plumbing systems', 18000000),
(1, 'Interior Finishing', 'Interior decoration and finishing', 54000000),

-- Skyline Office Complex (Project ID: 2)
(2, 'Site Preparation', 'Land clearing and preparation', 24000000),
(2, 'Building Structure', 'Main building construction', 48000000),
(3, 'Bridge Foundation', 'Bridge foundation work', 32000000),
(3, 'Bridge Structure', 'Main bridge construction', 48000000),

-- Yangon Garment Factory (Project ID: 4)
(4, 'Factory Building', 'Main factory construction', 30000000),
(4, 'Equipment Installation', 'Machinery installation', 18000000),
(4, 'Quality Control Systems', 'QC systems setup', 12000000),

-- Al-Amin Grand Mosque (Project ID: 5)
(5, 'Prayer Hall', 'Main prayer hall construction', 20000000),
(5, 'Minaret Construction', 'Minaret and tower construction', 10000000),
(5, 'Dome Construction', 'Main dome construction', 10000000);

-- Insert Tasks for each Work Item
INSERT INTO assignTasks (assignWorkItemId, taskName, taskStatus, taskCost, estimatedDuration) VALUES
-- Golden Tower Tasks
(1, 'Foundation Excavation', 4, 15000000, 30),
(2, 'Foundation Pouring', 4, 30000000, 45),
(3, 'Steel Column Installation', 3, 20000000, 60),
(4, 'Electrical Wiring', 2, 15000000, 40),
(5, 'Plumbing Installation', 2, 10000000, 30),
(6, 'Interior Painting', 1, 20000000, 50),

-- Skyline Office Tasks
(7, 'Land Clearing', 4, 12000000, 15),
(8, 'Building Foundation', 3, 24000000, 60),
(9, 'Bridge Pillars', 2, 20000000, 90),
(10, 'Bridge Deck', 1, 28000000, 120),

-- Yangon Factory Tasks
(11, 'Factory Structure', 3, 20000000, 45),
(12, 'Machinery Setup', 2, 12000000, 30),
(13, 'QC Equipment', 1, 8000000, 20),

-- Al-Amin Mosque Tasks
(14, 'Hall Foundation', 4, 10000000, 40),
(15, 'Minaret Base', 2, 5000000, 60),
(16, 'Dome Structure', 1, 8000000, 80);

-- Update Project Progress Summary
INSERT INTO projectProgress (assignProjectId, totalTasks, doneTasks, totalDuration, remainingDays, earnedValue, plannedValue) VALUES
(1, 36, 5, 240, 120, 45000000, 180000000),
(2, 24, 8, 420, 200, 48000000, 120000000),
(3, 18, 2, 540, 300, 16000000, 80000000),
(4, 30, 6, 180, 90, 12000000, 60000000),
(5, 12, 1, 300, 150, 8000000, 40000000)

ON DUPLICATE KEY UPDATE
doneTasks = VALUES(doneTasks),
remainingDays = VALUES(remainingDays),
earnedValue = VALUES(earnedValue),
lastUpdated = CURRENT_TIMESTAMP;
