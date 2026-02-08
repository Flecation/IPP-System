
-- ============================================================
-- Demo seed: assignWorkItemSkills + assignWorkItemSkillDetails
-- Purpose: make WorkItem Details -> Skills table show data.
--
-- Requires:
--   1) base schema loaded (tables.sql)
--   2) master data loaded (insertDataProjectDetails.sql)  -> skills + assignStatus + projectStatus exist
--   3) demo projects loaded (your 30 projects seed)       -> assignWorkItems exist
--
-- Run:
--   SOURCE demo_seed_workItemSkills.sql;
--   CALL seedWorkItemSkills(2, 4);  -- 2 to 4 skills per work item
-- ============================================================

DELIMITER $$

DROP PROCEDURE IF EXISTS seedWorkItemSkills$$
CREATE PROCEDURE seedWorkItemSkills(
    IN p_minSkillsPerWorkItem INT,
    IN p_maxSkillsPerWorkItem INT
)
BEGIN
    DECLARE v_done INT DEFAULT 0;
    DECLARE v_assignWorkItemId INT;
    DECLARE v_assignProjectId INT;
    DECLARE v_projectStatusName VARCHAR(255);
    DECLARE v_n INT;

    DECLARE cur CURSOR FOR
        SELECT awi.assignWorkItemId, awi.assignProjectId
        FROM assignWorkItems awi
        ORDER BY awi.assignWorkItemId;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_done = 1;

    OPEN cur;

    read_loop: LOOP
        FETCH cur INTO v_assignWorkItemId, v_assignProjectId;
        IF v_done = 1 THEN
            LEAVE read_loop;
        END IF;

        -- detect project status name (planning/inProgress/delay/finished)
        SELECT ps.projectStatusName
          INTO v_projectStatusName
        FROM assignProjects ap
        LEFT JOIN projectStatus ps ON ps.projectStatusId = ap.projectStatus
        WHERE ap.assignProjectId = v_assignProjectId
        LIMIT 1;

        SET v_n = FLOOR(RAND() * (p_maxSkillsPerWorkItem - p_minSkillsPerWorkItem + 1)) + p_minSkillsPerWorkItem;

        -- Insert v_n skills for this work item (avoid duplicates)
        INSERT INTO assignWorkItemSkills (assignWorkItemId, skillId, isCancel)
        SELECT v_assignWorkItemId, s.skillId, FALSE
        FROM (
            SELECT skillId
            FROM skills
            ORDER BY RAND()
            LIMIT 1000000
        ) s
        LEFT JOIN assignWorkItemSkills ex
               ON ex.assignWorkItemId = v_assignWorkItemId
              AND ex.skillId = s.skillId
        WHERE ex.assignWorkItemSkillId IS NULL
        ORDER BY RAND()
        LIMIT v_n;

        -- For each inserted assignWorkItemSkill row, add a "planned" detail (autoAssign)
        INSERT INTO assignWorkItemSkillDetails (assignWorkItemSkillId, assignStatusId, laborQty, dailyWagePerLabor)
        SELECT
            awis.assignWorkItemSkillId,
            (SELECT assignStatusId FROM assignStatus WHERE assignStatusName='autoAssign' LIMIT 1),
            ROUND(1 + (RAND() * 6), 1),              -- laborQty: 1.0 to 7.0
            ROUND(25000 + (RAND() * 45000), 0)       -- wage: 25,000 to 70,000
        FROM assignWorkItemSkills awis
        LEFT JOIN assignWorkItemSkillDetails d
               ON d.assignWorkItemSkillId = awis.assignWorkItemSkillId
              AND d.assignStatusId = (SELECT assignStatusId FROM assignStatus WHERE assignStatusName='autoAssign' LIMIT 1)
        WHERE awis.assignWorkItemId = v_assignWorkItemId
          AND d.assignWorkItemSkillDetailId IS NULL;

        -- For non-planning projects, add "actualResult" for some skills to make reports interesting
        IF v_projectStatusName IS NOT NULL AND LOWER(TRIM(v_projectStatusName)) <> 'planning' THEN
            INSERT INTO assignWorkItemSkillDetails (assignWorkItemSkillId, assignStatusId, laborQty, dailyWagePerLabor)
            SELECT
                awis.assignWorkItemSkillId,
                (SELECT assignStatusId FROM assignStatus WHERE assignStatusName='actualResult' LIMIT 1),
                ROUND(1 + (RAND() * 6), 1),
                ROUND(25000 + (RAND() * 50000), 0)
            FROM assignWorkItemSkills awis
            LEFT JOIN assignWorkItemSkillDetails d
                   ON d.assignWorkItemSkillId = awis.assignWorkItemSkillId
                  AND d.assignStatusId = (SELECT assignStatusId FROM assignStatus WHERE assignStatusName='actualResult' LIMIT 1)
            WHERE awis.assignWorkItemId = v_assignWorkItemId
              AND d.assignWorkItemSkillDetailId IS NULL
              AND RAND() < 0.70; -- 70% of skills get actualResult
        END IF;

    END LOOP;

    CLOSE cur;
END$$

DELIMITER ;

CALL seedWorkItemSkills(2, 4);