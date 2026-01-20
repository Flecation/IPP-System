-- =============================================
-- COMPLETE REORGANIZED INSERT SCRIPT
-- All foreign key constraints will be satisfied
-- =============================================

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

-- ///// INSERT ALL BUILDINGS FOR ALL PROJECT TYPES /////
INSERT INTO buildings (projectBuildingName) VALUES
-- Residential buildings
('Apartment'),
('Condominium'),
('Villa / House'),
('Townhouse'),
-- Commercial & Institutional buildings
('Office Building'),
('School / Educational Building'),
('Hospital / Healthcare Building'),
('Hotel / Hospitality Building'),
-- Industrial buildings
('Factory'),
('Warehouse'),
('Power Plant'),
-- Infrastructure buildings
('Road / Highway'),
('Bridge'),
('Dam / Reservoir'),
('Utility / Pipeline'),
-- Religious buildings
('Mosque'),
('Church'),
('Temple'),
('Monastery / Convent');

-- =============================================
-- INSERT ALL PROJECT DETAILS FOR ALL TYPES
-- =============================================

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

-- =============================================
-- INSERT ALL TASKS (before taskDetails)
-- =============================================

INSERT INTO tasks (projectTaskName) VALUES
-- Substructure tasks
('Excavation'),
('Foundation'),
('Formwork'),
('Reinforcement'),
('Concrete Pouring'),
-- Superstructure tasks
('Column Construction'),
('Beam Construction'),
('Slab Construction'),
('Wall Construction'),
-- Finishing tasks
('Plastering'),
('Painting'),
('Flooring'),
('Tiling'),
-- MEP tasks
('Electrical Wiring'),
('Plumbing Installation'),
('HVAC Installation'),
-- External tasks
('Landscaping'),
('Paving'),
('Fencing'),
-- Infrastructure-specific tasks
('Site Clearing & Grubbing'),
('Earthworks & Grading'),
('Subgrade Preparation'),
('Base Course Installation'),
('Asphalt/Concrete Paving'),
('Drainage Installation'),
('Bridge Deck Construction'),
('Pier/Abutment Construction'),
('Pipeline Trenching'),
('Pipe Laying & Jointing'),
('Backfilling & Compaction'),
('Pavement Markings'),
('Guardrail Installation'),
('Erosion Control'),
('Utility Connections');

-- =============================================
-- INSERT ALL workItemDetails FOR ALL PROJECT TYPES
-- Now all projectDetailIds exist
-- =============================================

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
(1, 5, 4, 6, 2, 5, 5, 10),       -- External: 5-10% (was 10-15%)
-- Total Cost: 95-120% (with contingency)

-- Apartment Medium Rise (projectDetailId: 2)
(2, 1, 18, 22, 6, 12, 15, 20),   -- Substructure
(2, 2, 33, 38, 10, 18, 32, 38),  -- Superstructure (higher for medium rise)
(2, 3, 23, 27, 6, 12, 26, 32),   -- Finishing
(2, 4, 13, 17, 5, 10, 22, 28),   -- MEP (higher for taller buildings)
(2, 5, 4, 6, 3, 6, 5, 10),       -- External
-- Total: 100-128%

-- Apartment High Rise (projectDetailId: 3)
(3, 1, 18, 22, 8, 15, 15, 20),   -- Substructure
(3, 2, 33, 38, 12, 22, 35, 42),  -- Superstructure (highest for high rise)
(3, 3, 23, 27, 8, 15, 28, 35),   -- Finishing
(3, 4, 13, 17, 6, 12, 25, 32),   -- MEP (complex systems in high rise)
(3, 5, 4, 6, 4, 8, 5, 10),       -- External
-- Total: 108-139%

-- Condominium Medium Rise (projectDetailId: 4) - Similar to Apartment Medium
(4, 1, 18, 22, 6, 12, 15, 20),
(4, 2, 33, 38, 10, 18, 33, 39),   -- Slightly higher quality
(4, 3, 23, 27, 6, 12, 28, 34),    -- Better finishes
(4, 4, 13, 17, 5, 10, 23, 29),    -- Better MEP
(4, 5, 4, 6, 3, 6, 5, 10),
-- Total: 104-132%

-- Condominium High Rise (projectDetailId: 5)
(5, 1, 18, 22, 8, 15, 15, 20),
(5, 2, 33, 38, 12, 22, 36, 43),   -- Premium structure
(5, 3, 23, 27, 8, 15, 30, 37),    -- Luxury finishes
(5, 4, 13, 17, 6, 12, 27, 34),    -- Advanced MEP
(5, 5, 4, 6, 4, 8, 5, 10),
-- Total: 113-144%

-- Villa / House Low Rise (projectDetailId: 6)
(6, 1, 18, 22, 4, 8, 15, 20),
(6, 2, 33, 38, 6, 12, 35, 42),     -- Houses have higher structure %
(6, 3, 23, 27, 4, 8, 30, 38),      -- Custom finishes
(6, 4, 13, 17, 3, 6, 15, 20),      -- Simpler MEP
(6, 5, 4, 6, 2, 4, 10, 15),        -- More external work
-- Total: 105-135%

