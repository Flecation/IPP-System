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
