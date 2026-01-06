DROP DATABASE IPPSystem;

CREATE DATABASE IPPSystem;

USE IPPSystem;
-- for master tables 
 
CREATE TABLE users (
	userId int primary key auto_increment,
    userName varchar(255),
    userRole enum('manager','supervisor'),
    userPhone varchar(255),
    userEmail varchar(255),
    userDOB date,
    userPassword varchar(255) not null,
    userStartDate Date ,
    userEndDate Date Default null,
    isActive boolean default true
);

create table skills (
	skillId int primary key auto_increment,
    skillName varchar(255)
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

create table labors (
	laborId int primary key auto_increment,
     laborName varchar(255),
     skillId int,
     laborStartDate Date,
     laborEndDate Date,
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
    maxDuration double
);

create table workItemRequireSkills (
	workItemRequireSkills int primary key auto_increment,
    workItemDetailId int,
    skillId int,
    minRequireLabors double,
    maxRequireLabors double,
    basicDailyWage double,
    maxDailyWage double
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
    managerId int,
    projectLocation varchar(255),
    projectOverHeadCost double,
    projectStatus enum('planning','inProgress','delay','finished','cancel')
);

create table assignProjectDetails(
    assignProjectDetailId int primary key auto_increment,
    assignProjectId int ,
    detailStatus enum('autoAssign','customAssign','actualResult'),
    projectDuration double,
    projectCost double,
    projectLaborQty double,
    startDate date,
    endDate date,
    foreign key (assignProjectId)
    references assignProjects (assignProjectId)
    on update cascade
    on delete cascade
);

create table assignWorkItems (
	assignWorkItemId int primary key auto_increment,
    assignProjectId int,
    projectWorkItemId int
);

create table assignWorkItemDetails(
    assignWorkItemDetailId int primary key auto_increment,
    assignWorkItemId int,
    detailStatus enum('autoAssign','customAssign','actualResult'),
    workItemDuration double,
    workItemCost double,
    workItemLaborQty double,
    startDate date,
    endDate date,
    foreign key (assignWorkItemId)
    references assignWorkItems (assignWorkItemId)
    on update cascade
    on delete cascade
);

create table assignTasks (
	assignTaskId int primary key auto_increment,
    assignWorkItemId int,
    projectTaskId int,
    isCancel boolean default false
);

create table assignTaskDetails(
    assignTakDetailId int primary key auto_increment,
    assignTaskId int,
    detailStatus enum('autoAssign','customAssign','actualResult'),
    taskDuration double,
    startDate date,
    endDate date,
    foreign key (assignTaskId)
    references assignTasks (assignTaskId)
    on update cascade
    on delete cascade
);


create table assignWorkItemSkills (
	assignWorkItemSkillId int primary key auto_increment,
    assignWorkItemId int,
    skillId int,
    laborQty double,
    isCustomize boolean,
    isCancel boolean default false
);

create table assignWorkers (
	assignWorkerId int primary key auto_increment,
    assignProjectId int,
    workerId int,
    isCustomize boolean,
    isCancel boolean default false
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
FOREIGN KEY (managerId)
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