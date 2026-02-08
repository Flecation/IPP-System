
-- ============================================================
-- Demo seed: dailyReportLabors (adds labor entries to reports)
-- Requires:
--   1) Your base DB schema (tables.sql)
--   2) Labors exist (run Database/laborData.sql or your own labor inserts)
--   3) dailyReports exist (run your demo report seed first)
-- ============================================================

DELIMITER $$

DROP PROCEDURE IF EXISTS seedDailyReportLabors$$
CREATE PROCEDURE seedDailyReportLabors(
    IN p_minLaborsPerReport INT,
    IN p_maxLaborsPerReport INT
)
BEGIN
    DECLARE v_done INT DEFAULT 0;
    DECLARE v_dailyReportId INT;
    DECLARE v_n INT;

    DECLARE cur CURSOR FOR
        SELECT dailyReportId FROM dailyReports ORDER BY dailyReportId;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_done = 1;

    OPEN cur;

    read_loop: LOOP
        FETCH cur INTO v_dailyReportId;
        IF v_done = 1 THEN
            LEAVE read_loop;
        END IF;

        SET v_n = FLOOR(RAND() * (p_maxLaborsPerReport - p_minLaborsPerReport + 1)) + p_minLaborsPerReport;

        -- Insert v_n random labor rows for this daily report.
        -- We avoid duplicates per report by selecting distinct laborId.
        INSERT INTO dailyReportLabors (dailyReportId, laborId, workHours, dailyWage, remark)
        SELECT
            v_dailyReportId,
            l.laborId,
            ROUND(6 + (RAND() * 4), 1) AS workHours,                -- 6.0 to 10.0 hours
            ROUND(35000 + (RAND() * 45000), 0) AS dailyWage,        -- 35,000 to 80,000 (adjust as you like)
            CASE
                WHEN RAND() < 0.10 THEN 'Overtime'
                WHEN RAND() < 0.20 THEN 'Half day'
                WHEN RAND() < 0.30 THEN 'On-site issue'
                ELSE 'OK'
            END AS remark
        FROM (
            SELECT DISTINCT laborId
            FROM labors
            WHERE isActive = TRUE
            ORDER BY RAND()
            LIMIT 1000000
        ) x
        JOIN labors l ON l.laborId = x.laborId
        ORDER BY RAND()
        LIMIT v_n;

    END LOOP;

    CLOSE cur;
END$$

DELIMITER ;

-- Run once:
   CALL seedDailyReportLabors(2, 6);
