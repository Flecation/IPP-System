-- for the labor procedure
DELIMITER $$

CREATE PROCEDURE getAllLabors ()
BEGIN
    SELECT
        l.laborId,
        l.laborName,
        l.laborNRC,
        l.laborPhone,
        l.laborStartDate,
        l.laborEndDate,
        s.skillName
    FROM labors l
    LEFT JOIN assignWorkers aw ON l.laborId = aw.laborId
    LEFT JOIN skills s ON aw.skillId = s.skillId;
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

CREATE PROCEDURE getBuildingNameByProjectId(IN p_assignProjectId INT)
BEGIN
    SELECT
        b.projectBuildingId,
        b.projectBuildingName
    FROM assignProjects ap
    INNER JOIN buildings b
        ON b.projectBuildingId = ap.projectBuildingId
    WHERE ap.assignProjectId = p_assignProjectId;
END$$

DELIMITER ;

-- for the level procedures

DELIMITER $$

CREATE PROCEDURE getLevelByProjectId(IN p_assignProjectId INT)
BEGIN
    SELECT
        pl.projectLevelId,
        pl.projectLevelName
    FROM assignProjects ap
    INNER JOIN projectLevels pl
        ON pl.projectLevelId = ap.projectLevelId
    WHERE ap.assignProjectId = p_assignProjectId;
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


CREATE PROCEDURE getAllTasksByAssignWorkItem(
    IN p_assignWorkItemId INT
)
BEGIN
    SELECT
        at.assignTaskId,
        t.projectTaskName AS taskName,
        ps.projectStatusName AS taskStatus,
        ast.assignStatusName AS assignStatus,
        atd.taskDuration AS duration,
        atd.startDate AS startDate,
        atd.endDate AS endDate

    FROM assignTasks at
    INNER JOIN tasks t
        ON t.projectTaskId = at.projectTaskId
    LEFT JOIN projectStatus ps
        ON ps.projectStatusId = at.taskStatus
    LEFT JOIN assignTaskDetails atd
        ON atd.assignTaskId = at.assignTaskId
    LEFT JOIN assignStatus ast
        ON ast.assignStatusId = atd.assignStatusId
    WHERE at.assignWorkItemId = p_assignWorkItemId
      AND atd.assignTaskDetailId IS NOT NULL;
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
    INSERT INTO assignTasks(assignWorkItemId, projectTaskId, taskStatus)
    VALUES (v_assignWorkItemId, p_projectTaskId, v_taskStatusId);
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

CREATE PROCEDURE getAllWorkItemByAssignProjectId(
    IN p_assignProjectId INT
)
BEGIN
    SELECT
        awi.assignWorkItemId,
        wi.projectWorkItemName AS workItemName,
        ps.projectStatusName AS workItemStatus,
        ast.assignStatusName AS assignStatus,
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
        -- Get the latest details for each work item
        SELECT
            awid1.*
        FROM assignWorkItemDetails awid1
        INNER JOIN (
            SELECT
                assignWorkItemId,
                MAX(assignWorkItemDetailId) as latestDetailId
            FROM assignWorkItemDetails
            GROUP BY assignWorkItemId
        ) latest
        ON awid1.assignWorkItemId = latest.assignWorkItemId
        AND awid1.assignWorkItemDetailId = latest.latestDetailId
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
        s.skillName,
        ast.assignStatusName AS assignStatus,
        awisd.laborQty,
        awisd.dailyWagePerLabor,
        awis.isCancel

    FROM assignWorkItemSkills awis
    INNER JOIN skills s
        ON s.skillId = awis.skillId
    LEFT JOIN assignWorkItemSkillDetails awisd
        ON awisd.assignWorkItemSkillId = awis.assignWorkItemSkillId
    LEFT JOIN assignStatus ast
        ON ast.assignStatusId = awisd.assignStatus

    WHERE awis.assignWorkItemId = p_assignWorkItemId;
END$$

DELIMITER ;

DELIMITER $$

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