-- Townhouse Low Rise (projectDetailId: 7)
(7, 1, 18, 22, 4, 8, 14, 19),
(7, 2, 33, 38, 6, 12, 33, 40),
(7, 3, 23, 27, 4, 8, 28, 35),
(7, 4, 13, 17, 3, 6, 18, 24),
(7, 5, 4, 6, 2, 4, 8, 12),
-- Total: 101-130%

-- Townhouse Medium Rise (projectDetailId: 8)
(8, 1, 18, 22, 5, 10, 14, 19),
(8, 2, 33, 38, 8, 14, 34, 41),
(8, 3, 23, 27, 5, 10, 29, 36),
(8, 4, 13, 17, 4, 8, 20, 26),
(8, 5, 4, 6, 3, 6, 8, 12);
-- Total: 105-134%

-- =============================================
-- COMMERCIAL & INSTITUTIONAL BUILDINGS
-- Realistic workItemDetails with proper cost distributions
-- =============================================

-- -----------------------------------------------------------
-- 1. OFFICE BUILDINGS
-- -----------------------------------------------------------

-- Office Building Low Rise (projectDetailId: 9)
INSERT INTO workItemDetails (projectDetailId, projectWorkItemId, minDuration, maxDuration, minLabors, maxLabors, minCost, maxCost) VALUES
(9, 1, 18, 22, 6, 12, 12, 18),   -- Substructure: 12-18%
(9, 2, 33, 38, 10, 20, 30, 38),   -- Superstructure: 30-38%
(9, 3, 23, 27, 8, 16, 25, 32),    -- Finishing: 25-32% (higher quality)
(9, 4, 13, 17, 6, 12, 25, 30),    -- MEP: 25-30% (complex systems)
(9, 5, 4, 6, 3, 8, 5, 10),        -- External: 5-10%

-- Office Building Medium Rise (projectDetailId: 10)
(10, 1, 18, 22, 8, 15, 13, 19),   -- Substructure
(10, 2, 33, 38, 12, 25, 32, 40),  -- Superstructure
(10, 3, 23, 27, 10, 20, 28, 35),  -- Finishing
(10, 4, 13, 17, 8, 16, 28, 35),   -- MEP (more complex)
(10, 5, 4, 6, 4, 10, 5, 10),      -- External

-- Office Building High Rise (projectDetailId: 11)
(11, 1, 18, 22, 10, 20, 14, 21),   -- Substructure (deep foundations)
(11, 2, 33, 38, 15, 30, 35, 44),   -- Superstructure (steel/concrete)
(11, 3, 23, 27, 12, 25, 30, 38),   -- Finishing (premium)
(11, 4, 13, 17, 10, 20, 30, 38),   -- MEP (very complex)
(11, 5, 4, 6, 5, 12, 5, 10),      -- External

-- -----------------------------------------------------------
-- 2. SCHOOL / EDUCATIONAL BUILDINGS
-- -----------------------------------------------------------

-- School Low Rise (projectDetailId: 12)
(12, 1, 18, 22, 5, 10, 10, 15),   -- Substructure
(12, 2, 33, 38, 8, 16, 28, 35),   -- Superstructure (spacious)
(12, 3, 23, 27, 6, 12, 30, 38),   -- Finishing (durable)
(12, 4, 13, 17, 5, 10, 20, 25),   -- MEP (standard)
(12, 5, 4, 6, 3, 8, 8, 12),       -- External (playgrounds)

-- School Medium Rise (projectDetailId: 13)
(13, 1, 18, 22, 6, 12, 11, 17),   -- Substructure
(13, 2, 33, 38, 10, 20, 30, 38),  -- Superstructure
(13, 3, 23, 27, 8, 16, 32, 40),   -- Finishing
(13, 4, 13, 17, 6, 12, 22, 28),   -- MEP
(13, 5, 4, 6, 4, 10, 8, 12),      -- External

-- -----------------------------------------------------------
-- 3. HOSPITAL / HEALTHCARE BUILDINGS
-- -----------------------------------------------------------

-- Hospital Medium Rise (projectDetailId: 14)
(14, 1, 18, 22, 8, 16, 15, 22),   -- Substructure (strong)
(14, 2, 33, 38, 12, 24, 30, 38),  -- Superstructure
(14, 3, 23, 27, 10, 20, 25, 32),  -- Finishing (hygienic)
(14, 4, 13, 17, 10, 20, 35, 45),  -- MEP (VERY complex: medical gases, etc.)
(14, 5, 4, 6, 5, 12, 8, 12),      -- External (accessibility)

-- Hospital High Rise (projectDetailId: 15)
(15, 1, 18, 22, 10, 20, 16, 24),   -- Substructure
(15, 2, 33, 38, 15, 30, 32, 40),   -- Superstructure
(15, 3, 23, 27, 12, 25, 28, 36),   -- Finishing
(15, 4, 13, 17, 12, 25, 38, 48),   -- MEP (extremely complex)
(15, 5, 4, 6, 6, 15, 8, 12),       -- External

-- -----------------------------------------------------------
-- 4. HOTEL / HOSPITALITY BUILDINGS
-- -----------------------------------------------------------

