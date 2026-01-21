/* ----------------------------------------------------------
   PROJECT 1: Residential Apartment (Medium Rise)
   ---------------------------------------------------------- */
CALL assignProjects(
    1,                          -- projectTypeId (Residential Building)
    'Golden Tower Apartments',  -- projectInstanceName
    1,                          -- projectBuildingId (Apartment)
    3,                          -- projectLevelId (Medium Rise: 3-5 floors)
    12000,                      -- projectArea (sqft)
    45,                         -- projectHeight (feet)
    5,                          -- totalStories
    20,                         -- totalUnits
    2,                          -- supervisorId
    'Yangon, Hlaing Township', -- projectLocation
    18000,                      -- projectOverHeadCost
    'planning',                 -- projectStatusName
    'autoAssign',               -- assignStatusName
    1500000,                    -- projectCost
    50,                         -- projectLaborQty
    240,                        -- projectDuration (days)
    '2024-02-01',               -- startDate
    '2024-10-01'                -- endDate
);

/* Assume assignProjectId = 1 */

-- WORK ITEM 1: Substructure (Excavation & Foundation)
CALL assignWorkItems(
    1,              -- assignProjectId
    1,              -- projectWorkItemId (Substructure)
    'planning',     -- projectStatusName
    'autoAssign',   -- assignStatusName
    225000,         -- workItemCost (15% of total)
    8,              -- workItemLaborQty
    30,             -- workItemDuration (days)
    '2024-02-01',   -- startDate
    '2024-03-02'    -- endDate
);

-- Assign tasks to Substructure (Work Item ID 1)
CALL assignTaskToWorkItem(1, 1, 1, 10, '2024-02-01', '2024-02-10', 'planning', 'autoAssign'); -- Excavation
CALL assignTaskToWorkItem(1, 1, 2, 8, '2024-02-11', '2024-02-18', 'planning', 'autoAssign'); -- Foundation
CALL assignTaskToWorkItem(1, 1, 3, 6, '2024-02-19', '2024-02-24', 'planning', 'autoAssign'); -- Formwork
CALL assignTaskToWorkItem(1, 1, 4, 3, '2024-02-25', '2024-02-27', 'planning', 'autoAssign'); -- Reinforcement
CALL assignTaskToWorkItem(1, 1, 5, 3, '2024-02-28', '2024-03-02', 'planning', 'autoAssign'); -- Concrete Pouring

-- Add skills for Substructure
CALL addSkillToWorkItem(1, 1, 'autoAssign', 4, 15);  -- General Laborer (4 workers)
CALL addSkillToWorkItem(1, 2, 'autoAssign', 2, 25);  -- Heavy Equipment Operator (2 workers)
CALL addSkillToWorkItem(1, 3, 'autoAssign', 2, 30);  -- Mason (2 workers)

-- WORK ITEM 2: Superstructure
CALL assignWorkItems(
    1,              -- assignProjectId
    2,              -- projectWorkItemId (Superstructure)
    'planning',     -- projectStatusName
    'autoAssign',   -- assignStatusName
    510000,         -- workItemCost (34% of total)
    20,             -- workItemLaborQty
    90,             -- workItemDuration (days)
    '2024-03-03',   -- startDate
    '2024-06-01'    -- endDate
);

-- Assign tasks to Superstructure (Work Item ID 2)
CALL assignTaskToWorkItem(1, 2, 6, 40, '2024-03-03', '2024-04-11', 'planning', 'autoAssign'); -- Column Construction
CALL assignTaskToWorkItem(1, 2, 7, 30, '2024-04-12', '2024-05-11', 'planning', 'autoAssign'); -- Beam Construction
CALL assignTaskToWorkItem(1, 2, 8, 20, '2024-05-12', '2024-05-31', 'planning', 'autoAssign'); -- Slab Construction

-- Add skills for Superstructure
CALL addSkillToWorkItem(2, 3, 'autoAssign', 6, 30);  -- Carpenter (6 workers)
CALL addSkillToWorkItem(2, 7, 'autoAssign', 8, 35);  -- Steel Fixer (8 workers)
CALL addSkillToWorkItem(2, 8, 'autoAssign', 6, 28);  -- Concrete Finisher (6 workers)

-- WORK ITEM 3: Finishing
CALL assignWOrkItems(
    1,              -- assignProjectId
    3,              -- projectWorkItemId (Finishing)
    'planning',     -- projectStatusName
    'autoAssign',   -- assignStatusName
    420000,         -- workItemCost (28% of total)
    15,             -- workItemLaborQty
    70,             -- workItemDuration (days)
    '2024-06-02',   -- startDate
    '2024-08-10'    -- endDate
);

