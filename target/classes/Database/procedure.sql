-- for the labor procedure
DELIMITER $$

CREATE PROCEDURE getAllLabors()
BEGIN
    SELECT
        l.laborId,
        l.laborName,
        l.laborNRC,
        l.laborPhone,
        l.laborStartDate,
        l.laborEndDate,
        l.isActive,
        s.skillId,
        s.skillName
    FROM labors l
    LEFT JOIN skills s ON l.skillId = s.skillId
    ORDER BY
        l.isActive DESC,
        l.laborName;
END$$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE getAllLaborsByProjectId(IN p_assignProjectId INT)
BEGIN
    SELECT
        l.laborId,
        s.skillName,
        l.laborName,
        l.laborNRC,
        l.laborPhone,
        l.laborStartDate,
        l.laborEndDate
    FROM assignWorkers aw
    INNER JOIN labors l
        ON l.laborId = aw.workerId
    LEFT JOIN skills s
        ON s.skillId = l.skillId
    WHERE aw.assignProjectId = p_assignProjectId
      AND (aw.isCancel IS NULL OR aw.isCancel = FALSE);
END$$

DELIMITER ;

-- for the building procedures

DELIMITER $$

CREATE PROCEDURE getBuildingsByProjectType(
    IN p_projectTypeId INT
)
BEGIN
    SELECT DISTINCT
        b.projectBuildingId,
        b.projectBuildingName
    FROM projectDetails pd
    INNER JOIN buildings b
        ON b.projectBuildingId = pd.projectBuildingId
    WHERE pd.projectTypeId = p_projectTypeId
    ORDER BY b.projectBuildingName;
END$$

DELIMITER ;

 DELIMITER $$

 CREATE PROCEDURE getBuildingNameByProjectId(
     IN p_projectTypeId INT
 )
 BEGIN
     SELECT DISTINCT
         b.projectBuildingId,
         b.projectBuildingName
     FROM projectDetails pd
     INNER JOIN buildings b
         ON b.projectBuildingId = pd.projectBuildingId
     WHERE pd.projectTypeId = p_projectTypeId
     ORDER BY b.projectBuildingName;
 END$$

 DELIMITER ;

-- for the level procedures

DELIMITER $$

CREATE PROCEDURE getLevelByProjectId(
    IN p_projectTypeId INT
)
BEGIN
    SELECT
        pl.projectLevelId,
        pl.projectLevelName
    FROM ProjectDetails pd
    INNER JOIN projectLevels pl
        ON pl.projectLevelId = pd.projectLevelId
    WHERE pd.projectTypeId = p_projectTypeId;
END$$

DELIMITER ;

-- for the skill procedures
DELIMITER $$

CREATE PROCEDURE getSkillByWorkItem(
    IN p_projectTypeId INT,
    IN p_workItemId INT
)
BEGIN
    SELECT
        s.skillId,
        s.skillName,
        wirs.minRequireLabors,
        wirs.maxRequireLabors,
        wirs.minDailyWage,
        wirs.maxDailyWage

    FROM projectDetails pd
    INNER JOIN workItemDetails wid
        ON wid.projectDetailId = pd.projectDetailId
    INNER JOIN workItemRequireSkills wirs
        ON wirs.workItemDetailId = wid.workItemDetailId
    INNER JOIN skills s
        ON s.skillId = wirs.skillId

    WHERE pd.projectTypeId = p_projectTypeId
      AND wid.projectWorkItemId = p_workItemId;
END$$

DELIMITER ;

DELIMITER $$

DROP PROCEDURE IF EXISTS getAllTasksByAssignWorkItem$$
CREATE PROCEDURE getAllTasksByAssignWorkItem(IN p_assignWorkItemId INT)
BEGIN
SELECT
    at.assignTaskId,
    t.projectTaskName AS taskName,
    at.plannedQty,
    at.unitOfMeasure,
    ps.projectStatusName AS taskStatus,

    -- planned (latest: auto/custom/extra)
    plan_atd.taskDuration  AS plannedDuration,
    plan_atd.startDate     AS plannedStartDate,
    plan_atd.endDate       AS plannedEndDate,

    -- actual (latest: actualResult)
    act_atd.taskDuration   AS actualDuration,
    act_atd.startDate      AS actualStartDate,
    act_atd.endDate        AS actualEndDate

FROM assigntasks at
    JOIN tasks t ON t.projectTaskId = at.projectTaskId
    LEFT JOIN projectstatus ps ON ps.projectStatusId = at.taskStatus

    LEFT JOIN (
    SELECT d1.*
    FROM assigntaskdetails d1
    JOIN assignstatus s1 ON s1.assignStatusId = d1.assignStatusId
    WHERE s1.assignStatusName IN ('autoAssign','customAssign','extraAssign')
    AND d1.assignTaskDetailId = (
    SELECT MAX(d2.assignTaskDetailId)
    FROM assigntaskdetails d2
    JOIN assignstatus s2 ON s2.assignStatusId = d2.assignStatusId
    WHERE d2.assignTaskId = d1.assignTaskId
    AND s2.assignStatusName IN ('autoAssign','customAssign','extraAssign')
    )
    ) plan_atd ON plan_atd.assignTaskId = at.assignTaskId

    LEFT JOIN (
    SELECT d1.*
    FROM assigntaskdetails d1
    JOIN assignstatus s1 ON s1.assignStatusId = d1.assignStatusId
    WHERE s1.assignStatusName = 'actualResult'
    AND d1.assignTaskDetailId = (
    SELECT MAX(d2.assignTaskDetailId)
    FROM assigntaskdetails d2
    JOIN assignstatus s2 ON s2.assignStatusId = d2.assignStatusId
    WHERE d2.assignTaskId = d1.assignTaskId
    AND s2.assignStatusName = 'actualResult'
    )
    ) act_atd ON act_atd.assignTaskId = at.assignTaskId

WHERE at.assignWorkItemId = p_assignWorkItemId
  AND at.isCancel = 0;
END$$

DELIMITER ;



DELIMITER $$

CREATE PROCEDURE getAllTasksDetailsByWorkItem(
    IN p_projectTypeId INT,
    IN p_workItemId INT,
    IN p_projectBuildingId INT,  -- New parameter
    IN p_projectLevelId INT      -- New parameter
)
BEGIN
    SELECT DISTINCT
        pd.projectTypeId,
        wid.projectWorkItemId,
        td.projectTaskId,
        t.projectTaskName,
        td.minDuration,
        td.maxDuration
    FROM projectDetails pd
    INNER JOIN workItemDetails wid
        ON wid.projectDetailId = pd.projectDetailId
    INNER JOIN taskDetails td
        ON td.workItemDetailId = wid.workItemDetailId
    INNER JOIN tasks t
        ON t.projectTaskId = td.projectTaskId
    WHERE pd.projectTypeId = p_projectTypeId
      AND wid.projectWorkItemId = p_workItemId
      AND pd.projectBuildingId = p_projectBuildingId  -- Added
      AND pd.projectLevelId = p_projectLevelId;       -- Added
END$$

DELIMITER ;

-- adding the extra or custom data for the tasks
DELIMITER $$