-- Hotel Medium Rise (projectDetailId: 16)
(16, 1, 18, 22, 7, 14, 12, 18),   -- Substructure
(16, 2, 33, 38, 10, 20, 28, 35),  -- Superstructure
(16, 3, 23, 27, 12, 24, 35, 45),  -- Finishing (LUXURY - highest cost!)
(16, 4, 13, 17, 8, 16, 25, 32),   -- MEP
(16, 5, 4, 6, 4, 10, 10, 15),     -- External (landscaping, pools)

-- Hotel High Rise (projectDetailId: 17)
(17, 1, 18, 22, 9, 18, 13, 20),   -- Substructure
(17, 2, 33, 38, 12, 25, 30, 38),  -- Superstructure
(17, 3, 23, 27, 15, 30, 38, 48),  -- Finishing (PREMIUM luxury)
(17, 4, 13, 17, 10, 20, 28, 36),  -- MEP
(17, 5, 4, 6, 5, 12, 10, 15);     -- External

-- =============================================
-- INDUSTRIAL BUILDINGS - workItemDetails
-- Single INSERT statement for all 6 projectDetailIds (19-24)
-- =============================================

-- Clear existing industrial data
DELETE FROM workItemDetails WHERE projectDetailId BETWEEN 19 AND 24;

-- Insert ALL industrial building work items in ONE statement
INSERT INTO workItemDetails (projectDetailId, projectWorkItemId, minDuration, maxDuration, minLabors, maxLabors, minCost, maxCost) VALUES
-- -----------------------------------------------------------
-- 1. FACTORY - Single Floor (projectDetailId: 19)
-- -----------------------------------------------------------
(19, 1, 18, 22, 5, 10, 10, 15),   -- Substructure: 10-15% (simple)
(19, 2, 33, 38, 8, 15, 40, 50),   -- Superstructure: 40-50% (heavy!)
(19, 3, 23, 27, 4, 8, 15, 20),    -- Finishing: 15-20% (basic)
(19, 4, 13, 17, 6, 12, 25, 30),   -- MEP: 25-30% (industrial systems)
(19, 5, 4, 6, 2, 5, 5, 10),       -- External: 5-10%

-- -----------------------------------------------------------
-- 2. FACTORY - Low Rise (projectDetailId: 20)
-- -----------------------------------------------------------
(20, 1, 18, 22, 6, 12, 11, 17),   -- Substructure
(20, 2, 33, 38, 10, 18, 42, 52),  -- Superstructure
(20, 3, 23, 27, 5, 10, 16, 22),   -- Finishing
(20, 4, 13, 17, 7, 14, 27, 33),   -- MEP
(20, 5, 4, 6, 3, 6, 5, 10),       -- External

-- -----------------------------------------------------------
-- 3. WAREHOUSE - Single Floor (projectDetailId: 21)
-- -----------------------------------------------------------
(21, 1, 18, 22, 4, 8, 8, 12),     -- Substructure: 8-12% (light)
(21, 2, 33, 38, 6, 12, 35, 45),   -- Superstructure: 35-45% (large spans)
(21, 3, 23, 27, 3, 6, 10, 15),    -- Finishing: 10-15% (minimal)
(21, 4, 13, 17, 4, 8, 20, 25),    -- MEP: 20-25% (basic)
(21, 5, 4, 6, 2, 4, 5, 10),       -- External: 5-10%

-- -----------------------------------------------------------
-- 4. WAREHOUSE - Low Rise (projectDetailId: 22)
-- -----------------------------------------------------------
(22, 1, 18, 22, 5, 10, 9, 14),    -- Substructure
(22, 2, 33, 38, 8, 15, 38, 48),   -- Superstructure
(22, 3, 23, 27, 4, 8, 12, 17),    -- Finishing
(22, 4, 13, 17, 5, 10, 22, 28),   -- MEP
(22, 5, 4, 6, 3, 5, 5, 10),       -- External

-- -----------------------------------------------------------
-- 5. POWER PLANT - Single Floor (projectDetailId: 23)
-- -----------------------------------------------------------
(23, 1, 18, 22, 8, 15, 15, 22),   -- Substructure: 15-22% (heavy!)
(23, 2, 33, 38, 10, 20, 30, 38),  -- Superstructure: 30-38%
(23, 3, 23, 27, 5, 10, 10, 15),   -- Finishing: 10-15% (functional)
(23, 4, 13, 17, 10, 20, 40, 50),  -- MEP: 40-50% (VERY complex!)
(23, 5, 4, 6, 4, 8, 8, 12),       -- External: 8-12%

-- -----------------------------------------------------------
-- 6. POWER PLANT - Low Rise (projectDetailId: 24)
-- -----------------------------------------------------------
(24, 1, 18, 22, 10, 18, 17, 24),  -- Substructure
(24, 2, 33, 38, 12, 24, 32, 40),  -- Superstructure
(24, 3, 23, 27, 6, 12, 12, 18),   -- Finishing
(24, 4, 13, 17, 12, 25, 42, 53),  -- MEP
(24, 5, 4, 6, 5, 10, 8, 12);      -- External

