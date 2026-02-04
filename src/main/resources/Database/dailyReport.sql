DELIMITER $$

DROP PROCEDURE IF EXISTS createDailyReport $$
CREATE PROCEDURE createDailyReport(
    IN  p_assignProjectId INT,
    IN  p_assignWorkItemId INT,
    IN  p_reportDate DATE,
    IN  p_supervisorId INT,
    IN  p_weather VARCHAR(100),
    IN  p_generalRemark TEXT,
    IN  p_issue LONGTEXT,
    OUT o_dailyReportId INT
)
BEGIN
    INSERT INTO dailyReports (
        assignProjectId, assignWorkItemId, reportDate,
        supervisorId, weather, generalRemark, issue
    ) VALUES (
        p_assignProjectId, p_assignWorkItemId, p_reportDate,
        p_supervisorId, p_weather, p_generalRemark, p_issue
    );

    SET o_dailyReportId = LAST_INSERT_ID();
END $$

DELIMITER ;

DELIMITER $$

DROP PROCEDURE IF EXISTS addDailyReportTask $$
CREATE PROCEDURE addDailyReportTask(
    IN p_dailyReportId INT,
    IN p_assignTaskId INT,
    IN p_progressDescription TEXT,
    IN p_workHours DOUBLE,
    IN p_completedQty DOUBLE,
    IN p_dailyCost DOUBLE,
    IN p_isCompleted BOOLEAN
)
BEGIN
    INSERT INTO dailyReportTasks (
        dailyReportId, assignTaskId, progressDescription,
        workHours, completedQty, dailyCost, isCompleted
    ) VALUES (
        p_dailyReportId, p_assignTaskId, p_progressDescription,
        p_workHours, p_completedQty, p_dailyCost, p_isCompleted
    );
END $$

DELIMITER ;

DELIMITER $$

DROP PROCEDURE IF EXISTS addDailyReportLabor $$
CREATE PROCEDURE addDailyReportLabor(
    IN p_dailyReportId INT,
    IN p_laborId INT,
    IN p_workHours DOUBLE,
    IN p_dailyWage DOUBLE,     -- MMK per day (you decide the unit)
    IN p_remark TEXT
)
BEGIN
    INSERT INTO dailyReportLabors (
        dailyReportId, laborId, workHours, dailyWage, remark
    ) VALUES (
        p_dailyReportId, p_laborId, p_workHours, p_dailyWage, p_remark
    );
END $$

DELIMITER ;

DELIMITER $$

DROP PROCEDURE IF EXISTS getDailyReportFull $$
CREATE PROCEDURE getDailyReportFull(IN p_dailyReportId INT)
BEGIN
    -- Header
    SELECT
        dr.dailyReportId,
        dr.assignProjectId,
        ap.projectInstanceName,
        dr.assignWorkItemId,
        wi.projectWorkItemName,
        dr.reportDate,
        dr.supervisorId,
        u.userName AS supervisorName,
        dr.weather,
        dr.generalRemark,
        dr.issue
    FROM dailyReports dr
    JOIN assignProjects ap ON ap.assignProjectId = dr.assignProjectId
    JOIN assignWorkItems awi ON awi.assignWorkItemId = dr.assignWorkItemId
    JOIN workItems wi ON wi.projectWorkItemId = awi.projectWorkItemId
    LEFT JOIN users u ON u.userId = dr.supervisorId
    WHERE dr.dailyReportId = p_dailyReportId;

    -- Tasks
    SELECT
        drt.dailyReportTaskId,
        drt.assignTaskId,
        t.projectTaskName,
        drt.progressDescription,
        drt.workHours,
        drt.completedQty,
        drt.dailyCost,
        drt.isCompleted
    FROM dailyReportTasks drt
    LEFT JOIN assignTasks at ON at.assignTaskId = drt.assignTaskId
    LEFT JOIN tasks t ON t.projectTaskId = at.projectTaskId
    WHERE drt.dailyReportId = p_dailyReportId;

    -- Labors
    SELECT
        drl.dailyReportLaborId,
        drl.laborId,
        l.laborName,
        s.skillName,
        drl.workHours,
        drl.dailyWage,
        drl.remark
    FROM dailyReportLabors drl
    JOIN labors l ON l.laborId = drl.laborId
    LEFT JOIN skills s ON s.skillId = l.skillId
    WHERE drl.dailyReportId = p_dailyReportId;
END $$

DELIMITER ;
