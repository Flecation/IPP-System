DROP DATABASE IPPSystem;

CREATE DATABASE IPPSystem;

USE IPPSystem;
-- for master tables

CREATE TABLE users (
	userId int primary key auto_increment,
    userName varchar(255),
    userRole enum('manager','supervisor'),
    userPhone varchar(255) unique,
    userEmail varchar(255) unique,
    userDOB date,
    userAddress longtext,
    userPassword varchar(255) not null,
    userPhoto varchar(255),
    userStartDate Date ,
    userEndDate Date Default null,
    isActive boolean default true
);

CREATE TABLE skills (
    skillId INT PRIMARY KEY AUTO_INCREMENT,
    skillName VARCHAR(255)
);

create table projectTypes (
	projectTypeId int primary key auto_increment,
    typeName varchar(255)
);

create table projectLevels (
	projectLevelId int primary key auto_increment,
    projectLevelName varchar(255)
);

create table workItems (
	projectWorkItemId int primary key auto_increment,
    projectWorkItemName varchar(255)
);

create table tasks (
	projectTaskId int primary key auto_increment,
    projectTaskName varchar(255)
);

create table buildings (
	projectBuildingId int primary key auto_increment,
    projectBuildingName varchar(255)
);

create table proficiencyLevels(
    proficiencyLevelId int primary key auto_increment,
    proficiencyLevelName varchar(250)
);

create table labors (
	laborId int primary key auto_increment,
     laborName varchar(255),
     laborNRC varchar(255) unique not null,
     laborPhone varchar(255),
     skillId int,
     laborStartDate Date,
     laborEndDate Date,
     proficiencyLevelId int,
     yearsExperience INT DEFAULT 1,
     isActive boolean default true
);

-- for template tables (standard assign tables)
create table projectDetails (
	projectDetailId int primary key auto_increment,
    projectTypeId int not null,
    projectLevelId int,
    projectBuildingId int,
	minOverHeadCost double,
    maxOverHeadCost double
);

create table workItemDetails (
	workItemDetailId int primary key auto_increment,
    projectDetailId int,
    projectWorkItemId int not null,
	minDuration double,
    maxDuration double,
    minLabors double,
    maxLabors double,
    minCost double,
    maxCost double
);

create table taskDetails (
	taskDetailId int primary key auto_increment,
    workItemDetailId int,
    projectTaskId int,
    minDuration double,
    maxDuration double,
    quantityFormula VARCHAR(255),
    unitOfMeasure VARCHAR(50)
);

create table workItemRequireSkills (
	workItemRequireSkillId int primary key auto_increment,
    workItemDetailId int,
    skillId int,
    minRequireLabors double,
    maxRequireLabors double,
    minDailyWage double,
    maxDailyWage double
);

-- for the status enum
create table assignStatus(
    assignStatusId int primary key auto_increment,
    assignStatusName varchar(255) -- autoAssign,customAssign,actualResult, extraAssign
);

create table projectStatus(
    projectStatusId int primary key auto_increment,
    projectStatusName varchar(255) -- planning,inProgress,delay,finished,cancel
);

-- for the real project assign
create table assignProjects (
	assignProjectId int primary key auto_increment,
    projectTypeId int,
    projectInstanceName varchar(255),
    projectLevelId int,
    projectBuildingId int,
    projectArea double, -- only for sq ft unit
    projectHeight double default 0, -- only for religious
    totalStories double, -- for all floors
    totalUnits double, -- for all units/ rooms ,in the backend the unit per floor will calculate
    supervisorId int,
    projectLocation varchar(255),
    projectOverHeadCost double,
    projectStatus int,
    actualCost double,
    progress_percentage DOUBLE,
    targetEndDate date,
    FOREIGN KEY (projectStatus) REFERENCES projectStatus(projectStatusId) ON UPDATE CASCADE ON DELETE CASCADE
);

create table assignProjectDetails(
    assignProjectDetailId int primary key auto_increment,
    assignProjectId int ,
    assignStatusId int,
    projectCost double,
    projectLaborQty double,
    projectDuration double,
    startDate date,
    endDate date,
    foreign key (assignProjectId)
    references assignProjects (assignProjectId)
    on update cascade
    on delete cascade,
    FOREIGN KEY (assignStatusId) REFERENCES assignStatus(assignStatusId) ON UPDATE CASCADE ON DELETE CASCADE
);

create table assignWorkItems (
	assignWorkItemId int primary key auto_increment,
    assignProjectId int,
    projectWorkItemId int,
    workItemStatus int,
    FOREIGN KEY (workItemStatus) REFERENCES projectStatus(projectStatusId) ON UPDATE CASCADE ON DELETE CASCADE
);