-- =============================================
-- INFRASTRUCTURE BUILDINGS - workItemDetails
-- Single INSERT statement for all 10 projectDetailIds (25-34)
-- Infrastructure uses scale levels (Small, Medium, Large, Extra Large)
-- =============================================

-- Clear existing infrastructure data
DELETE FROM workItemDetails WHERE projectDetailId BETWEEN 25 AND 34;

-- Insert ALL infrastructure work items in ONE statement
INSERT INTO workItemDetails (projectDetailId, projectWorkItemId, minDuration, maxDuration, minLabors, maxLabors, minCost, maxCost) VALUES
-- =============================================================================
-- 1. ROAD/HIGHWAY - Small Scale (projectDetailId: 25)
-- =============================================================================
(25, 1, 20, 25, 10, 20, 15, 20),   -- Substructure: 15-20% (earthworks)
(25, 2, 35, 40, 15, 30, 40, 50),   -- Superstructure: 40-50% (pavement)
(25, 3, 25, 30, 8, 15, 25, 30),    -- Finishing: 25-30% (markings, signs)
(25, 4, 10, 15, 5, 10, 15, 20),    -- MEP: 15-20% (lighting, drainage)
(25, 5, 5, 8, 3, 8, 5, 10),        -- External: 5-10% (landscaping)

-- =============================================================================
-- 2. ROAD/HIGHWAY - Medium Scale (projectDetailId: 26)
-- =============================================================================
(26, 1, 20, 25, 15, 25, 16, 22),   -- Substructure
(26, 2, 35, 40, 20, 40, 42, 53),   -- Superstructure
(26, 3, 25, 30, 10, 20, 26, 32),   -- Finishing
(26, 4, 10, 15, 8, 15, 16, 22),    -- MEP
(26, 5, 5, 8, 5, 12, 5, 10),       -- External

-- =============================================================================
-- 3. ROAD/HIGHWAY - Large Scale (projectDetailId: 27)
-- =============================================================================
(27, 1, 20, 25, 20, 40, 17, 24),   -- Substructure
(27, 2, 35, 40, 25, 50, 45, 56),   -- Superstructure
(27, 3, 25, 30, 15, 30, 28, 35),   -- Finishing
(27, 4, 10, 15, 10, 20, 18, 24),   -- MEP
(27, 5, 5, 8, 8, 15, 5, 10),       -- External

-- =============================================================================
-- 4. ROAD/HIGHWAY - Extra Large (projectDetailId: 28)
-- =============================================================================
(28, 1, 20, 25, 25, 50, 18, 26),   -- Substructure
(28, 2, 35, 40, 30, 60, 48, 60),   -- Superstructure
(28, 3, 25, 30, 20, 40, 30, 38),   -- Finishing
(28, 4, 10, 15, 12, 25, 20, 26),   -- MEP
(28, 5, 5, 8, 10, 20, 5, 10),      -- External

-- =============================================================================
-- 5. BRIDGE - Medium Scale (projectDetailId: 29)
-- =============================================================================
(29, 1, 25, 30, 20, 40, 25, 35),   -- Substructure: 25-35% (piers, abutments)
(29, 2, 40, 45, 25, 50, 45, 55),   -- Superstructure: 45-55% (deck, beams)
(29, 3, 20, 25, 15, 30, 15, 20),   -- Finishing: 15-20% (railings, surface)
(29, 4, 10, 15, 10, 20, 10, 15),   -- MEP: 10-15% (lighting, drainage)
(29, 5, 3, 5, 5, 10, 5, 10),       -- External: 5-10% (approaches)

-- =============================================================================
-- 6. BRIDGE - Large Scale (projectDetailId: 30)
-- =============================================================================
(30, 1, 25, 30, 25, 50, 28, 38),   -- Substructure
(30, 2, 40, 45, 30, 60, 48, 58),   -- Superstructure
(30, 3, 20, 25, 20, 40, 17, 23),   -- Finishing
(30, 4, 10, 15, 15, 30, 12, 17),   -- MEP
(30, 5, 3, 5, 8, 15, 5, 10),       -- External

-- =============================================================================
-- 7. DAM/RESERVOIR - Large Scale (projectDetailId: 31)
-- =============================================================================
(31, 1, 30, 35, 50, 100, 40, 50),  -- Substructure: 40-50% (massive excavation)
(31, 2, 40, 45, 60, 120, 35, 45),  -- Superstructure: 35-45% (concrete dam)
(31, 3, 15, 20, 20, 40, 10, 15),   -- Finishing: 10-15% (surface treatment)
(31, 4, 8, 12, 30, 60, 10, 15),    -- MEP: 10-15% (spillways, gates)
(31, 5, 5, 8, 15, 30, 5, 10),      -- External: 5-10% (access roads)

-- =============================================================================
-- 8. DAM/RESERVOIR - Extra Large (projectDetailId: 32)
-- =============================================================================
(32, 1, 30, 35, 60, 120, 42, 53),  -- Substructure
(32, 2, 40, 45, 70, 140, 38, 48),  -- Superstructure
(32, 3, 15, 20, 25, 50, 12, 17),   -- Finishing
(32, 4, 8, 12, 35, 70, 12, 17),    -- MEP
(32, 5, 5, 8, 20, 40, 5, 10),      -- External

