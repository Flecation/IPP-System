-- ///// status table /////
INSERT INTO assignStatus (assignStatusName)
VALUES ('autoAssign'), ('customAssign'), ('actualResult'), ('extraAssign');

INSERT INTO projectStatus (projectStatusName)
VALUES ('planning'), ('inProgress'), ('delay'), ('finished'), ('cancel');

-- //// for the project levels ////
INSERT INTO projectLevels (projectLevelName)
VALUES
('Single Floor'), -- id 1
('Low Rise'),     -- 1–2 floors id 2
('Medium Rise'),  -- 3–5 floors id 3
('High Rise'),    -- 6+ floors  id 4
('Extra High Rise'), -- id 5
-- for the infrastructure data
('Small Scale'),    -- id 6
('Medium Scale'),   -- id 7
('Large Scale'),    -- id 8
('Extra Large'),    -- id 9
('Grand Scale'); -- for the religious building  id 10

-- //// for the work items ////
INSERT INTO workItems (projectWorkItemName)
VALUES
('Substructure'),
('Superstructure'),
('Finishing'),
('MEP'),
('External');



-- //// project types ////
INSERT INTO projectTypes (typeName)
VALUES
('Residential Building'),
('Commercial & Institutional Building'),
('Industrial Building'),
('Infrastructure'),
('Religious Building');

-- ///// for residential buildings of relative data /////
INSERT INTO buildings (projectBuildingName)
VALUES
('Apartment'),
('Condominium'),
('Villa / House'),
('Townhouse');

-- //// insert project details for residential buildings ////
INSERT INTO projectDetails
(projectTypeId, projectBuildingId, projectLevelId, minOverHeadCost, maxOverHeadCost)
VALUES
-- Apartment
(1, 1, 2, 10, 15),   -- Apartment Low Rise: 10–15%
(1, 1, 3, 12, 18),   -- Apartment Medium Rise: 12–18%
(1, 1, 4, 15, 20),   -- Apartment High Rise: 15–20%
-- Condominium
(1, 2, 3, 12, 18),   -- Condo Medium Rise: 12–18%
(1, 2, 4, 15, 22),   -- Condo High Rise: 15–22%
-- villa / House
(1, 3, 2, 8, 12),    -- Villa / House Low Rise: 8–12%
-- Townhouse
(1, 4, 2, 9, 13),    -- Townhouse Low Rise: 9–13%
(1, 4, 3, 11, 17);   -- Townhouse Medium Rise: 11–17%


-- =============================================
-- CORRECTED workItemDetails for RESIDENTIAL BUILDINGS
-- Realistic cost percentages that sum to ~100%
-- Based on industry standards
-- =============================================

-- Clear existing residential data (projectDetailId 1-8)
DELETE FROM workItemDetails WHERE projectDetailId BETWEEN 1 AND 8;

-- Apartment Low Rise (projectDetailId: 1)
INSERT INTO workItemDetails (projectDetailId, projectWorkItemId, minDuration, maxDuration, minLabors, maxLabors, minCost, maxCost) VALUES
(1, 1, 18, 22, 5, 10, 15, 20),   -- Substructure: 15-20% (was 10-15%)
(1, 2, 33, 38, 8, 15, 30, 35),   -- Superstructure: 30-35% (was 10-15%)
(1, 3, 23, 27, 5, 10, 25, 30),   -- Finishing: 25-30% (was 10-15%)
(1, 4, 13, 17, 4, 8, 20, 25),    -- MEP: 20-25% (was 10-15%)
(1, 5, 4, 6, 2, 5, 5, 10);       -- External: 5-10% (was 10-15%)
-- Total Cost: 95-120% (with contingency)

-- Apartment Medium Rise (projectDetailId: 2)
INSERT INTO workItemDetails VALUES
(2, 1, 18, 22, 6, 12, 15, 20),   -- Substructure
(2, 2, 33, 38, 10, 18, 32, 38),  -- Superstructure (higher for medium rise)
(2, 3, 23, 27, 6, 12, 26, 32),   -- Finishing
(2, 4, 13, 17, 5, 10, 22, 28),   -- MEP (higher for taller buildings)
(2, 5, 4, 6, 3, 6, 5, 10);       -- External
-- Total: 100-128%