create table assignWorkItemDetails(
    assignWorkItemDetailId int primary key auto_increment,
    assignWorkItemId int,
    assignStatusId int,
    workItemCost double,
    workItemLaborQty double,
    workItemDuration double,
    startDate date,
    endDate date,
    foreign key (assignWorkItemId)
    references assignWorkItems (assignWorkItemId)
    on update cascade
    on delete cascade,
    FOREIGN KEY (assignStatusId) REFERENCES assignStatus(assignStatusId) ON UPDATE CASCADE ON DELETE CASCADE
);

create table assignTasks (
	assignTaskId int primary key auto_increment,
    assignWorkItemId int,
    projectTaskId int,
    isCancel boolean default false,
    taskStatus int,
    plannedQty DOUBLE NOT NULL,
    unitOfMeasure VARCHAR(50) NOT NULL,
    FOREIGN KEY (taskStatus) REFERENCES projectStatus(projectStatusId) ON UPDATE CASCADE ON DELETE CASCADE
);

create table assignTaskDetails(
    assignTaskDetailId int primary key auto_increment,
    assignTaskId int,
    assignStatusId int,
    taskDuration double,
    startDate date,
    endDate date,
    foreign key (assignTaskId)
    references assignTasks (assignTaskId)
    on update cascade
    on delete cascade,
    FOREIGN KEY (assignStatusId) REFERENCES assignStatus(assignStatusId) ON UPDATE CASCADE ON DELETE CASCADE
);


create table assignWorkItemSkills (
	assignWorkItemSkillId int primary key auto_increment,
    assignWorkItemId int,
    skillId int,
    isCancel boolean default false
);

CREATE TABLE assignWorkItemSkillDetails(
    assignWorkItemSkillDetailId INT PRIMARY KEY AUTO_INCREMENT,
    assignWorkItemSkillId INT,
    assignStatusId INT,
    laborQty DOUBLE,
    dailyWagePerLabor DOUBLE,
    FOREIGN KEY (assignStatusId)
        REFERENCES assignStatus(assignStatusId)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    FOREIGN KEY (assignWorkItemSkillId)
        REFERENCES assignWorkItemSkills(assignWorkItemSkillId)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);


create table assignWorkers (
	assignWorkerId int primary key auto_increment,
    assignProjectId int,
    workerId int,
    isCancel boolean default false
);

CREATE TABLE dailyReports (
    dailyReportId INT PRIMARY KEY AUTO_INCREMENT,
    assignProjectId INT NOT NULL,
    reportDate DATE NOT NULL,
    supervisorId INT,
    actualCost  DOUBLE,
    progress_percentage DOUBLE,
    weather VARCHAR(100),
    generalRemark TEXT,
    issue LONGTEXT,
    UNIQUE (assignProjectId, reportDate),

    FOREIGN KEY (assignProjectId)
        REFERENCES assignProjects(assignProjectId)
        ON DELETE CASCADE,
    FOREIGN KEY (supervisorId)
        REFERENCES users(userId)
        ON DELETE SET NULL
);

CREATE TABLE dailyReportTasks (
    dailyReportTaskId INT PRIMARY KEY AUTO_INCREMENT,
    dailyReportId INT NOT NULL,
    assignTaskId INT,
    progressDescription TEXT,
    workHours DOUBLE,
    completedQty DOUBLE,
    dailyCost DOUBLE,
    isCompleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (dailyReportId)
        REFERENCES dailyReports(dailyReportId)
        ON DELETE CASCADE,
    FOREIGN KEY (assignTaskId)
        REFERENCES assignTasks(assignTaskId)
);

CREATE TABLE dailyReportLabors (
    dailyReportLaborId INT PRIMARY KEY AUTO_INCREMENT,
    dailyReportId INT NOT NULL,
    laborId INT NOT NULL,
    workHours DOUBLE,
    dailyWage DOUBLE,
    remark TEXT,

    FOREIGN KEY (dailyReportId)
        REFERENCES dailyReports(dailyReportId)
        ON DELETE CASCADE,
    FOREIGN KEY (laborId)
        REFERENCES labors(laborId) ON DELETE CASCADE
);


-- =====================
-- adding foreign key
-- =====================

-- master file link foreign key
ALTER TABLE labors
ADD CONSTRAINT fk_labors_skill
FOREIGN KEY (skillId)
REFERENCES skills(skillId)
ON UPDATE CASCADE
ON DELETE CASCADE;

-- template structure
ALTER TABLE projectDetails
ADD CONSTRAINT fk_pd_projectType
FOREIGN KEY (projectTypeId)
REFERENCES projectTypes(projectTypeId)
ON UPDATE CASCADE
ON DELETE CASCADE;

ALTER TABLE projectDetails
ADD CONSTRAINT fk_pd_level
FOREIGN KEY (projectLevelId)
REFERENCES projectLevels(projectLevelId)
ON UPDATE CASCADE
ON DELETE CASCADE;