CREATE PROCEDURE addTaskDetailRecord(
    IN p_assignTaskId INT,
    IN p_duration DOUBLE,
    IN p_startDate DATE,
    IN p_endDate DATE,
    IN p_statusName VARCHAR(255)
)
BEGIN
    DECLARE v_statusId INT;
    DECLARE v_assignTaskDetailId INT;

    SELECT assignStatusId
    INTO v_statusId
    FROM assignStatus
    WHERE assignStatusName = p_statusName
    LIMIT 1;

    IF v_statusId IS NULL THEN
        SELECT FALSE AS success;
    ELSE
        SELECT atd.assignTaskDetailId INTO v_assignTaskDetailId
        FROM assignTaskDetails atd
        WHERE atd.assignTaskId = p_assignTaskId
          AND atd.assignStatusId = v_statusId;

        IF v_assignTaskDetailId IS NOT NULL THEN
            UPDATE assignTaskDetails
            SET taskDuration = p_duration, startDate = p_startDate, endDate = p_endDate
            WHERE assignTaskDetailId = v_assignTaskDetailId;

            SELECT TRUE AS success;
        ELSE
            INSERT INTO assignTaskDetails(assignTaskId, assignStatusId, taskDuration, startDate, endDate)
            VALUES (p_assignTaskId, v_statusId, p_duration, p_startDate, p_endDate);

            SELECT TRUE AS success;
        END IF;
    END IF;
END$$

DELIMITER ;

-- new adding the assign task
DELIMITER $$

CREATE PROCEDURE assignTaskToWorkItem(
    IN p_assignProjectId INT,
    IN p_projectWorkItemId INT,
    IN p_projectTaskId INT,
    IN p_duration DOUBLE,
    IN p_startDate DATE,
    IN p_endDate DATE,
    IN p_plannedQty DOUBLE,
    IN p_unitOfMeasure VARCHAR(50),
    IN p_projectStatusName VARCHAR(255),
    IN p_assignStatusName VARCHAR(255)
)
BEGIN
    DECLARE v_assignWorkItemId INT;
    DECLARE v_assignTaskId INT;
    DECLARE v_assignStatusId INT;
    DECLARE v_taskStatusId INT;
    DECLARE v_workItemStatusId INT;
    DECLARE v_defaultAssignStatusId INT;

    -- Get or create assignWorkItemId
    SELECT assignWorkItemId
    INTO v_assignWorkItemId
    FROM assignWorkItems
    WHERE assignProjectId = p_assignProjectId
      AND projectWorkItemId = p_projectWorkItemId
    LIMIT 1;

    -- Get project status ID for task and work item
    SELECT projectStatusId
    INTO v_taskStatusId
    FROM projectStatus
    WHERE projectStatusName = p_projectStatusName
    LIMIT 1;

    -- If work item doesn't exist, create it with proper status
    IF v_assignWorkItemId IS NULL THEN
        INSERT INTO assignWorkItems(assignProjectId, projectWorkItemId, workItemStatus)
        VALUES (p_assignProjectId, p_projectWorkItemId, v_taskStatusId);
        SET v_assignWorkItemId = LAST_INSERT_ID();
    END IF;

    -- Get assign status ID
    SELECT assignStatusId
    INTO v_assignStatusId
    FROM assignStatus
    WHERE assignStatusName = p_assignStatusName
    LIMIT 1;

    -- If assign status not found, use 'autoAssign' as default
    IF v_assignStatusId IS NULL THEN
        SELECT assignStatusId
        INTO v_assignStatusId
        FROM assignStatus
        WHERE assignStatusName = 'autoAssign'
        LIMIT 1;
    END IF;

    -- Insert task
    INSERT INTO assignTasks(assignWorkItemId, projectTaskId, taskStatus, plannedQty, unitOfMeasure)
    VALUES (v_assignWorkItemId, p_projectTaskId, v_taskStatusId, p_plannedQty, p_unitOfMeasure);
    SET v_assignTaskId = LAST_INSERT_ID();

    -- Insert task details
    INSERT INTO assignTaskDetails(assignTaskId, assignStatusId, taskDuration, startDate, endDate)
    VALUES (v_assignTaskId, v_assignStatusId, p_duration, p_startDate, p_endDate);

    SELECT TRUE AS success, v_assignTaskId AS newTaskId;
END$$

DELIMITER ;

-- for the work items procedures

DELIMITER $$

CREATE PROCEDURE getAllWorkItemDetails(
    IN p_projectTypeId INT,
    IN p_buildingId INT,
    IN p_levelId INT
)
BEGIN
    SELECT
        pd.projectTypeId,
        wid.projectWorkItemId,
        wi.projectWorkItemName,
        wid.minDuration,
        wid.maxDuration,
        wid.minCost,
        wid.maxCost,
        wid.minLabors AS minLaborQty,
        wid.maxLabors AS maxLaborQty

    FROM projectDetails pd
    INNER JOIN workItemDetails wid
        ON wid.projectDetailId = pd.projectDetailId
    INNER JOIN workItems wi
        ON wi.projectWorkItemId = wid.projectWorkItemId

    WHERE pd.projectTypeId = p_projectTypeId
      AND (p_buildingId IS NULL OR pd.projectBuildingId = p_buildingId)
      AND (p_levelId IS NULL OR pd.projectLevelId = p_levelId);
END$$

DELIMITER ;

DELIMITER $$

DROP PROCEDURE IF EXISTS getAllWorkItemByAssignProjectId $$
CREATE PROCEDURE getAllWorkItemByAssignProjectId(
    IN p_assignProjectId INT
)
BEGIN
    SELECT
        awi.assignWorkItemId,
        wi.projectWorkItemName AS workItemName,
        ps.projectStatusName AS workItemStatus,

        -- planned baseline status
        ast.assignStatusName AS assignStatus,

        -- planned baseline values (latest planned)
        awid.workItemCost AS cost,
        awid.workItemLaborQty AS laborQty,
        awid.workItemDuration AS duration,
        awid.startDate AS startDate,
        awid.endDate AS endDate

    FROM assignWorkItems awi
    INNER JOIN workItems wi
        ON wi.projectWorkItemId = awi.projectWorkItemId
    INNER JOIN projectStatus ps
        ON ps.projectStatusId = awi.workItemStatus

    LEFT JOIN (
        /* latest PLANNED details per work item */
        SELECT awid1.*
        FROM assignWorkItemDetails awid1
        JOIN assignStatus s1 ON awid1.assignStatusId = s1.assignStatusId
        JOIN (
            SELECT
                d.assignWorkItemId,
                MAX(d.assignWorkItemDetailId) AS latestPlannedDetailId
            FROM assignWorkItemDetails d
            JOIN assignStatus s ON d.assignStatusId = s.assignStatusId
            WHERE s.assignStatusName IN ('autoAssign','customAssign','extraAssign')
            GROUP BY d.assignWorkItemId
        ) latest
          ON latest.assignWorkItemId = awid1.assignWorkItemId
         AND latest.latestPlannedDetailId = awid1.assignWorkItemDetailId
    ) awid
      ON awid.assignWorkItemId = awi.assignWorkItemId

    LEFT JOIN assignStatus ast
      ON ast.assignStatusId = awid.assignStatusId

    WHERE awi.assignProjectId = p_assignProjectId
    ORDER BY awi.assignWorkItemId;
