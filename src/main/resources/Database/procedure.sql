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

CREATE PROCEDURE getSkillByWorkItemId(IN p_workItemId INT)
BEGIN
    SELECT DISTINCT
        s.skillId,
        s.skillName
    FROM workItemDetails wid
    INNER JOIN workItemRequireSkills wirs
        ON wirs.workItemDetailId = wid.workItemDetailId
    INNER JOIN skills s
        ON s.skillId = wirs.skillId
    WHERE wid.projectWorkItemId = p_workItemId;
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
      AND (at.isCancel IS NULL OR at.isCancel = FALSE)
      AND atd.assignTaskDetailId IS NOT NULL;
END$$

DELIMITER ;



DELIMITER $$

CREATE PROCEDURE getAllTasksDetailsByWorkItem(
    IN p_projectTypeId INT,
    IN p_workItemId INT
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
      AND wid.projectWorkItemId = p_workItemId;
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

        SELECT assignStatusId
        INTO v_statusId
        FROM assignStatus
        WHERE assignStatusName = p_statusName
        LIMIT 1;

        IF v_statusId IS NULL THEN
            SELECT FALSE AS success;
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
    IN p_statusName VARCHAR(255)
)
BEGIN
    DECLARE v_assignWorkItemId INT;
    DECLARE v_assignTaskId INT;
    DECLARE v_assignStatusId INT;
    DECLARE v_projectStatusId INT;

    SELECT assignWorkItemId
    INTO v_assignWorkItemId
    FROM assignWorkItems
    WHERE assignProjectId = p_assignProjectId
      AND projectWorkItemId = p_projectWorkItemId
    LIMIT 1;

    SELECT projectStatus
    INTO v_projectStatusId
    FROM projectStatus
    WHERE projectStatusName = "inPlanning";

    IF v_assignWorkItemId IS NULL THEN
        INSERT INTO assignWorkItems(assignProjectId, projectWorkItemId,projectStatus)
        VALUES (p_assignProjectId, p_projectWorkItemId,v_projectStatusId);
        SET v_assignWorkItemId = LAST_INSERT_ID();
    END IF;

    INSERT INTO assignTasks(assignWorkItemId, projectTaskId, isCancel)
    VALUES (v_assignWorkItemId, p_projectTaskId, FALSE);
    SET v_assignTaskId = LAST_INSERT_ID();

    SELECT assignStatusId
    INTO v_projectStatusId
    FROM assignStatus
    WHERE assignStatusName = p_statusName
    LIMIT 1;

    IF v_projectStatusId IS NULL THEN
        SELECT assignStatusId
        INTO v_projectStatusId
        FROM assignStatus
        WHERE assignStatusName = 'autoAssign'
        LIMIT 1;
    END IF;

    INSERT INTO assignTaskDetails(assignTaskId, assignStatusId, taskDuration, startDate, endDate)
    VALUES (v_assignTaskId, v_projectStatusId, p_duration, p_startDate, p_endDate);

    SELECT TRUE AS success;
END$$

DELIMITER ;