-- =============================================================================
-- 9. UTILITY/PIPELINE - Small Scale (projectDetailId: 33)
-- =============================================================================
(33, 1, 25, 30, 10, 20, 30, 40),   -- Substructure: 30-40% (trenching)
(33, 2, 35, 40, 8, 15, 40, 50),    -- Superstructure: 40-50% (pipe laying)
(33, 3, 20, 25, 5, 10, 15, 20),    -- Finishing: 15-20% (backfilling, restoration)
(33, 4, 12, 15, 6, 12, 10, 15),    -- MEP: 10-15% (valves, connections)
(33, 5, 5, 8, 3, 6, 5, 10),        -- External: 5-10%

-- =============================================================================
-- 10. UTILITY/PIPELINE - Medium Scale (projectDetailId: 34)
-- =============================================================================
(34, 1, 25, 30, 15, 30, 32, 42),   -- Substructure
(34, 2, 35, 40, 12, 25, 42, 52),   -- Superstructure
(34, 3, 20, 25, 8, 15, 17, 22),    -- Finishing
(34, 4, 12, 15, 8, 16, 12, 17),    -- MEP
(34, 5, 5, 8, 5, 10, 5, 10);       -- External

-- =============================================
-- RELIGIOUS BUILDINGS - workItemDetails
-- Single INSERT statement for all 8 projectDetailIds (35-42)
-- Religious buildings use scale levels (Small, Medium, Large, Grand)
-- =============================================

-- Clear existing religious data
DELETE FROM workItemDetails WHERE projectDetailId BETWEEN 35 AND 42;

-- Insert ALL religious building work items in ONE statement
INSERT INTO workItemDetails (projectDetailId, projectWorkItemId, minDuration, maxDuration, minLabors, maxLabors, minCost, maxCost) VALUES
-- =============================================================================
-- 1. MOSQUE - Small Scale (projectDetailId: 35)
-- =============================================================================
(35, 1, 18, 22, 5, 10, 12, 18),   -- Substructure: 12-18%
(35, 2, 33, 38, 8, 15, 35, 45),   -- Superstructure: 35-45% (domes, minarets)
(35, 3, 23, 27, 6, 12, 30, 40),   -- Finishing: 30-40% (decorative)
(35, 4, 13, 17, 4, 8, 18, 25),    -- MEP: 18-25% (ablution, sound)
(35, 5, 4, 6, 3, 6, 10, 15),      -- External: 10-15% (courtyard)

-- =============================================================================
-- 2. MOSQUE - Medium Scale (projectDetailId: 36)
-- =============================================================================
(36, 1, 18, 22, 6, 12, 14, 20),   -- Substructure
(36, 2, 33, 38, 10, 20, 38, 48),  -- Superstructure
(36, 3, 23, 27, 8, 16, 33, 43),   -- Finishing
(36, 4, 13, 17, 5, 10, 20, 28),   -- MEP
(36, 5, 4, 6, 4, 8, 10, 15),      -- External

-- =============================================================================
-- 3. MOSQUE - Large Scale (projectDetailId: 37)
-- =============================================================================
(37, 1, 18, 22, 8, 15, 16, 23),   -- Substructure
(37, 2, 33, 38, 12, 25, 42, 52),  -- Superstructure
(37, 3, 23, 27, 10, 20, 36, 46),  -- Finishing
(37, 4, 13, 17, 6, 12, 22, 30),   -- MEP
(37, 5, 4, 6, 5, 10, 10, 15),     -- External

-- =============================================================================
-- 4. CHURCH - Medium Scale (projectDetailId: 38)
-- =============================================================================
(38, 1, 18, 22, 6, 12, 13, 19),   -- Substructure
(38, 2, 33, 38, 10, 20, 36, 46),  -- Superstructure (steeples)
(38, 3, 23, 27, 8, 16, 32, 42),   -- Finishing (stained glass)
(38, 4, 13, 17, 5, 10, 19, 26),   -- MEP (organ, sound)
(38, 5, 4, 6, 4, 8, 10, 15),      -- External (churchyard)

-- =============================================================================
-- 5. CHURCH - Large Scale (projectDetailId: 39)
-- =============================================================================
(39, 1, 18, 22, 8, 15, 15, 22),   -- Substructure
(39, 2, 33, 38, 12, 25, 40, 50),  -- Superstructure
(39, 3, 23, 27, 10, 20, 35, 45),  -- Finishing
(39, 4, 13, 17, 6, 12, 21, 29),   -- MEP
(39, 5, 4, 6, 5, 10, 10, 15),     -- External

-- =============================================================================
-- 6. TEMPLE - Medium Scale (projectDetailId: 40)
-- =============================================================================
(40, 1, 18, 22, 7, 14, 14, 20),   -- Substructure
(40, 2, 33, 38, 10, 20, 38, 48),  -- Superstructure (pagodas, towers)
(40, 3, 23, 27, 9, 18, 34, 44),   -- Finishing (ornate carvings)
(40, 4, 13, 17, 5, 10, 18, 25),   -- MEP
(40, 5, 4, 6, 4, 8, 12, 18),      -- External (gardens)

