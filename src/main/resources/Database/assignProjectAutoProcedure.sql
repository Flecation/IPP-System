DELIMITER $$

DROP PROCEDURE IF EXISTS assignFullProjectAuto $$
CREATE PROCEDURE assignFullProjectAuto(
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
    IN p_constructorCost DOUBLE,
    IN p_projectDurationDays DOUBLE,
    IN p_projectStatusName VARCHAR(50),
    IN p_projectStartDate DATE,
    IN p_projectEndDate DATE
)
proc: BEGIN

    DECLARE v_assignProjectId INT;
    DECLARE v_projectStatusId INT;
    DECLARE v_assignStatusId INT;
    DECLARE v_assignWorkItemId INT;

    DECLARE v_workItemCost DOUBLE;
    DECLARE v_workItemDuration DOUBLE;
    DECLARE v_workItemLabors DOUBLE;
    DECLARE v_taskDuration DOUBLE;

    DECLARE v_totalLaborQty DOUBLE DEFAULT 0;
    DECLARE v_totalWorkItemCost DOUBLE DEFAULT 0;

    DECLARE c_workItemDetailId INT;
    DECLARE c_projectWorkItemId INT;
    DECLARE c_minCostPercent DOUBLE;
    DECLARE c_maxCostPercent DOUBLE;
    DECLARE c_minDurationPercent DOUBLE;
    DECLARE c_maxDurationPercent DOUBLE;
    DECLARE c_minLabors DOUBLE;
    DECLARE c_maxLabors DOUBLE;

    DECLARE v_quantityFormula VARCHAR(255);
    DECLARE v_unitOfMeasure VARCHAR(50);
    DECLARE v_quantity DOUBLE;
    DECLARE v_dyn_sql TEXT;

    DECLARE v_config_exists INT DEFAULT 0;

    DECLARE done_workitems INT DEFAULT 0;

    DECLARE cur_workitems CURSOR FOR
        SELECT
            wid.workItemDetailId,
            wid.projectWorkItemId,
            wid.minCost,
            wid.maxCost,
            wid.minDuration,
            wid.maxDuration,
            wid.minLabors,
            wid.maxLabors
        FROM workItemDetails wid
        JOIN projectDetails pd ON wid.projectDetailId = pd.projectDetailId
        WHERE pd.projectTypeId = p_projectTypeId
          AND pd.projectLevelId = p_projectLevelId
          AND pd.projectBuildingId = p_projectBuildingId;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done_workitems = 1;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    -- ---------- VALIDATION / DEFAULTS ----------
    IF p_projectTypeId IS NULL OR p_projectBuildingId IS NULL OR p_projectLevelId IS NULL THEN
        SELECT FALSE AS success, 'Missing required keys: projectTypeId/buildingId/levelId' AS message;
        LEAVE proc;
    END IF;

    IF p_constructorCost IS NULL OR p_constructorCost <= 0 THEN
        SELECT FALSE AS success, 'constructorCost must be > 0' AS message;
        LEAVE proc;
    END IF;

    IF p_projectStartDate IS NULL THEN
        SET p_projectStartDate = CURDATE();
    END IF;

    IF p_projectDurationDays IS NULL OR p_projectDurationDays <= 0 THEN
        IF p_projectEndDate IS NOT NULL AND p_projectEndDate > p_projectStartDate THEN
            SET p_projectDurationDays = DATEDIFF(p_projectEndDate, p_projectStartDate);
        ELSE
            SELECT FALSE AS success, 'projectDurationDays must be > 0 (or provide valid endDate)' AS message;
            LEAVE proc;
        END IF;
    END IF;

    IF p_projectEndDate IS NULL THEN
        SET p_projectEndDate = DATE_ADD(p_projectStartDate, INTERVAL CEIL(p_projectDurationDays) DAY);
    END IF;

    SELECT COUNT(*) INTO v_config_exists
    FROM projectDetails pd
    WHERE pd.projectTypeId = p_projectTypeId
      AND pd.projectLevelId = p_projectLevelId
      AND pd.projectBuildingId = p_projectBuildingId;

    IF v_config_exists = 0 THEN
        SELECT FALSE AS success, 'No projectDetails config for given type/building/level' AS message;
        LEAVE proc;
    END IF;

    SELECT projectStatusId INTO v_projectStatusId
    FROM projectStatus
    WHERE projectStatusName = p_projectStatusName
    LIMIT 1;

    IF v_projectStatusId IS NULL THEN
        SELECT FALSE AS success, CONCAT('Invalid projectStatusName: ', p_projectStatusName) AS message;
        LEAVE proc;
    END IF;

    SELECT assignStatusId INTO v_assignStatusId
    FROM assignStatus
    WHERE assignStatusName = 'autoAssign'
    LIMIT 1;

    IF v_assignStatusId IS NULL THEN
        SELECT FALSE AS success, 'assignStatusName "autoAssign" not found' AS message;
        LEAVE proc;
    END IF;

    START TRANSACTION;

    -- ---------- 1) INSERT PROJECT ----------
    INSERT INTO assignProjects (
        projectTypeId,
        projectInstanceName,
        projectLevelId,
        projectBuildingId,
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
        p_projectLevelId,
        p_projectBuildingId,
        p_projectArea,
        p_projectHeight,
        p_totalStories,
        p_totalUnits,
        p_supervisorId,
        p_projectLocation,
        0,
        v_projectStatusId
    );

    SET v_assignProjectId = LAST_INSERT_ID();

    -- ---------- 2) WORK ITEMS ----------
    SET done_workitems = 0;
    OPEN cur_workitems;

    workitem_loop: LOOP
        FETCH cur_workitems INTO
            c_workItemDetailId,
            c_projectWorkItemId,
            c_minCostPercent,
            c_maxCostPercent,
            c_minDurationPercent,
            c_maxDurationPercent,
            c_minLabors,
            c_maxLabors;

        IF done_workitems = 1 THEN
            LEAVE workitem_loop;
        END IF;

        SET v_workItemCost     = p_constructorCost     * (((c_minCostPercent + c_maxCostPercent) / 2) / 100);
        SET v_workItemDuration = p_projectDurationDays * (((c_minDurationPercent + c_maxDurationPercent) / 2) / 100);
        SET v_workItemLabors   = (c_minLabors + c_maxLabors) / 2;

        SET v_totalWorkItemCost = v_totalWorkItemCost + v_workItemCost;
        SET v_totalLaborQty     = v_totalLaborQty + v_workItemLabors;

        INSERT INTO assignWorkItems (assignProjectId, projectWorkItemId, workItemStatus)
        VALUES (v_assignProjectId, c_projectWorkItemId, v_projectStatusId);

        SET v_assignWorkItemId = LAST_INSERT_ID();

        INSERT INTO assignWorkItemDetails (
            assignWorkItemId,
            assignStatusId,
            workItemCost,
            workItemLaborQty,
            workItemDuration,
            startDate,
            endDate
        ) VALUES (
            v_assignWorkItemId,
            v_assignStatusId,
            v_workItemCost,
            v_workItemLabors,
            v_workItemDuration,
            p_projectStartDate,
            DATE_ADD(p_projectStartDate, INTERVAL CEIL(v_workItemDuration) DAY)
        );

        -- ---------- 2A) TASKS ----------
        BEGIN
            DECLARE done_tasks INT DEFAULT 0;
            DECLARE c_taskDetailId INT;
            DECLARE c_projectTaskId INT;
            DECLARE c_minTaskDurationPercent DOUBLE;
            DECLARE c_maxTaskDurationPercent DOUBLE;

            DECLARE cur_tasks CURSOR FOR
                SELECT taskDetailId, projectTaskId, minDuration, maxDuration
                FROM taskDetails
                WHERE workItemDetailId = c_workItemDetailId;

            DECLARE CONTINUE HANDLER FOR NOT FOUND SET done_tasks = 1;

            SET done_tasks = 0;
            OPEN cur_tasks;

            task_loop: LOOP
                FETCH cur_tasks INTO c_taskDetailId, c_projectTaskId, c_minTaskDurationPercent, c_maxTaskDurationPercent;

                IF done_tasks = 1 THEN
                    LEAVE task_loop;
                END IF;

                SET v_taskDuration = v_workItemDuration * (((c_minTaskDurationPercent + c_maxTaskDurationPercent) / 2) / 100);

                SELECT quantityFormula, unitOfMeasure
                INTO v_quantityFormula, v_unitOfMeasure
                FROM taskDetails
                WHERE taskDetailId = c_taskDetailId;

                IF v_quantityFormula IS NULL OR TRIM(v_quantityFormula) = '' THEN
                    SET v_quantity = 0;
                ELSE
                    SET v_quantityFormula = REPLACE(v_quantityFormula, 'area', IFNULL(p_projectArea, 0));
                    SET v_quantityFormula = REPLACE(v_quantityFormula, 'totalStories', IFNULL(p_totalStories, 0));
                    SET v_quantityFormula = REPLACE(v_quantityFormula, 'totalUnits', IFNULL(p_totalUnits, 0));

                   BEGIN
					  DECLARE CONTINUE HANDLER FOR SQLEXCEPTION SET @quantity := 0;

					  SET @quantity := 0;
					  SET v_dyn_sql = CONCAT('SELECT (', v_quantityFormula, ') INTO @quantity');
					  SET @dyn_sql := v_dyn_sql;

					  PREPARE stmt FROM @dyn_sql;
					  EXECUTE stmt;
					  DEALLOCATE PREPARE stmt;

					  SET v_quantity = IFNULL(@quantity, 0);
					END;

                END IF;

                SET v_unitOfMeasure = IFNULL(NULLIF(TRIM(v_unitOfMeasure), ''), 'unit');

                INSERT INTO assignTasks (assignWorkItemId, projectTaskId, taskStatus, plannedQty, unitOfMeasure)
                VALUES (v_assignWorkItemId, c_projectTaskId, v_projectStatusId, v_quantity, v_unitOfMeasure);

                INSERT INTO assignTaskDetails (assignTaskId, assignStatusId, taskDuration, startDate, endDate)
                VALUES (
                    LAST_INSERT_ID(),
                    v_assignStatusId,
                    v_taskDuration,
                    p_projectStartDate,
                    DATE_ADD(p_projectStartDate, INTERVAL CEIL(v_taskDuration) DAY)
                );
            END LOOP;

            CLOSE cur_tasks;
        END;

        -- ---------- 2B) REQUIRED SKILLS ----------
        BEGIN
            DECLARE done_skills INT DEFAULT 0;
            DECLARE v_skillId INT;
            DECLARE v_assignWorkItemSkillId INT;
            DECLARE v_minRequireLabors DOUBLE;
            DECLARE v_maxRequireLabors DOUBLE;
            DECLARE v_minDailyWage DOUBLE;
            DECLARE v_maxDailyWage DOUBLE;

            DECLARE cur_skills CURSOR FOR
                SELECT skillId, minRequireLabors, maxRequireLabors, minDailyWage, maxDailyWage
                FROM workItemRequireSkills
                WHERE workItemDetailId = c_workItemDetailId;

            DECLARE CONTINUE HANDLER FOR NOT FOUND SET done_skills = 1;

            SET done_skills = 0;
            OPEN cur_skills;

            skill_loop: LOOP
                FETCH cur_skills INTO v_skillId, v_minRequireLabors, v_maxRequireLabors, v_minDailyWage, v_maxDailyWage;

                IF done_skills = 1 THEN
                    LEAVE skill_loop;
                END IF;

                INSERT INTO assignWorkItemSkills (assignWorkItemId, skillId)
                VALUES (v_assignWorkItemId, v_skillId);

                SET v_assignWorkItemSkillId = LAST_INSERT_ID();

                INSERT INTO assignWorkItemSkillDetails (
                    assignWorkItemSkillId,
                    assignStatusId,
                    laborQty,
                    dailyWagePerLabor
                ) VALUES (
                    v_assignWorkItemSkillId,
                    v_assignStatusId,
                    (v_minRequireLabors + v_maxRequireLabors) / 2,
                    (v_minDailyWage + v_maxDailyWage) / 2
                );
            END LOOP;

            CLOSE cur_skills;
        END;

    END LOOP;

    CLOSE cur_workitems;

    -- ---------- 3) PROJECT DETAILS ----------
    INSERT INTO assignProjectDetails (
        assignProjectId,
        assignStatusId,
        projectCost,
        projectLaborQty,
        projectDuration,
        startDate,
        endDate
    ) VALUES (
        v_assignProjectId,
        v_assignStatusId,
        p_constructorCost,
        v_totalLaborQty,
        p_projectDurationDays,
        p_projectStartDate,
        p_projectEndDate
    );

    -- overhead example
    UPDATE assignProjects
    SET projectOverHeadCost = p_constructorCost * 0.10
    WHERE assignProjectId = v_assignProjectId;

    COMMIT;

    SELECT TRUE AS success,
           v_assignProjectId AS assignProjectId,
           'Project successfully auto-assigned' AS message,
           v_totalLaborQty AS totalLaborQuantity,
           v_totalWorkItemCost AS totalWorkItemCost;

END $$

DELIMITER ;