-- Apartment High Rise (projectDetailId: 3)
INSERT INTO workItemDetails VALUES
(3, 1, 18, 22, 8, 15, 15, 20),   -- Substructure
(3, 2, 33, 38, 12, 22, 35, 42),  -- Superstructure (highest for high rise)
(3, 3, 23, 27, 8, 15, 28, 35),   -- Finishing
(3, 4, 13, 17, 6, 12, 25, 32),   -- MEP (complex systems in high rise)
(3, 5, 4, 6, 4, 8, 5, 10);       -- External
-- Total: 108-139%

-- Condominium Medium Rise (projectDetailId: 4) - Similar to Apartment Medium
INSERT INTO workItemDetails VALUES
(4, 1, 18, 22, 6, 12, 15, 20),
(4, 2, 33, 38, 10, 18, 33, 39),   -- Slightly higher quality
(4, 3, 23, 27, 6, 12, 28, 34),    -- Better finishes
(4, 4, 13, 17, 5, 10, 23, 29),    -- Better MEP
(4, 5, 4, 6, 3, 6, 5, 10);
-- Total: 104-132%

-- Condominium High Rise (projectDetailId: 5)
INSERT INTO workItemDetails VALUES
(5, 1, 18, 22, 8, 15, 15, 20),
(5, 2, 33, 38, 12, 22, 36, 43),   -- Premium structure
(5, 3, 23, 27, 8, 15, 30, 37),    -- Luxury finishes
(5, 4, 13, 17, 6, 12, 27, 34),    -- Advanced MEP
(5, 5, 4, 6, 4, 8, 5, 10);
-- Total: 113-144%

-- Villa / House Low Rise (projectDetailId: 6)
INSERT INTO workItemDetails VALUES
(6, 1, 18, 22, 4, 8, 15, 20),
(6, 2, 33, 38, 6, 12, 35, 42),     -- Houses have higher structure %
(6, 3, 23, 27, 4, 8, 30, 38),      -- Custom finishes
(6, 4, 13, 17, 3, 6, 15, 20),      -- Simpler MEP
(6, 5, 4, 6, 2, 4, 10, 15);        -- More external work
-- Total: 105-135%

-- Townhouse Low Rise (projectDetailId: 7)
INSERT INTO workItemDetails VALUES
(7, 1, 18, 22, 4, 8, 14, 19),
(7, 2, 33, 38, 6, 12, 33, 40),
(7, 3, 23, 27, 4, 8, 28, 35),
(7, 4, 13, 17, 3, 6, 18, 24),
(7, 5, 4, 6, 2, 4, 8, 12);
-- Total: 101-130%

-- Townhouse Medium Rise (projectDetailId: 8)
INSERT INTO workItemDetails VALUES
(8, 1, 18, 22, 5, 10, 14, 19),
(8, 2, 33, 38, 8, 14, 34, 41),
(8, 3, 23, 27, 5, 10, 29, 36),
(8, 4, 13, 17, 4, 8, 20, 26),
(8, 5, 4, 6, 3, 6, 8, 12);
-- Total: 105-134%




-- ///// for commercial & institutional buildings of relative data /////

INSERT INTO buildings (projectBuildingName)
VALUES
('Office Building'),       -- buildingId = 5
('School / Educational Building'), -- buildingId = 6
('Hospital / Healthcare Building'), -- buildingId = 7
('Hotel / Hospitality Building');   -- buildingId = 8