-- Assign tasks to Finishing (Work Item ID 3)
CALL assignTaskToWorkItem(1, 3, 10, 25, '2024-06-02', '2024-06-26', 'planning', 'autoAssign'); -- Plastering
CALL assignTaskToWorkItem(1, 3, 11, 20, '2024-06-27', '2024-07-16', 'planning', 'autoAssign'); -- Painting
CALL assignTaskToWorkItem(1, 3, 12, 15, '2024-07-17', '2024-07-31', 'planning', 'autoAssign'); -- Flooring
CALL assignTaskToWorkItem(1, 3, 13, 10, '2024-08-01', '2024-08-10', 'planning', 'autoAssign'); -- Tiling

-- Add skills for Finishing
CALL addSkillToWorkItem(3, 13, 'autoAssign', 4, 25);  -- Plasterer (4 workers)
CALL addSkillToWorkItem(3, 14, 'autoAssign', 5, 22);  -- Painter (5 workers)
CALL addSkillToWorkItem(3, 15, 'autoAssign', 6, 28);  -- Tile Setter (6 workers)

-- WORK ITEM 4: MEP
CALL assignWorkItems(
    1,              -- assignProjectId
    4,              -- projectWorkItemId (MEP)
    'planning',     -- projectStatusName
    'autoAssign',   -- assignStatusName
    330000,         -- workItemCost (22% of total)
    10,             -- workItemLaborQty
    40,             -- workItemDuration (days)
    '2024-08-11',   -- startDate
    '2024-09-19'    -- endDate
);

-- Assign tasks to MEP (Work Item ID 4)
CALL assignTaskToWorkItem(1, 4, 14, 15, '2024-08-11', '2024-08-25', 'planning', 'autoAssign'); -- Electrical Wiring
CALL assignTaskToWorkItem(1, 4, 15, 15, '2024-08-26', '2024-09-09', 'planning', 'autoAssign'); -- Plumbing Installation
CALL assignTaskToWorkItem(1, 4, 16, 10, '2024-09-10', '2024-09-19', 'planning', 'autoAssign'); -- HVAC Installation

-- Add skills for MEP
CALL addSkillToWorkItem(4, 4, 'autoAssign', 4, 35);  -- Electrician (4 workers)
CALL addSkillToWorkItem(4, 5, 'autoAssign', 4, 32);  -- Plumber (4 workers)
CALL addSkillToWorkItem(4, 16, 'autoAssign', 2, 40); -- HVAC Technician (2 workers)

-- WORK ITEM 5: External
CALL assignWorkItems(
    1,              -- assignProjectId
    5,              -- projectWorkItemId (External)
    'planning',     -- projectStatusName
    'autoAssign',   -- assignStatusName
    150000,         -- workItemCost (10% of total)
    7,              -- workItemLaborQty
    15,             -- workItemDuration (days)
    '2024-09-20',   -- startDate
    '2024-10-01'    -- endDate
);

-- Assign tasks to External (Work Item ID 5)
CALL assignTaskToWorkItem(1, 5, 17, 7, '2024-09-20', '2024-09-26', 'planning', 'autoAssign'); -- Landscaping
CALL assignTaskToWorkItem(1, 5, 18, 5, '2024-09-27', '2024-10-01', 'planning', 'autoAssign'); -- Paving
CALL assignTaskToWorkItem(1, 5, 19, 3, '2024-09-27', '2024-09-29', 'planning', 'autoAssign'); -- Fencing

-- Add skills for External
CALL addSkillToWorkItem(5, 1, 'autoAssign', 4, 18);  -- General Laborer (4 workers)
CALL addSkillToWorkItem(5, 18, 'autoAssign', 2, 30); -- Landscaper (2 workers)
CALL addSkillToWorkItem(5, 19, 'autoAssign', 1, 25); -- Paving Specialist (1 worker)

/* ----------------------------------------------------------
   PROJECT 2: Commercial Office Building (High Rise)
   ---------------------------------------------------------- */