END$$

DELIMITER ;


DELIMITER $$

CREATE PROCEDURE assignWorkItems(
    IN p_assignProjectId INT,
    IN p_workItemId INT,
    IN p_projectStatusName VARCHAR(255),
    IN p_assignStatusName VARCHAR(255),
    IN p_cost DOUBLE,
    IN p_laborQty DOUBLE,
    IN p_duration DOUBLE,
    IN p_startDate DATE,
    IN p_endDate DATE
)
BEGIN
    DECLARE v_assignWorkItemId INT;
    DECLARE v_projectStatusId INT;
    DECLARE v_assignStatusId INT;
    DECLARE v_assignWorkItemDetailId INT;

    -- Get projectStatusId
    SELECT projectStatusId
    INTO v_projectStatusId
    FROM projectStatus
    WHERE projectStatusName = p_projectStatusName
    LIMIT 1;

    IF v_projectStatusId IS NULL THEN
        SELECT FALSE AS success;
    ELSE

        -- Find or create assignWorkItem
        SELECT assignWorkItemId
        INTO v_assignWorkItemId
        FROM assignWorkItems
        WHERE assignProjectId = p_assignProjectId
          AND projectWorkItemId = p_workItemId
        LIMIT 1;

        IF v_assignWorkItemId IS NULL THEN
            INSERT INTO assignWorkItems(assignProjectId, projectWorkItemId,workItemStatus)
            VALUES (p_assignProjectId, p_workItemId,v_projectStatusId);
            SET v_assignWorkItemId = LAST_INSERT_ID();
        END IF;

        -- Get assignStatusId
        SELECT assignStatusId
        INTO v_assignStatusId
        FROM assignStatus
        WHERE assignStatusName = p_assignStatusName
        LIMIT 1;

        IF v_assignStatusId IS NULL THEN
            SELECT FALSE AS success;
        ELSE
            -- Check if assignWorkItemDetails already exists for this status
            SELECT assignWorkItemDetailId
            INTO v_assignWorkItemDetailId
            FROM assignWorkItemDetails
            WHERE assignWorkItemId = v_assignWorkItemId
              AND assignStatusId = v_assignStatusId
            LIMIT 1;

            IF v_assignWorkItemDetailId IS NULL THEN
                INSERT INTO assignWorkItemDetails(
                    assignWorkItemId,
                    assignStatusId,
                    workItemCost,
                    workItemLaborQty,
                    workItemDuration,
                    startDate,
                    endDate
                )
                VALUES (
                    v_assignWorkItemId,
                    v_assignStatusId,
                    p_cost,
                    p_laborQty,
                    p_duration,
                    p_startDate,
                    p_endDate
                );
            ELSE
                UPDATE assignWorkItemDetails
                SET workItemCost = p_cost,
                    workItemLaborQty = p_laborQty,
                    workItemDuration = p_duration,
                    startDate = p_startDate,
                    endDate = p_endDate
                WHERE assignWorkItemDetailId = v_assignWorkItemDetailId;
            END IF;

            SELECT TRUE AS success;
        END IF;
    END IF;
END$$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE getAllSkillDetailsByAssignWorkItem(
    IN p_assignWorkItemId INT
)
BEGIN
    SELECT
        awis.assignWorkItemSkillId,
        s.skillId,
        s.skillName,
        ast.assignStatusName AS assignStatus,
        awisd.laborQty,
        awisd.dailyWagePerLabor,
        awisd.assignWorkItemSkillDetailId,
        awis.isCancel,
        -- Calculate totals
        ROUND(awisd.laborQty * awisd.dailyWagePerLabor, 2) AS totalDailyCost,
        -- Status indicator
        CASE
            WHEN awis.isCancel = TRUE THEN 'Cancelled'
            WHEN awisd.laborQty IS NULL OR awisd.dailyWagePerLabor IS NULL THEN 'Not Assigned'
            ELSE 'Active'
        END AS statusDescription
    FROM assignWorkItemSkills awis
    INNER JOIN skills s
        ON s.skillId = awis.skillId
    LEFT JOIN assignWorkItemSkillDetails awisd
        ON awisd.assignWorkItemSkillId = awis.assignWorkItemSkillId
    LEFT JOIN assignStatus ast
        ON ast.assignStatusId = awisd.assignStatusId  -- Fixed column name: assignStatusId not assignStatus
    WHERE awis.assignWorkItemId = p_assignWorkItemId
    ORDER BY
        CASE WHEN awis.isCancel = TRUE THEN 2 ELSE 1 END,  -- Active first, then cancelled
        s.skillName;
END$$

DELIMITER ;


DELIMITER $$

CREATE PROCEDURE addSkillToWorkItem(
    IN p_assignWorkItemId INT,
    IN p_skillId INT,
    IN p_assignStatusName VARCHAR(255),
    IN p_laborQty DOUBLE,
    IN p_dailyWage DOUBLE
)
BEGIN
    DECLARE v_assignWorkItemSkillId INT;
    DECLARE v_assignStatusId INT;
    DECLARE v_existingDetailId INT;
    DECLARE v_skillExists INT;
    DECLARE v_workItemExists INT;

    -- Validate that assignWorkItemId exists
    SELECT COUNT(*) INTO v_workItemExists
    FROM assignWorkItems
    WHERE assignWorkItemId = p_assignWorkItemId;

    -- Validate that skillId exists
    SELECT COUNT(*) INTO v_skillExists
    FROM skills
    WHERE skillId = p_skillId;

    IF v_workItemExists = 0 OR v_skillExists = 0 THEN
        SELECT FALSE AS success,
               CASE
                   WHEN v_workItemExists = 0 THEN 'Work item not found'
                   WHEN v_skillExists = 0 THEN 'Skill not found'
               END AS message;
    ELSE
        -- Find or create assignWorkItemSkill
        SELECT assignWorkItemSkillId
        INTO v_assignWorkItemSkillId
        FROM assignWorkItemSkills
        WHERE assignWorkItemId = p_assignWorkItemId
          AND skillId = p_skillId
        LIMIT 1;

        IF v_assignWorkItemSkillId IS NULL THEN
            INSERT INTO assignWorkItemSkills(assignWorkItemId, skillId)
            VALUES (p_assignWorkItemId, p_skillId);
            SET v_assignWorkItemSkillId = LAST_INSERT_ID();
        END IF;

        -- Get assignStatusId
        SELECT assignStatusId
        INTO v_assignStatusId
        FROM assignStatus
        WHERE assignStatusName = p_assignStatusName
        LIMIT 1;

        IF v_assignStatusId IS NULL THEN
            SELECT FALSE AS success, 'Assign status not found' AS message;
        ELSE
            -- Check if assignWorkItemSkillDetails already exists for this status
            SELECT assignWorkItemSkillDetailId
            INTO v_existingDetailId
            FROM assignWorkItemSkillDetails
            WHERE assignWorkItemSkillId = v_assignWorkItemSkillId
              AND assignStatusId = v_assignStatusId
            LIMIT 1;

            IF v_existingDetailId IS NULL THEN
                -- Insert new detail
                INSERT INTO assignWorkItemSkillDetails(
                    assignWorkItemSkillId,
                    assignStatusId,
                    laborQty,
                    dailyWagePerLabor
                )
                VALUES (
                    v_assignWorkItemSkillId,
                    v_assignStatusId,
                    p_laborQty,
                    p_dailyWage
                );
                SET v_existingDetailId = LAST_INSERT_ID();
                SELECT TRUE AS success, 'Skill added to work item' AS message, v_existingDetailId AS newDetailId;
            ELSE
                -- Update existing detail
                UPDATE assignWorkItemSkillDetails
                SET laborQty = p_laborQty,
                    dailyWagePerLabor = p_dailyWage,
                    assignStatusId = v_assignStatusId
                WHERE assignWorkItemSkillDetailId = v_existingDetailId;

                SELECT TRUE AS success, 'Skill requirements updated' AS message, v_existingDetailId AS updatedDetailId;
            END IF;
        END IF;
    END IF;