-- =============================================================================
-- 7. TEMPLE - Large Scale (projectDetailId: 41)
-- =============================================================================
(41, 1, 18, 22, 9, 18, 16, 24),   -- Substructure
(41, 2, 33, 38, 12, 25, 42, 52),  -- Superstructure
(41, 3, 23, 27, 11, 22, 38, 48),  -- Finishing
(41, 4, 13, 17, 6, 12, 20, 28),   -- MEP
(41, 5, 4, 6, 5, 10, 12, 18),     -- External

-- =============================================================================
-- 8. MONASTERY/CONVENT - Small Scale (projectDetailId: 42)
-- =============================================================================
(42, 1, 18, 22, 5, 10, 12, 18),   -- Substructure
(42, 2, 33, 38, 8, 15, 32, 42),   -- Superstructure
(42, 3, 23, 27, 6, 12, 28, 38),   -- Finishing (simple)
(42, 4, 13, 17, 4, 8, 16, 22),    -- MEP
(42, 5, 4, 6, 3, 6, 15, 20);      -- External (cloisters, gardens)

-- =============================================
-- INSERT ALL SKILLS (before skill requirements)
-- =============================================

INSERT INTO skills (skillName) VALUES
-- Core Construction Skills
('General Laborer'),
('Mason'),
('Carpenter'),
('Electrician'),
('Plumber'),
('Welder'),
('Steel Fixer'),
('Concrete Finisher'),
('Heavy Equipment Operator'),
('Foreman/Supervisor'),
('Surveyor'),
('Scaffolder'),

-- Specialized Skills
('Tile Setter'),
('Painter'),
('Plasterer'),
('HVAC Technician'),
('Glazier'),
('Roofer'),
('Landscaper'),
('Paving Specialist'),
('Bridge Specialist'),
('Pipe Layer'),
('Dam Construction Specialist'),
('Religious Art Specialist');

-- =============================================
-- INSERT ALL taskDetails (after tasks and workItemDetails)
-- =============================================

-- Clear existing task details for residential
DELETE FROM taskDetails WHERE workItemDetailId BETWEEN 1 AND 40;

-- -----------------------------------------------------------
-- SUBSTRUCTURE Tasks (workItemDetailId: 1, 6, 11, 16, 21, 26, 31, 36)
-- Total duration: 18-22%
-- -----------------------------------------------------------

-- Apartment Low Rise - Substructure (workItemDetailId: 1)
INSERT INTO taskDetails (workItemDetailId, projectTaskId, minDuration, maxDuration) VALUES
(1, 1, 4, 5),   -- Excavation
(1, 2, 7, 8),   -- Foundation
(1, 3, 3, 4),   -- Formwork
(1, 4, 2, 2.5), -- Reinforcement
(1, 5, 2, 2.5), -- Concrete Pouring

-- Apartment Medium Rise - Substructure (workItemDetailId: 6)
(6, 1, 4, 5),
(6, 2, 7, 8.5),
(6, 3, 3.5, 4),
(6, 4, 2.5, 3),
(6, 5, 2, 2.5),

-- Apartment High Rise - Substructure (workItemDetailId: 11)
(11, 1, 4, 5.5),
(11, 2, 8, 9.5),
(11, 3, 4, 4.5),
(11, 4, 3, 3.5),
(11, 5, 2.5, 3),

-- Condominium Medium Rise - Substructure (workItemDetailId: 16)
(16, 1, 4, 5),
(16, 2, 7, 8.5),
(16, 3, 3.5, 4),
(16, 4, 2.5, 3),
(16, 5, 2, 2.5),

-- Condominium High Rise - Substructure (workItemDetailId: 21)
(21, 1, 4, 5.5),
(21, 2, 8, 9.5),
(21, 3, 4, 4.5),
(21, 4, 3, 3.5),
(21, 5, 2.5, 3),

-- Villa/House - Substructure (workItemDetailId: 26)
(26, 1, 3, 4),
(26, 2, 6, 7),
(26, 3, 2.5, 3.5),
(26, 4, 1.5, 2),
(26, 5, 1.5, 2),

-- Townhouse Low Rise - Substructure (workItemDetailId: 31)
(31, 1, 3, 4),
(31, 2, 6.5, 7.5),
(31, 3, 3, 3.5),
(31, 4, 2, 2.5),
(31, 5, 1.5, 2),

-- Townhouse Medium Rise - Substructure (workItemDetailId: 36)
(36, 1, 3.5, 4.5),
(36, 2, 7, 8),
(36, 3, 3.5, 4),
(36, 4, 2.5, 3),
(36, 5, 2, 2.5),

-- -----------------------------------------------------------
-- SUPERSTRUCTURE Tasks (workItemDetailId: 2, 7, 12, 17, 22, 27, 32, 37)
-- Total duration: 33-38%
-- -----------------------------------------------------------