-- //// insert project detail for commercial & institutional buildings ////
INSERT INTO projectDetails
(projectTypeId, projectBuildingId, projectLevelId, minOverHeadCost, maxOverHeadCost)
VALUES
-- Office Building
(2, 5, 2, 10, 15),   -- Low Rise: 10–15%
(2, 5, 3, 12, 18),   -- Medium Rise: 12–18%
(2, 5, 4, 15, 22),   -- High Rise: 15–22%
-- School / Educational Building
(2, 6, 2, 8, 12),    -- Low Rise: 8–12%
(2, 6, 3, 10, 16),   -- Medium Rise: 10–16%
-- Hospital / Healthcare Building
(2, 7, 3, 12, 18),   -- Medium Rise: 12–18%
(2, 7, 4, 15, 22),   -- High Rise: 15–22%
-- Hotel / Hospitality Building
(2, 8, 3, 12, 18),   -- Medium Rise: 12–18%
(2, 8, 4, 15, 22);   -- High Rise: 15–22%


-- //// for the industrial building of relative data ////
-- Industrial buildings are usually single-story or low-rise
INSERT INTO buildings (projectBuildingName)
VALUES
('Factory'),       -- buildingId = 9
('Warehouse'),     -- buildingId = 10
('Power Plant');   -- buildingId = 11

-- //// insert project detail for the industrial building ////
INSERT INTO projectDetails
(projectTypeId, projectBuildingId, projectLevelId, minOverHeadCost, maxOverHeadCost)
VALUES
-- Factory
(3, 9, 1, 8, 12),    -- Single Floor: 8–12%
(3, 9, 2, 10, 15),   -- Low Rise: 10–15%
-- Warehouse
(3, 10, 1, 7, 10),   -- Single Floor: 7–10%
(3, 10, 2, 9, 14),   -- Low Rise: 9–14%
-- Power Plant
(3, 11, 1, 12, 18),  -- Single Floor: 12–18%
(3, 11, 2, 15, 22);  -- Low Rise: 15–22%


-- //// for the infrastructure building of relative data ////
-- //// infrastructure use the scale like small scale , medium skill etc ////
INSERT INTO buildings (projectBuildingName)
VALUES
('Road / Highway'),     -- building 12
('Bridge'),             -- building 13
('Dam / Reservoir'),    -- building 14
('Utility / Pipeline'); -- building 15

-- //// insert project detail for the infrastructure building ////
INSERT INTO projectDetails (projectTypeId, projectBuildingId, projectLevelId, minOverHeadCost, maxOverHeadCost)
VALUES
-- Road / Highway
(4, 12, 6, 5, 10),       -- small scale 5-10%
(4, 12, 7, 8, 12),       -- medium scale 8-12%
(4, 12, 8, 10, 15),      -- large scale 10-15%
(4, 12, 9, 12, 18),      -- extra large 12-18%
-- Bridge
(4, 13, 7, 10, 18),      -- medium scale 10-18%
(4, 13, 8, 15, 22),      -- large scale 15-22%
-- Dam / Reservoir
(4, 14, 8, 18, 25),      -- large scale 18-25%
(4, 14, 9, 20, 30),      -- extra large 20-30%
-- Utility / Pipeline
(4, 15, 6, 6, 12),       -- small scale 6-12%
(4, 15, 7, 10, 18);      -- medium scale 10-18%


-- //// for the religious building of relative data ////
INSERT INTO buildings (projectBuildingName)
VALUES
('Mosque'),             -- building 16
('Church'),             -- building 17
('Temple'),             -- building 18
('Monastery / Convent');-- building 19

-- //// insert project details for the religious building ////
INSERT INTO projectDetails (projectTypeId, projectBuildingId, projectLevelId, minOverHeadCost, maxOverHeadCost)
VALUES
-- Mosque
(5, 16, 6, 8, 12),       -- small scale 8-12%
(5, 16, 7, 12, 18),      -- medium scale 12-18%
(5, 16, 8, 15, 22),      -- large scale 15-12%
-- Church
(5, 17, 7, 10, 16),      -- medium scale 10-16%
(5, 17, 8, 14, 20),      -- large scale 14-20%
-- Temple
(5, 18, 7, 12, 18),      -- medium scale 12-18%
(5, 18, 8, 15, 22),      -- large scale 15-22%
-- Monastery / Convent
(5, 19, 6, 8, 12),       -- small scale 8-12%
(5, 19, 7, 10, 16);      -- medium scale 10-16%