END$$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE assignProjects(
    IN p_projectTypeId INT,
    IN p_projectInstanceName VARCHAR(255),
    IN p_projectBuildingId INT,
    IN p_projectLevelId INT,
    IN p_projectArea DOUBLE,
    IN p_projectHeight DOUBLE,
    IN p_totalStories DOUBLE,
    IN p_totalUnits DOUBLE,
    IN p_supervisorId INT,
    IN p_projectLocation VARCHAR(255),
    IN p_projectOverHeadCost DOUBLE,
    IN p_projectStatusName VARCHAR(255),
    IN p_assignStatusName VARCHAR(255),
    IN p_projectCost DOUBLE,
    IN p_projectLaborQty DOUBLE,
    IN p_projectDuration DOUBLE,
    IN p_startDate DATE,
    IN p_endDate DATE
)
BEGIN
    DECLARE v_assignProjectId INT;
    DECLARE v_projectStatusId INT;
    DECLARE v_assignStatusId INT;
    DECLARE v_assignProjectDetailId INT;

    -- Get projectStatusId
    SELECT projectStatusId
    INTO v_projectStatusId
    FROM projectStatus
    WHERE projectStatusName = p_projectStatusName
    LIMIT 1;

    IF v_projectStatusId IS NULL THEN
        SELECT FALSE AS success;
    ELSE
        -- Insert into assignProjects
        INSERT INTO assignProjects(
            projectTypeId,
            projectInstanceName,
            projectBuildingId,
            projectLevelId,
            projectArea,
            projectHeight,
            totalStories,
            totalUnits,
            supervisorId,
            projectLocation,
            projectOverHeadCost,
            projectStatus
        )
        VALUES (
            p_projectTypeId,
            p_projectInstanceName,
            p_projectBuildingId,
            p_projectLevelId,
            p_projectArea,
            p_projectHeight,
            p_totalStories,
            p_totalUnits,
            p_supervisorId,
            p_projectLocation,
            p_projectOverHeadCost,
            v_projectStatusId
        );
        SET v_assignProjectId = LAST_INSERT_ID();

        -- Get assignStatusId
        SELECT assignStatusId
        INTO v_assignStatusId
        FROM assignStatus
        WHERE assignStatusName = p_assignStatusName
        LIMIT 1;

        IF v_assignStatusId IS NULL THEN
            SELECT FALSE AS success;
        ELSE
            -- Check if assignProjectDetails already exists for this status
            SELECT assignProjectDetailId
            INTO v_assignProjectDetailId
            FROM assignProjectDetails
            WHERE assignProjectId = v_assignProjectId
              AND assignStatusId = v_assignStatusId
            LIMIT 1;

            IF v_assignProjectDetailId IS NULL THEN
                INSERT INTO assignProjectDetails(
                    assignProjectId,
                    assignStatusId,
                    projectCost,
                    projectLaborQty,
                    projectDuration,
                    startDate,
                    endDate
                )
                VALUES (
                    v_assignProjectId,
                    v_assignStatusId,
                    p_projectCost,
                    p_projectLaborQty,
                    p_projectDuration,
                    p_startDate,
                    p_endDate
                );
            ELSE
                UPDATE assignProjectDetails
                SET projectCost = p_projectCost,
                    projectLaborQty = p_projectLaborQty,
                    projectDuration = p_projectDuration,
                    startDate = p_startDate,
                    endDate = p_endDate
                WHERE assignProjectDetailId = v_assignProjectDetailId;
            END IF;

            SELECT TRUE AS success;
        END IF;
    END IF;
END$$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE updateAssignProject(
    IN p_assignProjectId INT,
    IN p_assignStatusName VARCHAR(255),
    IN p_projectCost DOUBLE,
    IN p_projectLaborQty DOUBLE,
    IN p_projectDuration DOUBLE,
    IN p_startDate DATE,
    IN p_endDate DATE
)
BEGIN
    DECLARE v_assignStatusId INT;
    DECLARE v_assignProjectDetailId INT;

    -- Get assignStatusId
    SELECT assignStatusId
    INTO v_assignStatusId
    FROM assignStatus
    WHERE assignStatusName = p_assignStatusName
    LIMIT 1;

    IF v_assignStatusId IS NULL THEN
        SELECT FALSE AS success;
    ELSE
        -- Check if assignProjectDetails already exists for this status
        SELECT assignProjectDetailId
        INTO v_assignProjectDetailId
        FROM assignProjectDetails
        WHERE assignProjectId = p_assignProjectId
          AND assignStatusId = v_assignStatusId
        LIMIT 1;

        IF v_assignProjectDetailId IS NULL THEN
            INSERT INTO assignProjectDetails(
                assignProjectId,
                assignStatusId,
                projectCost,
                projectLaborQty,
                projectDuration,
                startDate,
                endDate
            )
            VALUES (
                p_assignProjectId,
                v_assignStatusId,
                p_projectCost,
                p_projectLaborQty,
                p_projectDuration,
                p_startDate,
                p_endDate
            );
        ELSE
            UPDATE assignProjectDetails
            SET projectCost = p_projectCost,
                projectLaborQty = p_projectLaborQty,
                projectDuration = p_projectDuration,
                startDate = p_startDate,
                endDate = p_endDate
            WHERE assignProjectDetailId = v_assignProjectDetailId;
        END IF;

        SELECT TRUE AS success;
    END IF;
END$$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE getProjectDetails(
    IN p_projectTypeId INT
)
BEGIN
    SELECT
        pd.projectTypeId,
        pt.typeName AS projectTypeName,
        pd.projectLevelId,
        pl.projectLevelName,
        pd.projectBuildingId,
        pb.projectBuildingName,
        pd.minOverHeadCost,
        pd.maxOverHeadCost

    FROM projectDetails pd
    INNER JOIN projectTypes pt
        ON pt.projectTypeId = pd.projectTypeId
    LEFT JOIN projectLevels pl
        ON pl.projectLevelId = pd.projectLevelId
    LEFT JOIN buildings pb
        ON pb.projectBuildingId = pd.projectBuildingId

    WHERE pd.projectTypeId = p_projectTypeId;