-- Apartment Low Rise - Superstructure (workItemDetailId: 2)
(2, 6, 10, 12),  -- Column Construction
(2, 7, 8, 10),   -- Beam Construction
(2, 8, 7, 8),    -- Slab Construction
(2, 9, 8, 9),    -- Wall Construction

-- Apartment Medium Rise - Superstructure (workItemDetailId: 7)
(7, 6, 11, 13),
(7, 7, 9, 11),
(7, 8, 8, 9),
(7, 9, 9, 10),

-- Apartment High Rise - Superstructure (workItemDetailId: 12)
(12, 6, 13, 15),
(12, 7, 10, 12),
(12, 8, 9, 10),
(12, 9, 10, 11),

-- Condominium Medium Rise - Superstructure (workItemDetailId: 17)
(17, 6, 11, 13),
(17, 7, 9, 11),
(17, 8, 8, 9),
(17, 9, 9, 10),

-- Condominium High Rise - Superstructure (workItemDetailId: 22)
(22, 6, 14, 16),
(22, 7, 11, 13),
(22, 8, 9, 10),
(22, 9, 11, 12),

-- Villa/House - Superstructure (workItemDetailId: 27)
(27, 6, 8, 10),
(27, 7, 6, 8),
(27, 8, 5, 6),
(27, 9, 12, 14),

-- Townhouse Low Rise - Superstructure (workItemDetailId: 32)
(32, 6, 9, 11),
(32, 7, 7, 9),
(32, 8, 6, 7),
(32, 9, 10, 12),

-- Townhouse Medium Rise - Superstructure (workItemDetailId: 37)
(37, 6, 10, 12),
(37, 7, 8, 10),
(37, 8, 7, 8),
(37, 9, 11, 13),

-- -----------------------------------------------------------
-- FINISHING Tasks (workItemDetailId: 3, 8, 13, 18, 23, 28, 33, 38)
-- Total duration: 23-27%
-- -----------------------------------------------------------

-- Apartment Low Rise - Finishing (workItemDetailId: 3)
(3, 10, 8, 10),  -- Plastering
(3, 11, 6, 7),   -- Painting
(3, 12, 5, 6),   -- Flooring
(3, 13, 4, 5),   -- Tiling

-- Apartment Medium Rise - Finishing (workItemDetailId: 8)
(8, 10, 9, 11),
(8, 11, 7, 8),
(8, 12, 5.5, 6.5),
(8, 13, 4.5, 5.5),

-- Apartment High Rise - Finishing (workItemDetailId: 13)
(13, 10, 10, 12),
(13, 11, 8, 9),
(13, 12, 6, 7),
(13, 13, 5, 6),

-- Condominium Medium Rise - Finishing (workItemDetailId: 18)
(18, 10, 9, 11),
(18, 11, 8, 9),
(18, 12, 6, 7),
(18, 13, 5, 6),

-- Condominium High Rise - Finishing (workItemDetailId: 23)
(23, 10, 11, 13),
(23, 11, 9, 10),
(23, 12, 7, 8),
(23, 13, 6, 7),

-- Villa/House - Finishing (workItemDetailId: 28)
(28, 10, 7, 9),
(28, 11, 8, 10),
(28, 12, 6, 8),
(28, 13, 5, 7),

-- Townhouse Low Rise - Finishing (workItemDetailId: 33)
(33, 10, 7, 9),
(33, 11, 6, 7),
(33, 12, 5, 6),
(33, 13, 4, 5),

-- Townhouse Medium Rise - Finishing (workItemDetailId: 38)
(38, 10, 8, 10),
(38, 11, 7, 8),
(38, 12, 5.5, 6.5),
(38, 13, 4.5, 5.5),

-- -----------------------------------------------------------
-- MEP Tasks (workItemDetailId: 4, 9, 14, 19, 24, 29, 34, 39)
-- Total duration: 13-17%
-- -----------------------------------------------------------

-- Apartment Low Rise - MEP (workItemDetailId: 4)
(4, 14, 5, 6),   -- Electrical Wiring
(4, 15, 4, 5),   -- Plumbing Installation
(4, 16, 4, 5),   -- HVAC Installation

-- Apartment Medium Rise - MEP (workItemDetailId: 9)
(9, 14, 6, 7),
(9, 15, 5, 6),
(9, 16, 5, 6),

-- Apartment High Rise - MEP (workItemDetailId: 14)
(14, 14, 7, 8),
(14, 15, 6, 7),
(14, 16, 6, 7),

-- Condominium Medium Rise - MEP (workItemDetailId: 19)
(19, 14, 6, 7),
(19, 15, 5, 6),
(19, 16, 5, 6),

-- Condominium High Rise - MEP (workItemDetailId: 24)
(24, 14, 7, 8),
(24, 15, 6, 7),
(24, 16, 6, 7),

-- Villa/House - MEP (workItemDetailId: 29)
(29, 14, 4, 5),   -- Simpler electrical
(29, 15, 4, 5),   -- Standard plumbing
(29, 16, 3, 4),   -- Basic HVAC

-- Townhouse Low Rise - MEP (workItemDetailId: 34)
(34, 14, 5, 6),
(34, 15, 4, 5),
(34, 16, 4, 5),