CALL assignProjects(
    2,                          -- projectTypeId (Commercial & Institutional)
    'Skyline Office Tower',     -- projectInstanceName
    5,                          -- projectBuildingId (Office Building)
    4,                          -- projectLevelId (High Rise: 6+ floors)
    25000,                      -- projectArea (sqft)
    120,                        -- projectHeight (feet)
    12,                         -- totalStories
    48,                         -- totalUnits (offices)
    3,                          -- supervisorId
    'Mandalay, Chanayethazan', -- projectLocation
    35000,                      -- projectOverHeadCost
    'planning',                 -- projectStatusName
    'autoAssign',               -- assignStatusName
    3500000,                    -- projectCost
    80,                         -- projectLaborQty
    420,                        -- projectDuration (days)
    '2024-03-15',               -- startDate
    '2025-05-15'                -- endDate
);

/* Assume assignProjectId = 2 */

-- WORK ITEM 1: Substructure (for Office Building High Rise)
CALL assignWorkItems(
    2,              -- assignProjectId
    1,              -- projectWorkItemId (Substructure)
    'planning',     -- projectStatusName
    'autoAssign',   -- assignStatusName
    560000,         -- workItemCost (16% of total)
    15,             -- workItemLaborQty
    45,             -- workItemDuration (days)
    '2024-03-15',   -- startDate
    '2024-04-28'    -- endDate
);

-- Add deep foundation tasks for high rise
CALL assignTaskToWorkItem(2, 1, 1, 20, '2024-03-15', '2024-04-03', 'planning', 'autoAssign'); -- Deep Excavation
CALL assignTaskToWorkItem(2, 1, 2, 15, '2024-04-04', '2024-04-18', 'planning', 'autoAssign'); -- Pile Foundation
CALL assignTaskToWorkItem(2, 1, 3, 10, '2024-04-19', '2024-04-28', 'planning', 'autoAssign'); -- Raft Foundation

-- Add specialized skills for high-rise substructure
CALL addSkillToWorkItem(6, 2, 'autoAssign', 3, 40);  -- Heavy Equipment Operator (3 workers)
CALL addSkillToWorkItem(6, 10, 'autoAssign', 2, 45); -- Surveyor (2 workers)
CALL addSkillToWorkItem(6, 7, 'autoAssign', 5, 50);  -- Steel Fixer (5 workers)
CALL addSkillToWorkItem(6, 3, 'autoAssign', 5, 35);  -- Mason (5 workers)

-- WORK ITEM 2: Superstructure
CALL assignWorkItems(
    2,              -- assignProjectId
    2,              -- projectWorkItemId (Superstructure)
    'planning',     -- projectStatusName
    'autoAssign',   -- assignStatusName
    1365000,        -- workItemCost (39% of total)
    35,             -- workItemLaborQty
    180,            -- workItemDuration (days)
    '2024-04-29',   -- startDate
    '2024-10-25'    -- endDate
);

-- Add tasks for high-rise superstructure
CALL assignTaskToWorkItem(2, 2, 6, 80, '2024-04-29', '2024-07-17', 'planning', 'autoAssign'); -- Column Construction
CALL assignTaskToWorkItem(2, 2, 7, 60, '2024-07-18', '2024-09-15', 'planning', 'autoAssign'); -- Beam Construction
CALL assignTaskToWorkItem(2, 2, 8, 40, '2024-09-16', '2024-10-25', 'planning', 'autoAssign'); -- Slab Construction

-- WORK ITEM 3: Finishing (Premium finishes for office)
CALL assignWorkItems(
    2,              -- assignProjectId
    3,              -- projectWorkItemId (Finishing)
    'planning',     -- projectStatusName
    'autoAssign',   -- assignStatusName
    1050000,        -- workItemCost (30% of total)
    25,             -- workItemLaborQty
    120,            -- workItemDuration (days)
    '2024-10-26',   -- startDate
    '2025-02-23'    -- endDate
);

-- WORK ITEM 4: MEP (Complex for office building)
CALL assignWorkItems(
    2,              -- assignProjectId
    4,              -- projectWorkItemId (MEP)
    'planning',     -- projectStatusName
    'autoAssign',   -- assignStatusName
    490000,         -- workItemCost (14% of total)
    15,             -- workItemLaborQty
    75,             -- workItemDuration (days)
    '2025-02-24',   -- startDate
    '2025-05-09'    -- endDate
);

-- WORK ITEM 5: External
CALL assignWorkItems(
    2,              -- assignProjectId
    5,              -- projectWorkItemId (External)
    'planning',     -- projectStatusName
    'autoAssign',   -- assignStatusName
    35000,          -- workItemCost (1% of total)
    5,              -- workItemLaborQty
    6,              -- workItemDuration (days)
    '2025-05-10',   -- startDate
    '2025-05-15'    -- endDate
);