END$$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE getAllProjects()
BEGIN
    SELECT
        ap.assignProjectId,
        ap.projectInstanceName,
        ap.projectTypeId,
        pt.typeName AS projectTypeName,
        ap.projectBuildingId AS buildingId,
        pb.projectBuildingName AS buildingName,
        ap.projectLevelId AS levelId,
        pl.projectLevelName AS levelName,
        ap.supervisorId AS userId,
        u.userName,
        ap.projectArea,
        ap.projectHeight,
        ap.totalStories,
        ap.totalUnits,
        apd.projectCost,
        apd.projectLaborQty,
        ap.projectOverHeadCost,
        apd.projectDuration,
        apd.startDate,
        apd.endDate,
        ap.projectLocation,
        ps.projectStatusName AS projectStatus,
        ast.assignStatusName AS assignStatus

    FROM assignProjects ap
    INNER JOIN projectTypes pt
        ON pt.projectTypeId = ap.projectTypeId
    LEFT JOIN buildings pb
        ON pb.projectBuildingId = ap.projectBuildingId
    LEFT JOIN projectLevels pl
        ON pl.projectLevelId = ap.projectLevelId
    LEFT JOIN users u
        ON u.userId = ap.supervisorId
    LEFT JOIN projectStatus ps
        ON ps.projectStatusId = ap.projectStatus
    LEFT JOIN assignProjectDetails apd
		ON apd.assignProjectId = ap.assignProjectId
	LEFT JOIN assignStatus ast
		ON ast.assignStatusId = apd.assignStatusId;
END$$

DELIMITER ;


DELIMITER $$

DROP PROCEDURE IF EXISTS calculateCpiSpi $$
CREATE PROCEDURE calculateCpiSpi (
    IN p_assignProjectId INT,
    IN p_asOfDate DATE
)
BEGIN
    DECLARE BAC DOUBLE DEFAULT 0;   -- total budget
    DECLARE PV  DOUBLE DEFAULT 0;   -- planned value to date
    DECLARE AC  DOUBLE DEFAULT 0;   -- actual cost to date
    DECLARE EV  DOUBLE DEFAULT 0;   -- earned value to date
    DECLARE CPI DOUBLE;
    DECLARE SPI DOUBLE;

    DECLARE v_start DATE;
    DECLARE v_end   DATE;
    DECLARE v_plan_ratio DOUBLE DEFAULT 0;

    IF p_asOfDate IS NULL THEN
        SET p_asOfDate = CURDATE();
    END IF;

    /* =========================
       BAC + Baseline Dates
       ========================= */
    SELECT
        IFNULL(apd.projectCost, 0),
        apd.startDate,
        apd.endDate
    INTO BAC, v_start, v_end
    FROM assignProjectDetails apd
    JOIN assignStatus s ON apd.assignStatusId = s.assignStatusId
    WHERE apd.assignProjectId = p_assignProjectId
      AND s.assignStatusName IN ('autoAssign','customAssign','extraAssign')
    ORDER BY apd.assignProjectDetailId DESC
    LIMIT 1;

    /* =========================
       PV (to date) using time ratio
       PV = BAC * (elapsed_time / total_time)
       ========================= */
    IF v_start IS NULL OR v_end IS NULL OR v_end <= v_start THEN
        SET v_plan_ratio = 0;   -- or NULL, but 0 is safer for dashboards
    ELSE
        SET v_plan_ratio =
            LEAST(1,
                GREATEST(0,
                    DATEDIFF(p_asOfDate, v_start) / NULLIF(DATEDIFF(v_end, v_start), 0)
                )
            );
    END IF;

    SET PV = BAC * v_plan_ratio;

    /* =========================
       AC (to date): task cost + labor wage
       ========================= */
    SELECT
        IFNULL(SUM(drt.dailyCost),0) + IFNULL(SUM(drl.dailyWage),0)
    INTO AC
    FROM dailyReports dr
    LEFT JOIN dailyReportTasks drt ON dr.dailyReportId = drt.dailyReportId
    LEFT JOIN dailyReportLabors drl ON dr.dailyReportId = drl.dailyReportId
    WHERE dr.assignProjectId = p_assignProjectId
      AND dr.reportDate <= p_asOfDate;

    /* =========================
       EV (to date): workItemCost * physical progress
       progress = SUM(completedQty) / SUM(plannedQty)
       ========================= */
    SELECT
        IFNULL(SUM(
            lc.workItemCost *
            LEAST(1,
                IF(wp.plannedQty = 0, 0, wp.completedQty / wp.plannedQty)
            )
        ), 0)
    INTO EV
    FROM
    (
        /* Latest planned workItemCost per assignWorkItemId */
        SELECT awid1.assignWorkItemId, awid1.workItemCost
        FROM assignWorkItemDetails awid1
        JOIN assignStatus s1 ON awid1.assignStatusId = s1.assignStatusId
        WHERE s1.assignStatusName IN ('autoAssign','customAssign','extraAssign')
          AND awid1.assignWorkItemDetailId = (
              SELECT MAX(awid2.assignWorkItemDetailId)
              FROM assignWorkItemDetails awid2
              JOIN assignStatus s2 ON awid2.assignStatusId = s2.assignStatusId
              WHERE awid2.assignWorkItemId = awid1.assignWorkItemId
                AND s2.assignStatusName IN ('autoAssign','customAssign','extraAssign')
          )
    ) lc
    JOIN
    (
        /* Progress per work item from tasks */
        SELECT
            at.assignWorkItemId,
            SUM(at.plannedQty) AS plannedQty,
            SUM(IFNULL(done.completedQty,0)) AS completedQty
        FROM assignTasks at
        LEFT JOIN
        (
            SELECT
                drt.assignTaskId,
                SUM(drt.completedQty) AS completedQty
            FROM dailyReports dr
            JOIN dailyReportTasks drt ON dr.dailyReportId = drt.dailyReportId
            WHERE dr.assignProjectId = p_assignProjectId
              AND dr.reportDate <= p_asOfDate
            GROUP BY drt.assignTaskId
        ) done ON done.assignTaskId = at.assignTaskId
        WHERE at.isCancel = 0
        GROUP BY at.assignWorkItemId
    ) wp ON wp.assignWorkItemId = lc.assignWorkItemId;

    /* =========================
       CPI / SPI
       ========================= */
    SET CPI = IF(AC = 0, NULL, EV / AC);
    SET SPI = IF(PV = 0, NULL, EV / PV);

    /* =========================
       Output
       ========================= */
    SELECT
        BAC,
        PV,
        EV,
        AC,
        CPI,
        CASE
            WHEN CPI IS NULL THEN 'No Data'
            WHEN CPI >= 1.05 THEN 'Under Budget'
            WHEN CPI >= 0.95 THEN 'On Budget'
            ELSE 'Over Budget'
        END AS CPI_STATUS,
        SPI,
        CASE
            WHEN SPI IS NULL THEN 'No Data'
            WHEN SPI >= 1.05 THEN 'Ahead of Schedule'
            WHEN SPI >= 0.95 THEN 'On Schedule'
            ELSE 'Behind Schedule'
        END AS SPI_STATUS,
        v_start AS baselineStart,
        v_end   AS baselineEnd,
        p_asOfDate AS asOfDate;