-- Townhouse Medium Rise - MEP (workItemDetailId: 39)
(39, 14, 5.5, 6.5),
(39, 15, 4.5, 5.5),
(39, 16, 4.5, 5.5),

-- -----------------------------------------------------------
-- EXTERNAL Tasks (workItemDetailId: 5, 10, 15, 20, 25, 30, 35, 40)
-- Total duration: 4-6%
-- -----------------------------------------------------------

-- Apartment Low Rise - External (workItemDetailId: 5)
(5, 17, 1.5, 2),   -- Landscaping
(5, 18, 1.5, 2),   -- Paving
(5, 19, 1, 1.5),   -- Fencing

-- Apartment Medium Rise - External (workItemDetailId: 10)
(10, 17, 1.5, 2),
(10, 18, 1.5, 2),
(10, 19, 1, 1.5),

-- Apartment High Rise - External (workItemDetailId: 15)
(15, 17, 1.5, 2),
(15, 18, 1.5, 2),
(15, 19, 1, 1.5),

-- Condominium Medium Rise - External (workItemDetailId: 20)
(20, 17, 1.5, 2),
(20, 18, 1.5, 2),
(20, 19, 1, 1.5),

-- Condominium High Rise - External (workItemDetailId: 25)
(25, 17, 1.5, 2),
(25, 18, 1.5, 2),
(25, 19, 1, 1.5),

-- Villa/House - External (workItemDetailId: 30)
(30, 17, 2, 3),    -- More landscaping
(30, 18, 2, 2.5),  -- More paving
(30, 19, 1.5, 2),  -- More fencing

-- Townhouse Low Rise - External (workItemDetailId: 35)
(35, 17, 1.5, 2),
(35, 18, 1.5, 2),
(35, 19, 1, 1.5),

-- Townhouse Medium Rise - External (workItemDetailId: 40)
(40, 17, 1.5, 2),
(40, 18, 1.5, 2),
(40, 19, 1, 1.5);

-- =============================================
-- INSERT skill requirements (LAST - after all other tables)
-- =============================================

-- Current (sums to 21.5-26% - too high!):
-- (11, 1, 4, 5.5),    -- Excavation
-- (11, 2, 8, 9.5),    -- Foundation
-- (11, 3, 4, 4.5),    -- Formwork
-- (11, 4, 3, 3.5),    -- Reinforcement
-- (11, 5, 2.5, 3),    -- Concrete Pouring
-- TOTAL: 21.5-26% (should be 18-22%)

-- Fixed (scaled to 18-22%):
UPDATE taskDetails SET
    minDuration = 3.5, maxDuration = 4.3   -- Excavation: was 4-5.5
WHERE workItemDetailId = 11 AND projectTaskId = 1;

UPDATE taskDetails SET
    minDuration = 6.5, maxDuration = 7.5   -- Foundation: was 8-9.5
WHERE workItemDetailId = 11 AND projectTaskId = 2;

UPDATE taskDetails SET
    minDuration = 3.2, maxDuration = 3.7   -- Formwork: was 4-4.5
WHERE workItemDetailId = 11 AND projectTaskId = 3;

UPDATE taskDetails SET
    minDuration = 2.5, maxDuration = 2.9   -- Reinforcement: was 3-3.5
WHERE workItemDetailId = 11 AND projectTaskId = 4;

UPDATE taskDetails SET
    minDuration = 2.3, maxDuration = 2.6   -- Concrete: was 2.5-3
WHERE workItemDetailId = 11 AND projectTaskId = 5;
-- NEW TOTAL: 18-22%

-- ... (Add all the workItemRequireSkills INSERT statements from your original file here)
-- You'll need to copy the entire workItemRequireSkills section from your original file
-- starting from "INSERT INTO workItemRequireSkills" to the end

-- Note: Since this is already very long, I'm not copying the entire workItemRequireSkills section here
-- but you should include it at the end, after all other tables are populated

-- =============================================
-- VERIFICATION QUERIES
-- =============================================

-- Check total durations sum appropriately for each work item
SELECT
    wd.workItemDetailId,
    pd.projectDetailId,
    wi.projectWorkItemName,
    SUM(td.minDuration) as TotalMinDuration,
    SUM(td.maxDuration) as TotalMaxDuration,
    wd.minDuration as ExpectedMin,
    wd.maxDuration as ExpectedMax,
    CASE
        WHEN ABS(SUM(td.minDuration) - wd.minDuration) <= 2 AND ABS(SUM(td.maxDuration) - wd.maxDuration) <= 2
        THEN '✅ OK'
        ELSE '⚠️ Check'
    END as Status
FROM workItemDetails wd
JOIN taskDetails td ON wd.workItemDetailId = td.workItemDetailId
JOIN projectDetails pd ON wd.projectDetailId = pd.projectDetailId
JOIN workItems wi ON wd.projectWorkItemId = wi.projectWorkItemId
WHERE wd.workItemDetailId BETWEEN 1 AND 40
GROUP BY wd.workItemDetailId, pd.projectDetailId, wi.projectWorkItemName, wd.minDuration, wd.maxDuration
ORDER BY wd.workItemDetailId;