/* ----------------------------------------------------------
   PROJECT 3: Infrastructure - Bridge (Large Scale)
   ---------------------------------------------------------- */
CALL assignProjects(
    4,                          -- projectTypeId (Infrastructure)
    'Maha Bandoola Bridge',     -- projectInstanceName
    13,                         -- projectBuildingId (Bridge)
    8,                          -- projectLevelId (Large Scale)
    1500,                       -- projectArea (meters length)
    25,                         -- projectHeight (meters)
    1,                          -- totalStories (single level)
    4,                          -- totalUnits (lanes)
    4,                          -- supervisorId
    'Yangon River Crossing',    -- projectLocation
    500000,                     -- projectOverHeadCost
    'planning',                 -- projectStatusName
    'autoAssign',               -- assignStatusName
    5000000,                    -- projectCost
    120,                        -- projectLaborQty
    540,                        -- projectDuration (days)
    '2024-06-01',               -- startDate
    '2025-12-01'                -- endDate
);

/* Assume assignProjectId = 3 */

-- WORK ITEM 1: Substructure (Bridge piers and abutments)
CALL assignWorkItems(
    3,              -- assignProjectId
    1,              -- projectWorkItemId (Substructure)
    'planning',     -- projectStatusName
    'autoAssign',   -- assignStatusName
    1500000,        -- workItemCost (30% of total)
    40,             -- workItemLaborQty
    180,            -- workItemDuration (days)
    '2024-06-01',   -- startDate
    '2024-11-27'    -- endDate
);

-- Bridge-specific tasks for substructure
CALL assignTaskToWorkItem(3, 1, 20, 45, '2024-06-01', '2024-07-15', 'planning', 'autoAssign'); -- Site Clearing & Grubbing
CALL assignTaskToWorkItem(3, 1, 21, 60, '2024-07-16', '2024-09-13', 'planning', 'autoAssign'); -- Earthworks & Grading
CALL assignTaskToWorkItem(3, 1, 28, 75, '2024-09-14', '2024-11-27', 'planning', 'autoAssign'); -- Pier/Abutment Construction

-- Add specialized bridge skills
CALL addSkillToWorkItem(11, 2, 'autoAssign', 5, 45);   -- Heavy Equipment Operator (5 workers)
CALL addSkillToWorkItem(11, 20, 'autoAssign', 3, 60);  -- Bridge Specialist (3 workers)
CALL addSkillToWorkItem(11, 10, 'autoAssign', 2, 50);  -- Surveyor (2 workers)
CALL addSkillToWorkItem(11, 7, 'autoAssign', 15, 55);  -- Steel Fixer (15 workers)

-- WORK ITEM 2: Superstructure (Bridge deck)
CALL assignWorkItems(
    3,              -- assignProjectId
    2,              -- projectWorkItemId (Superstructure)
    'planning',     -- projectStatusName
    'autoAssign',   -- assignStatusName
    2400000,        -- workItemCost (48% of total)
    50,             -- workItemLaborQty
    240,            -- workItemDuration (days)
    '2024-11-28',   -- startDate
    '2025-07-26'    -- endDate
);

-- Bridge deck construction tasks
CALL assignTaskToWorkItem(3, 2, 27, 120, '2024-11-28', '2025-03-27', 'planning', 'autoAssign'); -- Bridge Deck Construction
CALL assignTaskToWorkItem(3, 2, 7, 60, '2025-03-28', '2025-05-26', 'planning', 'autoAssign'); -- Beam Construction
CALL assignTaskToWorkItem(3, 2, 8, 60, '2025-05-27', '2025-07-26', 'planning', 'autoAssign'); -- Slab Construction

-- WORK ITEM 3: Finishing (Bridge finishing works)
CALL assignWorkItems(
    3,              -- assignProjectId
    3,              -- projectWorkItemId (Finishing)
    'planning',     -- projectStatusName
    'autoAssign',   -- assignStatusName
    750000,         -- workItemCost (15% of total)
    20,             -- workItemLaborQty
    90,             -- workItemDuration (days)
    '2025-07-27',   -- startDate
    '2025-10-24'    -- endDate
);

-- Bridge finishing tasks
CALL assignTaskToWorkItem(3, 3, 11, 30, '2025-07-27', '2025-08-25', 'planning', 'autoAssign'); -- Painting (anti-corrosion)
CALL assignTaskToWorkItem(3, 3, 32, 30, '2025-08-26', '2025-09-24', 'planning', 'autoAssign'); -- Pavement Markings
CALL assignTaskToWorkItem(3, 3, 33, 30, '2025-09-25', '2025-10-24', 'planning', 'autoAssign'); -- Guardrail Installation