END$$

DELIMITER ;
DELIMITER $$

DROP PROCEDURE IF EXISTS getProjectDashboard $$
CREATE PROCEDURE getProjectDashboard(
    IN p_assignProjectId INT,
    IN p_asOfDate DATE
)
BEGIN
    DECLARE BAC DOUBLE DEFAULT 0;
    DECLARE PV  DOUBLE DEFAULT 0;
    DECLARE EV  DOUBLE DEFAULT 0;
    DECLARE AC  DOUBLE DEFAULT 0;
    DECLARE CPI DOUBLE;
    DECLARE SPI DOUBLE;

    DECLARE v_start DATE;
    DECLARE v_end   DATE;
    DECLARE v_totalDays INT DEFAULT 0;

    /* TIME-BASED elapsed days (calendar days passed since baseline start, capped by totalDays) */
    DECLARE v_elapsedDays INT DEFAULT 0;

    /* how many distinct report days exist up to asOfDate */
    DECLARE v_reportedDays INT DEFAULT 0;

    /* how many distinct days have completedQty > 0 up to asOfDate (production days) */
    DECLARE v_completedDays INT DEFAULT 0;

    /* Latest report date (DATE) */
    DECLARE v_latestReportDate DATE DEFAULT NULL;

    DECLARE v_totalWorkItems INT DEFAULT 0;
    DECLARE v_completedWorkItems INT DEFAULT 0;
    DECLARE v_progress DOUBLE DEFAULT 0;

    IF p_asOfDate IS NULL THEN
        SET p_asOfDate = CURDATE();
END IF;

    /* Latest project baseline (BAC + dates) */
SELECT
    IFNULL(apd.projectCost, 0),
    apd.startDate,
    apd.endDate
INTO BAC, v_start, v_end
FROM assignProjectDetails apd
         JOIN assignStatus s ON apd.assignStatusId = s.assignStatusId
WHERE apd.assignProjectId = p_assignProjectId
  AND s.assignStatusName IN ('autoAssign','customAssign','extraAssign')
ORDER BY apd.assignProjectDetailId DESC
    LIMIT 1;

/* Days */
IF v_start IS NULL OR v_end IS NULL OR v_end < v_start THEN
        SET v_totalDays = 0;
        SET v_elapsedDays = 0;
ELSE
        SET v_totalDays = DATEDIFF(v_end, v_start) + 1;

        /* elapsedDays = calendar days passed since baseline start (time-based) */
        SET v_elapsedDays = LEAST(
            v_totalDays,
            GREATEST(0, DATEDIFF(p_asOfDate, v_start) + 1)
        );
END IF;

    /* completedDays = number of distinct days with any completedQty > 0 (work-done days) */
SELECT COUNT(DISTINCT dr.reportDate)
INTO v_completedDays
FROM dailyReports dr
         JOIN dailyReportTasks drt ON dr.dailyReportId = drt.dailyReportId
WHERE dr.assignProjectId = p_assignProjectId
  AND dr.reportDate <= p_asOfDate
  AND IFNULL(drt.completedQty, 0) > 0;

SET v_completedDays = LEAST(v_totalDays, v_completedDays);

    /* Latest report date (optional) */
SELECT MAX(reportDate)
INTO v_latestReportDate
FROM dailyReports
WHERE assignProjectId = p_assignProjectId;

/* PV to date = BAC * (elapsed/total) */
SET PV = IF(v_totalDays = 0, 0, BAC * (v_elapsedDays / v_totalDays));

    /* Reported days (any report rows) */
SELECT COUNT(DISTINCT reportDate)
INTO v_reportedDays
FROM dailyReports
WHERE assignProjectId = p_assignProjectId
  AND reportDate <= p_asOfDate;

/* Work item counts */
SELECT COUNT(*)
INTO v_totalWorkItems
FROM assignWorkItems
WHERE assignProjectId = p_assignProjectId;

SELECT IFNULL(SUM(CASE WHEN ps.projectStatusName = 'finished' THEN 1 ELSE 0 END),0)
INTO v_completedWorkItems
FROM assignWorkItems awi
         LEFT JOIN projectStatus ps ON awi.workItemStatus = ps.projectStatusId
WHERE awi.assignProjectId = p_assignProjectId;

/* AC to date = tasks + labors (separate sums to avoid double count) */
SET AC =
        (SELECT IFNULL(SUM(drt.dailyCost),0)
         FROM dailyReports dr
         JOIN dailyReportTasks drt ON dr.dailyReportId = drt.dailyReportId
         WHERE dr.assignProjectId = p_assignProjectId
           AND dr.reportDate <= p_asOfDate)
      + (SELECT IFNULL(SUM(drl.dailyWage),0)
         FROM dailyReports dr
         JOIN dailyReportLabors drl ON dr.dailyReportId = drl.dailyReportId
         WHERE dr.assignProjectId = p_assignProjectId
           AND dr.reportDate <= p_asOfDate);

    /* EV = SUM(workItem BAC * workItem progress) */
SELECT
    IFNULL(SUM(wiBac.workItemCost * wiProg.progressRatio), 0)
INTO EV
FROM
    (
        /* latest BAC per work item for this project */
        SELECT awid1.assignWorkItemId, awid1.workItemCost
        FROM assignWorkItemDetails awid1
                 JOIN assignStatus s1 ON awid1.assignStatusId = s1.assignStatusId
                 JOIN assignWorkItems awi ON awid1.assignWorkItemId = awi.assignWorkItemId
        WHERE awi.assignProjectId = p_assignProjectId
          AND s1.assignStatusName IN ('autoAssign','customAssign','extraAssign')
          AND awid1.assignWorkItemDetailId = (
            SELECT MAX(awid2.assignWorkItemDetailId)
            FROM assignWorkItemDetails awid2
                     JOIN assignStatus s2 ON awid2.assignStatusId = s2.assignStatusId
            WHERE awid2.assignWorkItemId = awid1.assignWorkItemId
              AND s2.assignStatusName IN ('autoAssign','customAssign','extraAssign')
        )
    ) wiBac
        JOIN
    (
        /* progress per work item from completedQty / plannedQty */
        SELECT
            at.assignWorkItemId,
            LEAST(
                    1,
                    IF(
                            SUM(at.plannedQty) = 0,
                            0,
                            SUM(IFNULL(done.completedQty,0)) / SUM(at.plannedQty)
                    )
            ) AS progressRatio
        FROM assignTasks at
        JOIN assignWorkItems awi ON at.assignWorkItemId = awi.assignWorkItemId
            LEFT JOIN (
            SELECT
            drt.assignTaskId,
            SUM(drt.completedQty) AS completedQty
            FROM dailyReports dr
            JOIN dailyReportTasks drt ON dr.dailyReportId = drt.dailyReportId
            WHERE dr.assignProjectId = p_assignProjectId
            AND dr.reportDate <= p_asOfDate
            GROUP BY drt.assignTaskId
            ) done ON done.assignTaskId = at.assignTaskId
        WHERE awi.assignProjectId = p_assignProjectId
          AND at.isCancel = 0
        GROUP BY at.assignWorkItemId
    ) wiProg ON wiProg.assignWorkItemId = wiBac.assignWorkItemId;

