/* ----------------------------------------------------------
   PROJECT 1: Residential Apartment (Medium Rise)
   ---------------------------------------------------------- */
CALL assignFullProject(
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
    '2024-02-01',               -- startDate
    '2024-10-01'                -- endDate
);

/* ----------------------------------------------------------
   PROJECT 2: Commercial Office Building (High Rise)
   ---------------------------------------------------------- */
CALL assignFullProject(
    2,                          -- projectTypeId (Commercial & Institutional)
    'Skyline Office Tower',     -- projectInstanceName
    5,                          -- projectBuildingId (Office Building)
    4,                          -- projectLevelId (High Rise: 6+ floors)
    25000,                      -- projectArea (sqft)
    120,                        -- projectHeight (feet)
    12,                         -- totalStories
    48,                         -- totalUnits (offices)
    3,                          -- supervisorId
    'Mandalay, Chanayethazan',  -- projectLocation
    35000,                      -- projectOverHeadCost
    'planning',                 -- projectStatusName
    'autoAssign',               -- assignStatusName
    '2024-03-15',               -- startDate
    '2025-05-15'                -- endDate
);

/* ----------------------------------------------------------
   PROJECT 3: Infrastructure - Bridge (Large Scale)
   ---------------------------------------------------------- */
CALL assignFullProject(
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
    '2024-06-01',               -- startDate
    '2025-12-01'                -- endDate
);

/* ----------------------------------------------------------
   PROJECT 4: Industrial Factory (Single Floor)
   ---------------------------------------------------------- */
CALL assignFullProject(
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
    '2024-04-01',               -- startDate
    '2024-09-28'                -- endDate
);

/* ----------------------------------------------------------
   PROJECT 5: Religious Building - Mosque (Large Scale)
   ---------------------------------------------------------- */
CALL assignFullProject(
    5,                          -- projectTypeId (Religious Building)
    'Al-Amin Grand Mosque',     -- projectInstanceName
    16,                         -- projectBuildingId (Mosque)
    8,                          -- projectLevelId (Large Scale)
    8000,                       -- projectArea (sqft)
    60,                         -- projectHeight (feet)
    2,                          -- totalStories
    1,                          -- totalUnits (main prayer hall)
    6,                          -- supervisorId
    'Naypyidaw, Zabuthiri',     -- projectLocation
    30000,                      -- projectOverHeadCost
    'planning',                 -- projectStatusName
    'autoAssign',               -- assignStatusName
    '2024-05-01',               -- startDate
    '2025-02-25'                -- endDate
);