-- WORK ITEM 4: MEP (Bridge lighting and drainage)
CALL assignWorkItems(
    3,              -- assignProjectId
    4,              -- projectWorkItemId (MEP)
    'planning',     -- projectStatusName
    'autoAssign',   -- assignStatusName
    300000,         -- workItemCost (6% of total)
    10,             -- workItemLaborQty
    30,             -- workItemDuration (days)
    '2025-10-25',   -- startDate
    '2025-11-23'    -- endDate
);

-- WORK ITEM 5: External (Approach roads and landscaping)
CALL assignWorkItems(
    3,              -- assignProjectId
    5,              -- projectWorkItemId (External)
    'planning',     -- projectStatusName
    'autoAssign',   -- assignStatusName
    50000,          -- workItemCost (1% of total)
    8,              -- workItemLaborQty
    8,              -- workItemDuration (days)
    '2025-11-24',   -- startDate
    '2025-12-01'    -- endDate
);


/* ----------------------------------------------------------
   PROJECT 4: Industrial Factory (Single Floor)
   ---------------------------------------------------------- */
CALL assignProjects(
    3,                          -- projectTypeId (Industrial Building)
    'Yangon Garment Factory',   -- projectInstanceName
    9,                          -- projectBuildingId (Factory)
    1,                          -- projectLevelId (Single Floor)
    50000,                      -- projectArea (sqft)
    20,                         -- projectHeight (feet)
    1,                          -- totalStories
    10,                         -- totalUnits (production sections)
    5,                          -- supervisorId
    'Yangon, Dagon Seikkan',   -- projectLocation
    25000,                      -- projectOverHeadCost
    'planning',                 -- projectStatusName
    'autoAssign',               -- assignStatusName
    2000000,                    -- projectCost
    60,                         -- projectLaborQty
    180,                        -- projectDuration (days)
    '2024-04-01',               -- startDate
    '2024-09-28'                -- endDate
);

/* Assume assignProjectId = 4 */

-- WORK ITEM 1: Substructure (Light foundation for factory)
CALL assignWorkItems(
    4,              -- assignProjectId
    1,              -- projectWorkItemId (Substructure)
    'planning',     -- projectStatusName
    'autoAssign',   -- assignStatusName
    300000,         -- workItemCost (15% of total)
    12,             -- workItemLaborQty
    30,             -- workItemDuration (days)
    '2024-04-01',   -- startDate
    '2024-04-30'    -- endDate
);

-- WORK ITEM 2: Superstructure (Large spans for factory)
CALL assignWorkItems(
    4,              -- assignProjectId
    2,              -- projectWorkItemId (Superstructure)
    'planning',     -- projectStatusName
    'autoAssign',   -- assignStatusName
    1000000,        -- workItemCost (50% of total)
    25,             -- workItemLaborQty
    90,             -- workItemDuration (days)
    '2024-05-01',   -- startDate
    '2024-07-29'    -- endDate
);

-- WORK ITEM 3: Finishing (Basic for industrial use)
CALL assignWorkItems(
    4,              -- assignProjectId
    3,              -- projectWorkItemId (Finishing)
    'planning',     -- projectStatusName
    'autoAssign',   -- assignStatusName
    300000,         -- workItemCost (15% of total)
    10,             -- workItemLaborQty
    30,             -- workItemDuration (days)
    '2024-07-30',   -- startDate
    '2024-08-28'    -- endDate
);

-- WORK ITEM 4: MEP (Industrial systems)
CALL assignWorkItems(
    4,              -- assignProjectId
    4,              -- projectWorkItemId (MEP)
    'planning',     -- projectStatusName
    'autoAssign',   -- assignStatusName
    360000,         -- workItemCost (18% of total)
    13,             -- workItemLaborQty
    25,             -- workItemDuration (days)
    '2024-08-29',   -- startDate
    '2024-09-22'    -- endDate
);

-- WORK ITEM 5: External
CALL assignWorkItems(
    4,              -- assignProjectId
    5,              -- projectWorkItemId (External)
    'planning',     -- projectStatusName
    'autoAssign',   -- assignStatusName
    40000,          -- workItemCost (2% of total)
    5,              -- workItemLaborQty
    6,              -- workItemDuration (days)
    '2024-09-23',   -- startDate
    '2024-09-28'    -- endDate
);