/* overall progress = EV/BAC */
SET v_progress = IF(BAC = 0, 0, EV / BAC);

    /* indices */
    SET CPI = IF(AC = 0, NULL, EV / AC);
    SET SPI = IF(PV = 0, NULL, EV / PV);

SELECT
    BAC, PV, EV, AC, CPI, SPI,
    v_progress AS progressRatio,
    v_start AS baselineStart,
    v_end AS baselineEnd,
    v_elapsedDays AS elapsedDays,          -- time-based
    v_totalDays AS totalDays,
    v_reportedDays AS reportedDays,
    v_completedDays AS completedDays,      -- work-done days
    v_latestReportDate AS latestReportDate,
    v_completedWorkItems AS completedWorkItems,
    v_totalWorkItems AS totalWorkItems,
    p_asOfDate AS asOfDate;
END$$

DELIMITER ;


DELIMITER $$

DROP PROCEDURE IF EXISTS getWorkItemDashboard $$
CREATE PROCEDURE getWorkItemDashboard(
    IN p_assignWorkItemId INT,
    IN p_asOfDate DATE
)
BEGIN
    DECLARE BAC DOUBLE DEFAULT 0;
    DECLARE PV  DOUBLE DEFAULT 0;
    DECLARE EV  DOUBLE DEFAULT 0;
    DECLARE AC  DOUBLE DEFAULT 0;
    DECLARE CPI DOUBLE;
    DECLARE SPI DOUBLE;

    DECLARE v_start DATE;
    DECLARE v_end   DATE;
    DECLARE v_totalDays INT DEFAULT 0;
    DECLARE v_elapsedDays INT DEFAULT 0;
    DECLARE v_progress DOUBLE DEFAULT 0;

    IF p_asOfDate IS NULL THEN
        SET p_asOfDate = CURDATE();
    END IF;

    /* Latest baseline for this work item */
    SELECT
        IFNULL(awid.workItemCost,0),
        awid.startDate,
        awid.endDate
    INTO BAC, v_start, v_end
    FROM assignWorkItemDetails awid
    JOIN assignStatus s ON awid.assignStatusId = s.assignStatusId
    WHERE awid.assignWorkItemId = p_assignWorkItemId
      AND s.assignStatusName IN ('autoAssign','customAssign','extraAssign')
    ORDER BY awid.assignWorkItemDetailId DESC
    LIMIT 1;

    /* PV by time ratio */
    IF v_start IS NULL OR v_end IS NULL OR v_end < v_start THEN
        SET v_totalDays = 0;
        SET v_elapsedDays = 0;
    ELSE
        SET v_totalDays = DATEDIFF(v_end, v_start) + 1;
        SET v_elapsedDays = LEAST(v_totalDays, GREATEST(0, DATEDIFF(p_asOfDate, v_start) + 1));
    END IF;

    SET PV = IF(v_totalDays = 0, 0, BAC * (v_elapsedDays / v_totalDays));

    /* AC = tasks + labors for reports of this work item */
    SET AC =
        (SELECT IFNULL(SUM(drt.dailyCost),0)
         FROM dailyReports dr
         JOIN dailyReportTasks drt ON dr.dailyReportId = drt.dailyReportId
         WHERE dr.assignWorkItemId = p_assignWorkItemId
           AND dr.reportDate <= p_asOfDate)
      + (SELECT IFNULL(SUM(drl.dailyWage),0)
         FROM dailyReports dr
         JOIN dailyReportLabors drl ON dr.dailyReportId = drl.dailyReportId
         WHERE dr.assignWorkItemId = p_assignWorkItemId
           AND dr.reportDate <= p_asOfDate);

    /* Progress from completedQty / plannedQty */
    SELECT
        LEAST(1,
            IF(SUM(at.plannedQty)=0, 0, SUM(IFNULL(done.completedQty,0)) / SUM(at.plannedQty))
        )
    INTO v_progress
    FROM assignTasks at
    LEFT JOIN (
        SELECT drt.assignTaskId, SUM(drt.completedQty) AS completedQty
        FROM dailyReports dr
        JOIN dailyReportTasks drt ON dr.dailyReportId = drt.dailyReportId
        WHERE dr.reportDate <= p_asOfDate
        GROUP BY drt.assignTaskId
    ) done ON done.assignTaskId = at.assignTaskId
    WHERE at.assignWorkItemId = p_assignWorkItemId
      AND at.isCancel = 0;

    SET EV = BAC * IFNULL(v_progress,0);

    SET CPI = IF(AC = 0, NULL, EV / AC);
    SET SPI = IF(PV = 0, NULL, EV / PV);

    SELECT
        BAC, PV, EV, AC, CPI, SPI,
        IFNULL(v_progress,0) AS progressRatio,
        v_start AS baselineStart,
        v_end AS baselineEnd,
        v_elapsedDays AS elapsedDays,
        v_totalDays AS totalDays,
        p_asOfDate AS asOfDate;
END$$

DELIMITER ;
DELIMITER $$

