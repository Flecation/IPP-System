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


-- Substructure, Superstructure, Finishing, MEP, External
-- // in there the min and max of duration are the total month of % but the labors are not % they are qty //
INSERT INTO workItemDetails
(projectDetailId, projectWorkItemId, minDuration, maxDuration, minLabors, maxLabors, minCost, maxCost)
VALUES
-- Apartment Low Rise
(1,1,0.18,0.22,5,10,10*0.25,15*0.25),  -- Substructure
(1,2,0.33,0.38,8,15,10*0.35,15*0.35),  -- Superstructure
(1,3,0.23,0.27,5,10,10*0.20,15*0.20),  -- Finishing
(1,4,0.13,0.17,4,8,10*0.15,15*0.15),   -- MEP
(1,5,0.04,0.06,2,5,10*0.05,15*0.05),   -- External
-- Apartment Medium Rise
(2,1,0.18,0.22,6,12,12*0.25,18*0.25),
(2,2,0.33,0.38,10,18,12*0.35,18*0.35),
(2,3,0.23,0.27,6,12,12*0.20,18*0.20),
(2,4,0.13,0.17,5,10,12*0.15,18*0.15),
(2,5,0.04,0.06,3,6,12*0.05,18*0.05),
-- Apartment High Rise
(3,1,0.18,0.22,8,15,15*0.25,20*0.25),  -- Substructure
(3,2,0.33,0.38,12,22,15*0.35,20*0.35), -- Superstructure
(3,3,0.23,0.27,8,15,15*0.20,20*0.20),  -- Finishing
(3,4,0.13,0.17,6,12,15*0.15,20*0.15),  -- MEP
(3,5,0.04,0.06,4,8,15*0.05,20*0.05);   -- External
-- Condominium Medium Rise
(4,1,0.18,0.22,6,12,12*0.25,18*0.25),
(4,2,0.33,0.38,10,18,12*0.35,18*0.35),
(4,3,0.23,0.27,6,12,12*0.20,18*0.20),
(4,4,0.13,0.17,5,10,12*0.15,18*0.15),
(4,5,0.04,0.06,3,6,12*0.05,18*0.05),
-- Condominium High Rise
(5,1,0.18,0.22,8,15,15*0.25,22*0.25),  -- Substructure
(5,2,0.33,0.38,12,22,15*0.35,22*0.35), -- Superstructure
(5,3,0.23,0.27,8,15,15*0.20,22*0.20),  -- Finishing
(5,4,0.13,0.17,6,12,15*0.15,22*0.15),  -- MEP
(5,5,0.04,0.06,4,8,15*0.05,22*0.05);   -- External
-- Villa / House Low Rise
(6,1,0.18,0.22,4,8,8*0.25,12*0.25),
(6,2,0.33,0.38,6,12,8*0.35,12*0.35),
(6,3,0.23,0.27,4,8,8*0.20,12*0.20),
(6,4,0.13,0.17,3,6,8*0.15,12*0.15),
(6,5,0.04,0.06,2,4,8*0.05,12*0.05),
-- Townhouse Low Rise
(7,1,0.18,0.22,4,8,9*0.25,13*0.25),
(7,2,0.33,0.38,6,12,9*0.35,13*0.35),
(7,3,0.23,0.27,4,8,9*0.20,13*0.20),
(7,4,0.13,0.17,3,6,9*0.15,13*0.15),
(7,5,0.04,0.06,2,4,9*0.05,13*0.05),
-- Townhouse Medium Rise
(8,1,0.18,0.22,5,10,11*0.25,17*0.25),
(8,2,0.33,0.38,8,14,11*0.35,17*0.35),
(8,3,0.23,0.27,5,10,11*0.20,17*0.20),
(8,4,0.13,0.17,4,8,11*0.15,17*0.15),
(8,5,0.04,0.06,3,6,11*0.05,17*0.05);













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