/* ----------------------------------------------------------
   PROJECT 5: Religious Building - Mosque (Large Scale)
   ---------------------------------------------------------- */
CALL assignProjects(
    5,                          -- projectTypeId (Religious Building)
    'Al-Amin Grand Mosque',     -- projectInstanceName
    16,                         -- projectBuildingId (Mosque)
    8,                          -- projectLevelId (Large Scale)
    8000,                       -- projectArea (sqft)
    60,                         -- projectHeight (feet) - for minaret
    2,                          -- totalStories
    1,                          -- totalUnits (main prayer hall)
    6,                          -- supervisorId
    'Naypyidaw, Zabuthiri',     -- projectLocation
    30000,                      -- projectOverHeadCost
    'planning',                 -- projectStatusName
    'autoAssign',               -- assignStatusName
    1200000,                    -- projectCost
    40,                         -- projectLaborQty
    300,                        -- projectDuration (days)
    '2024-05-01',               -- startDate
    '2025-02-25'                -- endDate
);

/* Assume assignProjectId = 5 */

-- WORK ITEM 1: Substructure (with dome and minaret foundations)
CALL assignWorkItems(
    5,              -- assignProjectId
    1,              -- projectWorkItemId (Substructure)
    'planning',     -- projectStatusName
    'autoAssign',   -- assignStatusName
    216000,         -- workItemCost (18% of total)
    10,             -- workItemLaborQty
    45,             -- workItemDuration (days)
    '2024-05-01',   -- startDate
    '2024-06-14'    -- endDate
);

-- Add specialized tasks for mosque foundation
CALL assignTaskToWorkItem(5, 1, 1, 15, '2024-05-01', '2024-05-15', 'planning', 'autoAssign'); -- Main excavation
CALL assignTaskToWorkItem(5, 1, 2, 20, '2024-05-16', '2024-06-04', 'planning', 'autoAssign'); -- Foundation with minaret base
CALL assignTaskToWorkItem(5, 1, 5, 10, '2024-06-05', '2024-06-14', 'planning', 'autoAssign'); -- Special concrete for dome

-- Add specialized religious building skills
CALL addSkillToWorkItem(21, 3, 'autoAssign', 4, 35);   -- Mason (4 workers, special decorative work)
CALL addSkillToWorkItem(21, 22, 'autoAssign', 2, 60);  -- Religious Art Specialist (2 workers)
CALL addSkillToWorkItem(21, 8, 'autoAssign', 4, 30);   -- Concrete Finisher (4 workers)

-- WORK ITEM 2: Superstructure (Dome and minaret construction)
CALL assignWorkItems(
    5,              -- assignProjectId
    2,              -- projectWorkItemId (Superstructure)
    'planning',     -- projectStatusName
    'autoAssign',   -- assignStatusName
    540000,         -- workItemCost (45% of total)
    15,             -- workItemLaborQty
    135,            -- workItemDuration (days)
    '2024-06-15',   -- startDate
    '2024-10-28'    -- endDate
);

-- WORK ITEM 3: Finishing (Ornate decorative work)
CALL assignWorkItems(
    5,              -- assignProjectId
    3,              -- projectWorkItemId (Finishing)
    'planning',     -- projectStatusName
    'autoAssign',   -- assignStatusName
    300000,         -- workItemCost (25% of total)
    10,             -- workItemLaborQty
    90,             -- workItemDuration (days)
    '2024-10-29',   -- startDate
    '2025-01-27'    -- endDate
);

-- WORK ITEM 4: MEP (Special systems for mosque)
CALL assignWorkItems(
    5,              -- assignProjectId
    4,              -- projectWorkItemId (MEP)
    'planning',     -- projectStatusName
    'autoAssign',   -- assignStatusName
    120000,         -- workItemCost (10% of total)
    5,              -- workItemLaborQty
    25,             -- workItemDuration (days)
    '2025-01-28',   -- startDate
    '2025-02-21'    -- endDate
);

-- WORK ITEM 5: External (Courtyard and landscaping)
CALL assignWorkItems(
    5,              -- assignProjectId
    5,              -- projectWorkItemId (External)
    'planning',     -- projectStatusName
    'autoAssign',   -- assignStatusName
    24000,          -- workItemCost (2% of total)
    5,              -- workItemLaborQty
    4,              -- workItemDuration (days)
    '2025-02-22',   -- startDate
    '2025-02-25'    -- endDate
);