DROP PROCEDURE IF EXISTS getProjectWorkItemsDashboard $$
CREATE PROCEDURE getProjectWorkItemsDashboard(
    IN p_assignProjectId INT,
    IN p_asOfDate DATE
)
BEGIN
    IF p_asOfDate IS NULL THEN
        SET p_asOfDate = CURDATE();
    END IF;

    SELECT
        awi.assignWorkItemId,
        wi.projectWorkItemName AS workItemName,
        ps.projectStatusName AS workItemStatus,

        base.workItemCost AS BAC,

        /* PV */
        IF(base.totalDays = 0, 0, base.workItemCost * (base.elapsedDays / base.totalDays)) AS PV,

        /* EV */
        base.workItemCost * IFNULL(prog.progressRatio,0) AS EV,

        /* AC (safe) */
        IFNULL(ac.AC,0) AS AC,

        /* CPI */
        IF(IFNULL(ac.AC,0)=0, NULL, (base.workItemCost * IFNULL(prog.progressRatio,0)) / ac.AC) AS CPI,

        /* SPI */
        IF(
            IF(base.totalDays = 0, 0, base.workItemCost * (base.elapsedDays / base.totalDays)) = 0,
            NULL,
            (base.workItemCost * IFNULL(prog.progressRatio,0)) /
            (IF(base.totalDays = 0, 0, base.workItemCost * (base.elapsedDays / base.totalDays)))
        ) AS SPI,

        IFNULL(prog.progressRatio,0) AS progressRatio

    FROM assignWorkItems awi
    LEFT JOIN workItems wi ON awi.projectWorkItemId = wi.projectWorkItemId
    LEFT JOIN projectStatus ps ON awi.workItemStatus = ps.projectStatusId

    /* latest baseline per work item + day ratio */
    JOIN (
        SELECT
            x.assignWorkItemId,
            x.workItemCost,
            x.startDate,
            x.endDate,
            IF(x.startDate IS NULL OR x.endDate IS NULL OR x.endDate < x.startDate, 0, DATEDIFF(x.endDate, x.startDate)+1) AS totalDays,
            IF(x.startDate IS NULL OR x.endDate IS NULL OR x.endDate < x.startDate, 0,
                LEAST(DATEDIFF(x.endDate, x.startDate)+1, GREATEST(0, DATEDIFF(p_asOfDate, x.startDate)+1))
            ) AS elapsedDays
        FROM assignWorkItemDetails x
        JOIN assignStatus s ON x.assignStatusId = s.assignStatusId
        WHERE s.assignStatusName IN ('autoAssign','customAssign','extraAssign')
          AND x.assignWorkItemDetailId IN (
              SELECT MAX(awid2.assignWorkItemDetailId)
              FROM assignWorkItemDetails awid2
              JOIN assignStatus s2 ON awid2.assignStatusId = s2.assignStatusId
              WHERE s2.assignStatusName IN ('autoAssign','customAssign','extraAssign')
              GROUP BY awid2.assignWorkItemId
          )
    ) base ON base.assignWorkItemId = awi.assignWorkItemId

    /* progress per work item */
    LEFT JOIN (
        SELECT
            at.assignWorkItemId,
            LEAST(1,
                IF(SUM(at.plannedQty)=0, 0, SUM(IFNULL(done.completedQty,0)) / SUM(at.plannedQty))
            ) AS progressRatio
        FROM assignTasks at
        LEFT JOIN (
            SELECT drt.assignTaskId, SUM(drt.completedQty) AS completedQty
            FROM dailyReports dr
            JOIN dailyReportTasks drt ON dr.dailyReportId = drt.dailyReportId
            WHERE dr.assignProjectId = p_assignProjectId
              AND dr.reportDate <= p_asOfDate
            GROUP BY drt.assignTaskId
        ) done ON done.assignTaskId = at.assignTaskId
        WHERE at.isCancel = 0
        GROUP BY at.assignWorkItemId
    ) prog ON prog.assignWorkItemId = awi.assignWorkItemId

    /* AC per work item (safe: sum tasks + sum labors per dailyReportId, then sum) */
    LEFT JOIN (
        SELECT
            dr.assignWorkItemId,
            SUM(IFNULL(t.sumTaskCost,0) + IFNULL(l.sumLaborCost,0)) AS AC
        FROM dailyReports dr
        LEFT JOIN (
            SELECT dailyReportId, SUM(dailyCost) AS sumTaskCost
            FROM dailyReportTasks
            GROUP BY dailyReportId
        ) t ON t.dailyReportId = dr.dailyReportId
        LEFT JOIN (
            SELECT dailyReportId, SUM(dailyWage) AS sumLaborCost
            FROM dailyReportLabors
            GROUP BY dailyReportId
        ) l ON l.dailyReportId = dr.dailyReportId
        WHERE dr.assignProjectId = p_assignProjectId
          AND dr.reportDate <= p_asOfDate
        GROUP BY dr.assignWorkItemId
    ) ac ON ac.assignWorkItemId = awi.assignWorkItemId

    WHERE awi.assignProjectId = p_assignProjectId
    ORDER BY awi.assignWorkItemId;
END$$

DELIMITER ;
DELIMITER $$

DROP PROCEDURE IF EXISTS updateProjectBaseline $$
CREATE PROCEDURE updateProjectBaseline(
    IN p_assignProjectId INT,
    IN p_projectCost DOUBLE,
    IN p_startDate DATE,
    IN p_endDate DATE,
    IN p_duration DOUBLE
)
BEGIN
    DECLARE v_assignStatusId INT;
    DECLARE v_projectStatusName VARCHAR(50);

    /* Find project status name */
    SELECT ps.projectStatusName
    INTO v_projectStatusName
    FROM assignProjects ap
    JOIN projectStatus ps ON ap.projectStatusId = ps.projectStatusId
    WHERE ap.assignProjectId = p_assignProjectId
    LIMIT 1;

    IF v_projectStatusName IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Project not found';
    END IF;

    /* Block edits if finished/cancelled */
    IF v_projectStatusName IN ('finished','cancelled') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot edit finished/cancelled project';
    END IF;

    /* planned -> customAssign, otherwise -> extraAssign */
    IF v_projectStatusName = 'planned' THEN
        SELECT assignStatusId INTO v_assignStatusId
        FROM assignStatus
        WHERE assignStatusName = 'customAssign'
        LIMIT 1;
    ELSE
        SELECT assignStatusId INTO v_assignStatusId
        FROM assignStatus
        WHERE assignStatusName = 'extraAssign'
        LIMIT 1;
    END IF;

    IF v_assignStatusId IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'assignStatus mapping missing';
    END IF;

    INSERT INTO assignProjectDetails(
        assignProjectId,
        assignStatusId,
        projectCost,
        startDate,
        endDate,
        projectDuration
    )
    VALUES (
        p_assignProjectId,
        v_assignStatusId,
        p_projectCost,
        p_startDate,
        p_endDate,
        p_duration
    );
END$$

DELIMITER ;

DELIMITER $$

DROP PROCEDURE IF EXISTS updateWorkItemBaseline $$
CREATE PROCEDURE updateWorkItemBaseline(
    IN p_assignWorkItemId INT,
    IN p_workItemCost DOUBLE,
    IN p_laborQty DOUBLE,
    IN p_duration DOUBLE,
    IN p_startDate DATE,
    IN p_endDate DATE
)
BEGIN
    DECLARE v_assignStatusId INT;
    DECLARE v_projectStatusName VARCHAR(50);

    /* Find parent project status name */
    SELECT ps.projectStatusName
    INTO v_projectStatusName
    FROM assignWorkItems awi
    JOIN assignProjects ap ON awi.assignProjectId = ap.assignProjectId
    JOIN projectStatus ps ON ap.projectStatus = ps.projectStatusId
    WHERE awi.assignWorkItemId = p_assignWorkItemId
    LIMIT 1;

    IF v_projectStatusName IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Work item or project not found';
    END IF;

    IF v_projectStatusName IN ('finished','cancelled') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot edit work item of finished/cancelled project';
    END IF;

    IF v_projectStatusName = 'planned' THEN
        SELECT assignStatusId INTO v_assignStatusId
        FROM assignStatus
        WHERE assignStatusName = 'customAssign'
        LIMIT 1;
    ELSE
        SELECT assignStatusId INTO v_assignStatusId
        FROM assignStatus
        WHERE assignStatusName = 'extraAssign'
        LIMIT 1;
    END IF;

    IF v_assignStatusId IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'assignStatus mapping missing';
    END IF;

    INSERT INTO assignWorkItemDetails(
        assignWorkItemId,
        assignStatusId,
        workItemCost,
        workItemLaborQty,
        workItemDuration,
        startDate,
        endDate
    )
    VALUES (
        p_assignWorkItemId,
        v_assignStatusId,
        p_workItemCost,
        p_laborQty,
        p_duration,
        p_startDate,
        p_endDate
    );
END$$

DELIMITER ;