ALTER TABLE projectDetails
ADD CONSTRAINT fk_pd_building
FOREIGN KEY (projectBuildingId)
REFERENCES buildings(projectBuildingId)
ON UPDATE CASCADE
ON DELETE CASCADE;

ALTER TABLE workItemDetails
ADD CONSTRAINT fk_wid_projectDetail
FOREIGN KEY (projectDetailId)
REFERENCES projectDetails(projectDetailId)
ON UPDATE CASCADE
ON DELETE CASCADE;

ALTER TABLE workItemDetails
ADD CONSTRAINT fk_wid_workItemId
FOREIGN KEY (projectWorkItemId)
REFERENCES workItems(projectWorkItemId)
ON UPDATE CASCADE
ON DELETE CASCADE;

ALTER TABLE taskDetails
ADD CONSTRAINT fk_td_workItemDetail
FOREIGN KEY (workItemDetailId)
REFERENCES workItemDetails(workItemDetailId)
ON UPDATE CASCADE
ON DELETE CASCADE;

ALTER TABLE taskDetails
ADD CONSTRAINT fk_td_task
FOREIGN KEY (projectTaskId)
REFERENCES tasks(projectTaskId)
ON UPDATE CASCADE
ON DELETE CASCADE;

ALTER TABLE workItemRequireSkills
ADD CONSTRAINT fk_wirs_taskDetail
FOREIGN KEY (workItemDetailId)
REFERENCES workItemDetails(workItemDetailId)
ON UPDATE CASCADE
ON DELETE CASCADE;

ALTER TABLE workItemRequireSkills
ADD CONSTRAINT fk_wirs_skill
FOREIGN KEY (skillId)
REFERENCES skills(skillId)
ON UPDATE CASCADE
ON DELETE CASCADE;

ALTER TABLE assignProjects
ADD CONSTRAINT fk_ap_projectType
FOREIGN KEY (projectTypeId)
REFERENCES projectTypes(projectTypeId)
ON UPDATE CASCADE
ON DELETE CASCADE;

ALTER TABLE assignProjects
ADD CONSTRAINT fk_ap_level
FOREIGN KEY (projectLevelId)
REFERENCES projectLevels(projectLevelId)
ON UPDATE CASCADE
ON DELETE CASCADE;

ALTER TABLE assignProjects
ADD CONSTRAINT fk_ap_building
FOREIGN KEY (projectBuildingId)
REFERENCES buildings(projectBuildingId)
ON UPDATE CASCADE
ON DELETE CASCADE;


ALTER TABLE assignProjects
ADD CONSTRAINT fk_ap_manager
FOREIGN KEY (supervisorId)
REFERENCES users(userId)
ON UPDATE CASCADE
ON DELETE CASCADE;

ALTER TABLE assignWorkItems
ADD CONSTRAINT fk_awi_project
FOREIGN KEY (assignProjectId)
REFERENCES assignProjects(assignProjectId)
ON UPDATE CASCADE
ON DELETE CASCADE;

ALTER TABLE assignWorkItems
ADD CONSTRAINT fk_awi_workItem
FOREIGN KEY (projectWorkItemId)
REFERENCES workItems(projectWorkItemId)
ON UPDATE CASCADE
ON DELETE CASCADE;

ALTER TABLE assignTasks
ADD CONSTRAINT fk_at_assignWorkItem
FOREIGN KEY (assignWorkItemId)
REFERENCES assignWorkItems(assignWorkItemId)
ON UPDATE CASCADE
ON DELETE CASCADE;

ALTER TABLE assignTasks
ADD CONSTRAINT fk_at_task
FOREIGN KEY (projectTaskId)
REFERENCES tasks(projectTaskId)
ON UPDATE CASCADE
ON DELETE CASCADE;

ALTER TABLE assignWorkItemSkills
ADD CONSTRAINT fk_awis_assignWorkItem
FOREIGN KEY (assignWorkItemId)
REFERENCES assignWorkItems(assignWorkItemId)
ON UPDATE CASCADE
ON DELETE CASCADE;

ALTER TABLE assignWorkItemSkills
ADD CONSTRAINT fk_awis_skill
FOREIGN KEY (skillId)
REFERENCES skills(skillId)
ON UPDATE CASCADE
ON DELETE CASCADE;

ALTER TABLE assignWorkers
ADD CONSTRAINT fk_aw_project
FOREIGN KEY (assignProjectId)
REFERENCES assignProjects(assignProjectId)
ON UPDATE CASCADE
ON DELETE CASCADE;

ALTER TABLE assignWorkers
ADD CONSTRAINT fk_aw_oldWorker
FOREIGN KEY (workerId)
REFERENCES labors(laborId)
ON UPDATE CASCADE
ON DELETE CASCADE;