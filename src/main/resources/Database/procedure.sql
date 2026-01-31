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


CREATE PROCEDURE getAllTasksByAssignWorkItem(
    IN p_assignWorkItemId INT
)
BEGIN
    SELECT
        at.assignTaskId,
        t.projectTaskName AS taskName,
        at.plannedQty AS plannedQty,
        at.unitOfMeasure AS unitOfMeasure,
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

CREATE PROCEDURE calculateCpiSpi (
    IN p_assignProjectId INT
)
BEGIN
    DECLARE PV DOUBLE DEFAULT 0;
    DECLARE EV DOUBLE DEFAULT 0;
    DECLARE AC DOUBLE DEFAULT 0;
    DECLARE CPI DOUBLE;
    DECLARE SPI DOUBLE;

    -- Planned Value (PV): latest plan cost (autoAssign/customAssign/extraAssign)
    SELECT IFNULL(
        (
            SELECT apd.projectCost
            FROM assignProjectDetails apd
            JOIN assignStatus s ON apd.assignStatusId = s.assignStatusId
            WHERE apd.assignProjectId = p_assignProjectId
              AND s.assignStatusName IN ('autoAssign', 'customAssign', 'extraAssign')
            ORDER BY apd.assignProjectDetailId DESC
            LIMIT 1
        ),
        0
    )
    INTO PV;

    -- Earned Value (EV):
    -- - If an 'actualResult' record exists, use that
    -- - Otherwise (project still running), use actual cost from dailyReportTasks.dailyCost
    SELECT IFNULL(
        (
            SELECT SUM(apd.projectCost)
            FROM assignProjectDetails apd
            JOIN assignStatus s ON apd.assignStatusId = s.assignStatusId
            WHERE apd.assignProjectId = p_assignProjectId
              AND s.assignStatusName = 'actualResult'
        ),
        (
            SELECT IFNULL(SUM(drt.dailyCost), 0)
            FROM dailyReports dr
            LEFT JOIN dailyReportTasks drt ON dr.dailyReportId = drt.dailyReportId
            WHERE dr.assignProjectId = p_assignProjectId
        )
    )
    INTO EV;

    -- Actual Cost (AC): from daily reports
    SELECT
        IFNULL(SUM(drt.dailyCost), 0)
    INTO AC
    FROM dailyReports dr
    LEFT JOIN dailyReportTasks drt ON dr.dailyReportId = drt.dailyReportId

    WHERE dr.assignProjectId = p_assignProjectId;

    SET CPI = IF(AC = 0, NULL, EV / AC);
    SET SPI = IF(PV = 0, NULL, EV / PV);

    SELECT
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
        END AS SPI_STATUS;
END$$

DELIMITER ;

 DELIMITER $$

 CREATE PROCEDURE assignFullProject(
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
     IN p_startDate DATE,
     IN p_endDate DATE
 )
 BEGIN
     DECLARE v_assignProjectId INT;
     DECLARE v_projectStatusId INT;
     DECLARE v_assignStatusId INT;
     DECLARE v_assignProjectDetailId INT;
     DECLARE v_workItemStatusId INT;
     DECLARE v_taskStatusId INT;

     -- 1. Get projectStatusId
     SELECT projectStatusId INTO v_projectStatusId
     FROM projectStatus
     WHERE projectStatusName = p_projectStatusName
     LIMIT 1;

     IF v_projectStatusId IS NULL THEN
         SELECT FALSE AS success;
         LEAVE BEGIN;
     END IF;

     -- 2. Insert assignProjects
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
     ) VALUES (
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

     -- 3. Get assignStatusId
     SELECT assignStatusId INTO v_assignStatusId
     FROM assignStatus
     WHERE assignStatusName = p_assignStatusName
     LIMIT 1;

     IF v_assignStatusId IS NULL THEN
         SELECT FALSE AS success;
         LEAVE BEGIN;
     END IF;

     -- 4. Insert assignProjectDetails from projectDetail
     INSERT INTO assignProjectDetails(
         assignProjectId,
         assignStatusId,
         projectCost,
         projectLaborQty,
         projectDuration,
         startDate,
         endDate
     )
     SELECT
         v_assignProjectId,
         v_assignStatusId,
         projectCost,
         projectLaborQty,
         projectDuration,
         p_startDate,
         p_endDate
     FROM projectDetail
     WHERE projectTypeId = p_projectTypeId;

     SET v_assignProjectDetailId = LAST_INSERT_ID();

     -- 5. Get default workItemStatus (autoAssign)
     SELECT assignStatusId INTO v_workItemStatusId
     FROM assignStatus
     WHERE assignStatusName = 'autoAssign'
     LIMIT 1;
     IF v_workItemStatusId IS NULL THEN SET v_workItemStatusId = 1; END IF;

     -- 6. Insert assignWorkItems based on projectDetail
     INSERT INTO assignWorkItems(assignProjectId, projectWorkItemId, workItemStatus)
     SELECT
         v_assignProjectId,
         pd.projectWorkItemId,
         v_workItemStatusId
     FROM projectDetail pd
     WHERE pd.projectTypeId = p_projectTypeId;

     -- 7. Get default taskStatus (autoAssign)
     SELECT assignStatusId INTO v_taskStatusId
     FROM assignStatus
     WHERE assignStatusName = 'autoAssign'
     LIMIT 1;
     IF v_taskStatusId IS NULL THEN SET v_taskStatusId = 1; END IF;

     -- 8. Insert assignTasks based on projectDetail
     INSERT INTO assignTasks(assignWorkItemId, projectTaskId, taskStatus, plannedQty, unitOfMeasure)
     SELECT
         aw.assignWorkItemId,
         pd.projectTaskId,
         v_taskStatusId,
         IFNULL(pd.plannedQty, 1),
         IFNULL(pd.unitOfMeasure, 'unit')
     FROM assignWorkItems aw
     JOIN projectDetail pd ON pd.projectWorkItemId = aw.projectWorkItemId
     WHERE aw.assignProjectId = v_assignProjectId;

     SELECT TRUE AS success;
 END$$

 DELIMITER ;
