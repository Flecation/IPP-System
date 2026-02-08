-- ==========================================================
-- Demo_30_projects_PATCH_v3.sql
-- (More compatible version: NO CTE, NO window functions)
-- Fills missing:
--   1) assignWorkItemSkills
--   2) assignWorkItemSkillDetails (autoAssign + actualResult)
--   3) dailyReportLabors
--
-- Run AFTER:
--   tables.sql
--   insertDataProjectDetails_FULL_no_delete.sql
--   Demo_30_projects_FIXED.sql
-- ==========================================================

/* ------------------------------
   1) assignWorkItemSkills
---------------------------------*/
INSERT INTO assignWorkItemSkills (assignWorkItemId, skillId, isCancel)
SELECT
    awi.assignWorkItemId,
    wrs.skillId,
    FALSE
FROM assignWorkItems awi
JOIN assignProjects ap
    ON ap.assignProjectId = awi.assignProjectId
JOIN projectDetails pd
    ON pd.projectTypeId     = ap.projectTypeId
   AND pd.projectLevelId    = ap.projectLevelId
   AND pd.projectBuildingId = ap.projectBuildingId
JOIN workItemDetails wid
    ON wid.projectDetailId   = pd.projectDetailId
   AND wid.projectWorkItemId = awi.projectWorkItemId
JOIN workItemRequireSkills wrs
    ON wrs.workItemDetailId = wid.workItemDetailId
WHERE NOT EXISTS (
    SELECT 1
    FROM assignWorkItemSkills x
    WHERE x.assignWorkItemId = awi.assignWorkItemId
      AND x.skillId          = wrs.skillId
);

/* ------------------------------
   2) assignWorkItemSkillDetails (autoAssign = 1)
---------------------------------*/
INSERT INTO assignWorkItemSkillDetails
(assignWorkItemSkillId, assignStatusId, laborQty, dailyWagePerLabor)
SELECT
    awis.assignWorkItemSkillId,
    1 AS assignStatusId, -- autoAssign
    wrs.minRequireLabors AS laborQty,
    wrs.minDailyWage     AS dailyWagePerLabor
FROM assignWorkItemSkills awis
JOIN assignWorkItems awi
    ON awi.assignWorkItemId = awis.assignWorkItemId
JOIN assignProjects ap
    ON ap.assignProjectId = awi.assignProjectId
JOIN projectDetails pd
    ON pd.projectTypeId     = ap.projectTypeId
   AND pd.projectLevelId    = ap.projectLevelId
   AND pd.projectBuildingId = ap.projectBuildingId
JOIN workItemDetails wid
    ON wid.projectDetailId   = pd.projectDetailId
   AND wid.projectWorkItemId = awi.projectWorkItemId
JOIN workItemRequireSkills wrs
    ON wrs.workItemDetailId = wid.workItemDetailId
   AND wrs.skillId          = awis.skillId
WHERE NOT EXISTS (
    SELECT 1
    FROM assignWorkItemSkillDetails d
    WHERE d.assignWorkItemSkillId = awis.assignWorkItemSkillId
      AND d.assignStatusId        = 1
);

/* ------------------------------
   3) assignWorkItemSkillDetails (actualResult = 3)
---------------------------------*/
INSERT INTO assignWorkItemSkillDetails
(assignWorkItemSkillId, assignStatusId, laborQty, dailyWagePerLabor)
SELECT
    awis.assignWorkItemSkillId,
    3 AS assignStatusId, -- actualResult
    ROUND(wrs.minRequireLabors + (wrs.maxRequireLabors - wrs.minRequireLabors) * 0.60, 2) AS laborQty,
    ROUND(wrs.minDailyWage     + (wrs.maxDailyWage     - wrs.minDailyWage)     * 0.50, 2) AS dailyWagePerLabor
FROM assignWorkItemSkills awis
JOIN assignWorkItems awi
    ON awi.assignWorkItemId = awis.assignWorkItemId
JOIN assignProjects ap
    ON ap.assignProjectId = awi.assignProjectId
JOIN projectDetails pd
    ON pd.projectTypeId     = ap.projectTypeId
   AND pd.projectLevelId    = ap.projectLevelId
   AND pd.projectBuildingId = ap.projectBuildingId
JOIN workItemDetails wid
    ON wid.projectDetailId   = pd.projectDetailId
   AND wid.projectWorkItemId = awi.projectWorkItemId
JOIN workItemRequireSkills wrs
    ON wrs.workItemDetailId = wid.workItemDetailId
   AND wrs.skillId          = awis.skillId
WHERE NOT EXISTS (
    SELECT 1
    FROM assignWorkItemSkillDetails d
    WHERE d.assignWorkItemSkillId = awis.assignWorkItemSkillId
      AND d.assignStatusId        = 3
);

/* ------------------------------
   4) dailyReportLabors
   Insert up to 2 labors per daily report (simple deterministic pick).
   We pick the smallest 2 laborId values that match any skill required by the assignWorkItem.
   Wage uses actualResult (status=3) for that skill on that work item.
---------------------------------*/

-- 4A) First labor per daily report (minimum laborId that matches)
INSERT INTO dailyReportLabors (dailyReportId, laborId, workHours, dailyWage, remark)
SELECT
    dr.dailyReportId,
    MIN(l.laborId) AS laborId,
    8 AS workHours,
    COALESCE(MAX(awsd.dailyWagePerLabor), 0) AS dailyWage,
    'Auto-seeded (1st labor)' AS remark
FROM dailyReports dr
JOIN assignWorkItemSkills awis
    ON awis.assignWorkItemId = dr.assignWorkItemId
JOIN labors l
    ON l.skillId = awis.skillId
LEFT JOIN assignWorkItemSkillDetails awsd
    ON awsd.assignWorkItemSkillId = awis.assignWorkItemSkillId
   AND awsd.assignStatusId = 3
WHERE NOT EXISTS (
    SELECT 1 FROM dailyReportLabors x
    WHERE x.dailyReportId = dr.dailyReportId
)
GROUP BY dr.dailyReportId;

-- 4B) Second labor per daily report (next laborId > first one)
INSERT INTO dailyReportLabors (dailyReportId, laborId, workHours, dailyWage, remark)
SELECT
    dr.dailyReportId,
    MIN(l2.laborId) AS laborId,
    8 AS workHours,
    COALESCE(MAX(awsd.dailyWagePerLabor), 0) AS dailyWage,
    'Auto-seeded (2nd labor)' AS remark
FROM dailyReports dr
JOIN dailyReportLabors firstL
    ON firstL.dailyReportId = dr.dailyReportId
JOIN assignWorkItemSkills awis
    ON awis.assignWorkItemId = dr.assignWorkItemId
JOIN labors l2
    ON l2.skillId = awis.skillId
   AND l2.laborId > firstL.laborId
LEFT JOIN assignWorkItemSkillDetails awsd
    ON awsd.assignWorkItemSkillId = awis.assignWorkItemSkillId
   AND awsd.assignStatusId = 3
WHERE NOT EXISTS (
    SELECT 1 FROM dailyReportLabors x
    WHERE x.dailyReportId = dr.dailyReportId
      AND x.laborId = l2.laborId
)
GROUP BY dr.dailyReportId;

-- ==========================================================
-- End of patch v3
-- ==========================================================
