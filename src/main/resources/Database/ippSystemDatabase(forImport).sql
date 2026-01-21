CREATE DATABASE  IF NOT EXISTS `ippsystem` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `ippsystem`;
-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: ippsystem
-- ------------------------------------------------------
-- Server version	9.5.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '29c66e07-b7c2-11f0-a44b-002b675283cc:1-2905';

--
-- Table structure for table `assignprojectdetails`
--

DROP TABLE IF EXISTS `assignprojectdetails`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assignprojectdetails` (
  `assignProjectDetailId` int NOT NULL AUTO_INCREMENT,
  `assignProjectId` int DEFAULT NULL,
  `assignStatusId` int DEFAULT NULL,
  `projectCost` double DEFAULT NULL,
  `projectLaborQty` double DEFAULT NULL,
  `projectDuration` double DEFAULT NULL,
  `startDate` date DEFAULT NULL,
  `endDate` date DEFAULT NULL,
  PRIMARY KEY (`assignProjectDetailId`),
  KEY `assignProjectId` (`assignProjectId`),
  KEY `assignStatusId` (`assignStatusId`),
  CONSTRAINT `assignprojectdetails_ibfk_1` FOREIGN KEY (`assignProjectId`) REFERENCES `assignprojects` (`assignProjectId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `assignprojectdetails_ibfk_2` FOREIGN KEY (`assignStatusId`) REFERENCES `assignstatus` (`assignStatusId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assignprojectdetails`
--

LOCK TABLES `assignprojectdetails` WRITE;
/*!40000 ALTER TABLE `assignprojectdetails` DISABLE KEYS */;
INSERT INTO `assignprojectdetails` VALUES (1,1,1,1500000,50,240,'2024-02-01','2024-10-01'),(2,2,1,3500000,80,420,'2024-03-15','2025-05-15'),(3,3,1,5000000,120,540,'2024-06-01','2025-12-01'),(4,4,1,2000000,60,180,'2024-04-01','2024-09-28'),(5,5,1,1200000,40,300,'2024-05-01','2025-02-25');
/*!40000 ALTER TABLE `assignprojectdetails` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `assignprojects`
--

DROP TABLE IF EXISTS `assignprojects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assignprojects` (
  `assignProjectId` int NOT NULL AUTO_INCREMENT,
  `projectTypeId` int DEFAULT NULL,
  `projectInstanceName` varchar(255) DEFAULT NULL,
  `projectLevelId` int DEFAULT NULL,
  `projectBuildingId` int DEFAULT NULL,
  `projectArea` double DEFAULT NULL,
  `projectHeight` double DEFAULT '0',
  `totalStories` double DEFAULT NULL,
  `totalUnits` double DEFAULT NULL,
  `supervisorId` int DEFAULT NULL,
  `projectLocation` varchar(255) DEFAULT NULL,
  `projectOverHeadCost` double DEFAULT NULL,
  `projectStatus` int DEFAULT NULL,
  PRIMARY KEY (`assignProjectId`),
  KEY `projectStatus` (`projectStatus`),
  KEY `fk_ap_projectType` (`projectTypeId`),
  KEY `fk_ap_level` (`projectLevelId`),
  KEY `fk_ap_building` (`projectBuildingId`),
  KEY `fk_ap_manager` (`supervisorId`),
  CONSTRAINT `assignprojects_ibfk_1` FOREIGN KEY (`projectStatus`) REFERENCES `projectstatus` (`projectStatusId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_ap_building` FOREIGN KEY (`projectBuildingId`) REFERENCES `buildings` (`projectBuildingId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_ap_level` FOREIGN KEY (`projectLevelId`) REFERENCES `projectlevels` (`projectLevelId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_ap_manager` FOREIGN KEY (`supervisorId`) REFERENCES `users` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_ap_projectType` FOREIGN KEY (`projectTypeId`) REFERENCES `projecttypes` (`projectTypeId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assignprojects`
--

LOCK TABLES `assignprojects` WRITE;
/*!40000 ALTER TABLE `assignprojects` DISABLE KEYS */;
INSERT INTO `assignprojects` VALUES (1,1,'Golden Tower Apartments',3,1,12000,45,5,20,2,'Yangon, Hlaing Township',18000,1),(2,2,'Skyline Office Tower',4,5,25000,120,12,48,3,'Mandalay, Chanayethazan',35000,1),(3,4,'Maha Bandoola Bridge',8,13,1500,25,1,4,4,'Yangon River Crossing',500000,1),(4,3,'Yangon Garment Factory',1,9,50000,20,1,10,5,'Yangon, Dagon Seikkan',25000,1),(5,5,'Al-Amin Grand Mosque',8,16,8000,60,2,1,6,'Naypyidaw, Zabuthiri',30000,1);
/*!40000 ALTER TABLE `assignprojects` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `assignstatus`
--

DROP TABLE IF EXISTS `assignstatus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assignstatus` (
  `assignStatusId` int NOT NULL AUTO_INCREMENT,
  `assignStatusName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`assignStatusId`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assignstatus`
--

LOCK TABLES `assignstatus` WRITE;
/*!40000 ALTER TABLE `assignstatus` DISABLE KEYS */;
INSERT INTO `assignstatus` VALUES (1,'autoAssign'),(2,'customAssign'),(3,'actualResult'),(4,'extraAssign');
/*!40000 ALTER TABLE `assignstatus` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `assigntaskdetails`
--

DROP TABLE IF EXISTS `assigntaskdetails`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assigntaskdetails` (
  `assignTaskDetailId` int NOT NULL AUTO_INCREMENT,
  `assignTaskId` int DEFAULT NULL,
  `assignStatusId` int DEFAULT NULL,
  `taskDuration` double DEFAULT NULL,
  `startDate` date DEFAULT NULL,
  `endDate` date DEFAULT NULL,
  PRIMARY KEY (`assignTaskDetailId`),
  KEY `assignTaskId` (`assignTaskId`),
  KEY `assignStatusId` (`assignStatusId`),
  CONSTRAINT `assigntaskdetails_ibfk_1` FOREIGN KEY (`assignTaskId`) REFERENCES `assigntasks` (`assignTaskId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `assigntaskdetails_ibfk_2` FOREIGN KEY (`assignStatusId`) REFERENCES `assignstatus` (`assignStatusId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assigntaskdetails`
--

LOCK TABLES `assigntaskdetails` WRITE;
/*!40000 ALTER TABLE `assigntaskdetails` DISABLE KEYS */;
INSERT INTO `assigntaskdetails` VALUES (1,1,1,10,'2024-02-01','2024-02-10'),(2,2,1,8,'2024-02-11','2024-02-18'),(3,3,1,6,'2024-02-19','2024-02-24'),(4,4,1,3,'2024-02-25','2024-02-27'),(5,5,1,3,'2024-02-28','2024-03-02'),(6,6,1,40,'2024-03-03','2024-04-11'),(7,7,1,30,'2024-04-12','2024-05-11'),(8,8,1,20,'2024-05-12','2024-05-31'),(9,9,1,25,'2024-06-02','2024-06-26'),(10,10,1,20,'2024-06-27','2024-07-16'),(11,11,1,15,'2024-07-17','2024-07-31'),(12,12,1,10,'2024-08-01','2024-08-10'),(13,13,1,15,'2024-08-11','2024-08-25'),(14,14,1,15,'2024-08-26','2024-09-09'),(15,15,1,10,'2024-09-10','2024-09-19'),(16,16,1,7,'2024-09-20','2024-09-26'),(17,17,1,5,'2024-09-27','2024-10-01'),(18,18,1,3,'2024-09-27','2024-09-29'),(19,19,1,20,'2024-03-15','2024-04-03'),(20,20,1,15,'2024-04-04','2024-04-18'),(21,21,1,10,'2024-04-19','2024-04-28'),(22,22,1,80,'2024-04-29','2024-07-17'),(23,23,1,60,'2024-07-18','2024-09-15'),(24,24,1,40,'2024-09-16','2024-10-25'),(25,25,1,45,'2024-06-01','2024-07-15'),(26,26,1,60,'2024-07-16','2024-09-13'),(27,27,1,75,'2024-09-14','2024-11-27'),(28,28,1,120,'2024-11-28','2025-03-27'),(29,29,1,60,'2025-03-28','2025-05-26'),(30,30,1,60,'2025-05-27','2025-07-26'),(31,31,1,30,'2025-07-27','2025-08-25'),(32,32,1,30,'2025-08-26','2025-09-24'),(33,33,1,30,'2025-09-25','2025-10-24'),(34,34,1,15,'2024-05-01','2024-05-15'),(35,35,1,20,'2024-05-16','2024-06-04'),(36,36,1,10,'2024-06-05','2024-06-14');
/*!40000 ALTER TABLE `assigntaskdetails` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `assigntasks`
--

DROP TABLE IF EXISTS `assigntasks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assigntasks` (
  `assignTaskId` int NOT NULL AUTO_INCREMENT,
  `assignWorkItemId` int DEFAULT NULL,
  `projectTaskId` int DEFAULT NULL,
  `isCancel` tinyint(1) DEFAULT '0',
  `taskStatus` int DEFAULT NULL,
  PRIMARY KEY (`assignTaskId`),
  KEY `taskStatus` (`taskStatus`),
  KEY `fk_at_assignWorkItem` (`assignWorkItemId`),
  KEY `fk_at_task` (`projectTaskId`),
  CONSTRAINT `assigntasks_ibfk_1` FOREIGN KEY (`taskStatus`) REFERENCES `projectstatus` (`projectStatusId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_at_assignWorkItem` FOREIGN KEY (`assignWorkItemId`) REFERENCES `assignworkitems` (`assignWorkItemId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_at_task` FOREIGN KEY (`projectTaskId`) REFERENCES `tasks` (`projectTaskId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assigntasks`
--

LOCK TABLES `assigntasks` WRITE;
/*!40000 ALTER TABLE `assigntasks` DISABLE KEYS */;
INSERT INTO `assigntasks` VALUES (1,1,1,0,1),(2,1,2,0,1),(3,1,3,0,1),(4,1,4,0,1),(5,1,5,0,1),(6,2,6,0,1),(7,2,7,0,1),(8,2,8,0,1),(9,3,10,0,1),(10,3,11,0,1),(11,3,12,0,1),(12,3,13,0,1),(13,4,14,0,1),(14,4,15,0,1),(15,4,16,0,1),(16,5,17,0,1),(17,5,18,0,1),(18,5,19,0,1),(19,6,1,0,1),(20,6,2,0,1),(21,6,3,0,1),(22,8,6,0,1),(23,8,7,0,1),(24,8,8,0,1),(25,12,20,0,1),(26,12,21,0,1),(27,12,28,0,1),(28,13,27,0,1),(29,13,7,0,1),(30,13,8,0,1),(31,14,11,0,1),(32,14,32,0,1),(33,14,33,0,1),(34,22,1,0,1),(35,22,2,0,1),(36,22,5,0,1);
/*!40000 ALTER TABLE `assigntasks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `assignworkers`
--

DROP TABLE IF EXISTS `assignworkers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assignworkers` (
  `assignWorkerId` int NOT NULL AUTO_INCREMENT,
  `assignProjectId` int DEFAULT NULL,
  `workerId` int DEFAULT NULL,
  `isCancel` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`assignWorkerId`),
  KEY `fk_aw_project` (`assignProjectId`),
  KEY `fk_aw_oldWorker` (`workerId`),
  CONSTRAINT `fk_aw_oldWorker` FOREIGN KEY (`workerId`) REFERENCES `labors` (`laborId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_aw_project` FOREIGN KEY (`assignProjectId`) REFERENCES `assignprojects` (`assignProjectId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assignworkers`
--

LOCK TABLES `assignworkers` WRITE;
/*!40000 ALTER TABLE `assignworkers` DISABLE KEYS */;
/*!40000 ALTER TABLE `assignworkers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `assignworkitemdetails`
--

DROP TABLE IF EXISTS `assignworkitemdetails`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assignworkitemdetails` (
  `assignWorkItemDetailId` int NOT NULL AUTO_INCREMENT,
  `assignWorkItemId` int DEFAULT NULL,
  `assignStatusId` int DEFAULT NULL,
  `workItemCost` double DEFAULT NULL,
  `workItemLaborQty` double DEFAULT NULL,
  `workItemDuration` double DEFAULT NULL,
  `startDate` date DEFAULT NULL,
  `endDate` date DEFAULT NULL,
  PRIMARY KEY (`assignWorkItemDetailId`),
  KEY `assignWorkItemId` (`assignWorkItemId`),
  KEY `assignStatusId` (`assignStatusId`),
  CONSTRAINT `assignworkitemdetails_ibfk_1` FOREIGN KEY (`assignWorkItemId`) REFERENCES `assignworkitems` (`assignWorkItemId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `assignworkitemdetails_ibfk_2` FOREIGN KEY (`assignStatusId`) REFERENCES `assignstatus` (`assignStatusId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assignworkitemdetails`
--

LOCK TABLES `assignworkitemdetails` WRITE;
/*!40000 ALTER TABLE `assignworkitemdetails` DISABLE KEYS */;
INSERT INTO `assignworkitemdetails` VALUES (1,1,1,225000,8,30,'2024-02-01','2024-03-02'),(2,2,1,510000,20,90,'2024-03-03','2024-06-01'),(3,3,1,420000,15,70,'2024-06-02','2024-08-10'),(4,4,1,330000,10,40,'2024-08-11','2024-09-19'),(5,5,1,150000,7,15,'2024-09-20','2024-10-01'),(6,6,1,560000,15,45,'2024-03-15','2024-04-28'),(7,8,1,1365000,35,180,'2024-04-29','2024-10-25'),(8,9,1,1050000,25,120,'2024-10-26','2025-02-23'),(9,10,1,490000,15,75,'2025-02-24','2025-05-09'),(10,11,1,35000,5,6,'2025-05-10','2025-05-15'),(11,12,1,1500000,40,180,'2024-06-01','2024-11-27'),(12,13,1,2400000,50,240,'2024-11-28','2025-07-26'),(13,14,1,750000,20,90,'2025-07-27','2025-10-24'),(14,15,1,300000,10,30,'2025-10-25','2025-11-23'),(15,16,1,50000,8,8,'2025-11-24','2025-12-01'),(16,17,1,300000,12,30,'2024-04-01','2024-04-30'),(17,18,1,1000000,25,90,'2024-05-01','2024-07-29'),(18,19,1,300000,10,30,'2024-07-30','2024-08-28'),(19,20,1,360000,13,25,'2024-08-29','2024-09-22'),(20,21,1,40000,5,6,'2024-09-23','2024-09-28'),(21,22,1,216000,10,45,'2024-05-01','2024-06-14'),(22,23,1,540000,15,135,'2024-06-15','2024-10-28'),(23,24,1,300000,10,90,'2024-10-29','2025-01-27'),(24,25,1,120000,5,25,'2025-01-28','2025-02-21'),(25,26,1,24000,5,4,'2025-02-22','2025-02-25');
/*!40000 ALTER TABLE `assignworkitemdetails` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `assignworkitems`
--

DROP TABLE IF EXISTS `assignworkitems`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assignworkitems` (
  `assignWorkItemId` int NOT NULL AUTO_INCREMENT,
  `assignProjectId` int DEFAULT NULL,
  `projectWorkItemId` int DEFAULT NULL,
  `workItemStatus` int DEFAULT NULL,
  PRIMARY KEY (`assignWorkItemId`),
  KEY `workItemStatus` (`workItemStatus`),
  KEY `fk_awi_project` (`assignProjectId`),
  KEY `fk_awi_workItem` (`projectWorkItemId`),
  CONSTRAINT `assignworkitems_ibfk_1` FOREIGN KEY (`workItemStatus`) REFERENCES `projectstatus` (`projectStatusId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_awi_project` FOREIGN KEY (`assignProjectId`) REFERENCES `assignprojects` (`assignProjectId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_awi_workItem` FOREIGN KEY (`projectWorkItemId`) REFERENCES `workitems` (`projectWorkItemId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assignworkitems`
--

LOCK TABLES `assignworkitems` WRITE;
/*!40000 ALTER TABLE `assignworkitems` DISABLE KEYS */;
INSERT INTO `assignworkitems` VALUES (1,1,1,1),(2,1,2,1),(3,1,3,1),(4,1,4,1),(5,1,5,1),(6,2,1,1),(8,2,2,1),(9,2,3,1),(10,2,4,1),(11,2,5,1),(12,3,1,1),(13,3,2,1),(14,3,3,1),(15,3,4,1),(16,3,5,1),(17,4,1,1),(18,4,2,1),(19,4,3,1),(20,4,4,1),(21,4,5,1),(22,5,1,1),(23,5,2,1),(24,5,3,1),(25,5,4,1),(26,5,5,1);
/*!40000 ALTER TABLE `assignworkitems` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `assignworkitemskilldetails`
--

DROP TABLE IF EXISTS `assignworkitemskilldetails`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assignworkitemskilldetails` (
  `assignWorkItemSkillDetailId` int NOT NULL AUTO_INCREMENT,
  `assignWorkItemSkillId` int DEFAULT NULL,
  `assignStatusId` int DEFAULT NULL,
  `laborQty` double DEFAULT NULL,
  `dailyWagePerLabor` double DEFAULT NULL,
  PRIMARY KEY (`assignWorkItemSkillDetailId`),
  KEY `assignStatusId` (`assignStatusId`),
  KEY `assignWorkItemSkillId` (`assignWorkItemSkillId`),
  CONSTRAINT `assignworkitemskilldetails_ibfk_1` FOREIGN KEY (`assignStatusId`) REFERENCES `assignstatus` (`assignStatusId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `assignworkitemskilldetails_ibfk_2` FOREIGN KEY (`assignWorkItemSkillId`) REFERENCES `assignworkitemskills` (`assignWorkItemSkillId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assignworkitemskilldetails`
--

LOCK TABLES `assignworkitemskilldetails` WRITE;
/*!40000 ALTER TABLE `assignworkitemskilldetails` DISABLE KEYS */;
INSERT INTO `assignworkitemskilldetails` VALUES (1,1,1,4,15),(2,2,1,2,25),(3,3,1,2,30),(4,4,1,6,30),(5,5,1,8,35),(6,6,1,6,28),(7,7,1,4,25),(8,8,1,5,22),(9,9,1,6,28),(10,10,1,4,35),(11,11,1,4,32),(12,12,1,2,40),(13,13,1,4,18),(14,14,1,2,30),(15,15,1,1,25),(16,16,1,3,40),(17,17,1,2,45),(18,18,1,5,50),(19,19,1,5,35),(20,20,1,5,45),(21,21,1,3,60),(22,22,1,2,50),(23,23,1,15,55),(24,24,1,4,35),(25,25,1,2,60),(26,26,1,4,30);
/*!40000 ALTER TABLE `assignworkitemskilldetails` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `assignworkitemskills`
--

DROP TABLE IF EXISTS `assignworkitemskills`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assignworkitemskills` (
  `assignWorkItemSkillId` int NOT NULL AUTO_INCREMENT,
  `assignWorkItemId` int DEFAULT NULL,
  `skillId` int DEFAULT NULL,
  `isCancel` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`assignWorkItemSkillId`),
  KEY `fk_awis_assignWorkItem` (`assignWorkItemId`),
  KEY `fk_awis_skill` (`skillId`),
  CONSTRAINT `fk_awis_assignWorkItem` FOREIGN KEY (`assignWorkItemId`) REFERENCES `assignworkitems` (`assignWorkItemId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_awis_skill` FOREIGN KEY (`skillId`) REFERENCES `skills` (`skillId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assignworkitemskills`
--

LOCK TABLES `assignworkitemskills` WRITE;
/*!40000 ALTER TABLE `assignworkitemskills` DISABLE KEYS */;
INSERT INTO `assignworkitemskills` VALUES (1,1,1,0),(2,1,2,0),(3,1,3,0),(4,2,3,0),(5,2,7,0),(6,2,8,0),(7,3,13,0),(8,3,14,0),(9,3,15,0),(10,4,4,0),(11,4,5,0),(12,4,16,0),(13,5,1,0),(14,5,18,0),(15,5,19,0),(16,6,2,0),(17,6,10,0),(18,6,7,0),(19,6,3,0),(20,11,2,0),(21,11,20,0),(22,11,10,0),(23,11,7,0),(24,21,3,0),(25,21,22,0),(26,21,8,0);
/*!40000 ALTER TABLE `assignworkitemskills` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `buildings`
--

DROP TABLE IF EXISTS `buildings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `buildings` (
  `projectBuildingId` int NOT NULL AUTO_INCREMENT,
  `projectBuildingName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`projectBuildingId`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `buildings`
--

LOCK TABLES `buildings` WRITE;
/*!40000 ALTER TABLE `buildings` DISABLE KEYS */;
INSERT INTO `buildings` VALUES (1,'Apartment'),(2,'Condominium'),(3,'Villa / House'),(4,'Townhouse'),(5,'Office Building'),(6,'School / Educational Building'),(7,'Hospital / Healthcare Building'),(8,'Hotel / Hospitality Building'),(9,'Factory'),(10,'Warehouse'),(11,'Power Plant'),(12,'Road / Highway'),(13,'Bridge'),(14,'Dam / Reservoir'),(15,'Utility / Pipeline'),(16,'Mosque'),(17,'Church'),(18,'Temple'),(19,'Monastery / Convent');
/*!40000 ALTER TABLE `buildings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dailyreportlabors`
--

DROP TABLE IF EXISTS `dailyreportlabors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dailyreportlabors` (
  `dailyReportLaborId` int NOT NULL AUTO_INCREMENT,
  `dailyReportId` int NOT NULL,
  `laborId` int NOT NULL,
  `workHours` double DEFAULT NULL,
  `dailyWage` double DEFAULT NULL,
  `remark` text,
  PRIMARY KEY (`dailyReportLaborId`),
  KEY `dailyReportId` (`dailyReportId`),
  KEY `laborId` (`laborId`),
  CONSTRAINT `dailyreportlabors_ibfk_1` FOREIGN KEY (`dailyReportId`) REFERENCES `dailyreports` (`dailyReportId`) ON DELETE CASCADE,
  CONSTRAINT `dailyreportlabors_ibfk_2` FOREIGN KEY (`laborId`) REFERENCES `labors` (`laborId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dailyreportlabors`
--

LOCK TABLES `dailyreportlabors` WRITE;
/*!40000 ALTER TABLE `dailyreportlabors` DISABLE KEYS */;
/*!40000 ALTER TABLE `dailyreportlabors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dailyreports`
--

DROP TABLE IF EXISTS `dailyreports`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dailyreports` (
  `dailyReportId` int NOT NULL AUTO_INCREMENT,
  `assignProjectId` int NOT NULL,
  `reportDate` date NOT NULL,
  `supervisorId` int DEFAULT NULL,
  `weather` varchar(100) DEFAULT NULL,
  `generalRemark` text,
  `issue` longtext,
  `createdAt` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`dailyReportId`),
  UNIQUE KEY `assignProjectId` (`assignProjectId`,`reportDate`),
  KEY `supervisorId` (`supervisorId`),
  CONSTRAINT `dailyreports_ibfk_1` FOREIGN KEY (`assignProjectId`) REFERENCES `assignprojects` (`assignProjectId`) ON DELETE CASCADE,
  CONSTRAINT `dailyreports_ibfk_2` FOREIGN KEY (`supervisorId`) REFERENCES `users` (`userId`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dailyreports`
--

LOCK TABLES `dailyreports` WRITE;
/*!40000 ALTER TABLE `dailyreports` DISABLE KEYS */;
/*!40000 ALTER TABLE `dailyreports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dailyreporttasks`
--

DROP TABLE IF EXISTS `dailyreporttasks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dailyreporttasks` (
  `dailyReportTaskId` int NOT NULL AUTO_INCREMENT,
  `dailyReportId` int NOT NULL,
  `assignTaskId` int DEFAULT NULL,
  `progressDescription` text,
  `workHours` double DEFAULT NULL,
  `completedQty` double DEFAULT NULL,
  `isCompleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`dailyReportTaskId`),
  KEY `dailyReportId` (`dailyReportId`),
  KEY `assignTaskId` (`assignTaskId`),
  CONSTRAINT `dailyreporttasks_ibfk_1` FOREIGN KEY (`dailyReportId`) REFERENCES `dailyreports` (`dailyReportId`) ON DELETE CASCADE,
  CONSTRAINT `dailyreporttasks_ibfk_2` FOREIGN KEY (`assignTaskId`) REFERENCES `assigntasks` (`assignTaskId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dailyreporttasks`
--

LOCK TABLES `dailyreporttasks` WRITE;
/*!40000 ALTER TABLE `dailyreporttasks` DISABLE KEYS */;
/*!40000 ALTER TABLE `dailyreporttasks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `labors`
--

DROP TABLE IF EXISTS `labors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `labors` (
  `laborId` int NOT NULL AUTO_INCREMENT,
  `laborName` varchar(255) DEFAULT NULL,
  `laborNRC` varchar(255) NOT NULL,
  `laborPhone` varchar(255) DEFAULT NULL,
  `skillId` int DEFAULT NULL,
  `laborStartDate` date DEFAULT NULL,
  `laborEndDate` date DEFAULT NULL,
  `isActive` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`laborId`),
  UNIQUE KEY `laborNRC` (`laborNRC`),
  KEY `fk_labors_skill` (`skillId`),
  CONSTRAINT `fk_labors_skill` FOREIGN KEY (`skillId`) REFERENCES `skills` (`skillId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=211 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `labors`
--

LOCK TABLES `labors` WRITE;
/*!40000 ALTER TABLE `labors` DISABLE KEYS */;
INSERT INTO `labors` VALUES (106,'Aung Kyaw','12/ABC100001','09123456701',1,'2020-01-15',NULL,1),(107,'Min Thu','12/ABC100002','09123456702',1,'2020-03-10',NULL,1),(108,'Hla Myint','12/ABC100003','09123456703',1,'2020-05-20',NULL,1),(109,'Ko Ko','12/ABC100004','09123456704',1,'2020-07-05',NULL,1),(110,'Zaw Win','12/ABC100005','09123456705',1,'2020-08-15',NULL,1),(111,'Myo Min','12/ABC100006','09123456706',1,'2020-09-25',NULL,1),(112,'Tin Htut','12/ABC100007','09123456707',1,'2020-11-10',NULL,1),(113,'Kyaw Swar','12/ABC100008','09123456708',1,'2021-01-15',NULL,1),(114,'Soe Moe','12/ABC100009','09123456709',1,'2021-03-20',NULL,1),(115,'Naing Lin','12/ABC100010','09123456710',1,'2021-05-30',NULL,1),(116,'Aung Myo','12/ABC100011','09123456711',1,'2021-07-15',NULL,1),(117,'Htet Aung','12/ABC100012','09123456712',1,'2021-09-10',NULL,1),(118,'Yan Naing','12/ABC100013','09123456713',1,'2021-11-25',NULL,1),(119,'Moe Kyaw','12/ABC100014','09123456714',1,'2022-02-15',NULL,1),(120,'Thu Rein','12/ABC100015','09123456715',1,'2022-04-10',NULL,1),(121,'Myint Aung','12/DEF200001','09223456701',2,'2019-06-15',NULL,1),(122,'Win Htay','12/DEF200002','09223456702',2,'2019-08-20',NULL,1),(123,'Aung Hla','12/DEF200003','09223456703',2,'2019-10-05',NULL,1),(124,'Kyaw Zin','12/DEF200004','09223456704',2,'2020-01-10',NULL,1),(125,'Hla Shwe','12/DEF200005','09223456705',2,'2020-03-25',NULL,1),(126,'Zaw Htwe','12/DEF200006','09223456706',2,'2020-05-15',NULL,1),(127,'Myo Swe','12/DEF200007','09223456707',2,'2020-07-30',NULL,1),(128,'Tin Maung','12/DEF200008','09223456708',2,'2020-09-10',NULL,1),(129,'Soe Win','12/DEF200009','09223456709',2,'2020-11-25',NULL,1),(130,'Naing Oo','12/DEF200010','09223456710',2,'2021-02-15',NULL,1),(131,'Aung Ko','12/GHI300001','09323456701',3,'2018-04-15',NULL,1),(132,'Ko Zaw','12/GHI300002','09323456702',3,'2018-06-20',NULL,1),(133,'Hla Tun','12/GHI300003','09323456703',3,'2018-08-05',NULL,1),(134,'Win Ko','12/GHI300004','09323456704',3,'2019-01-10',NULL,1),(135,'Kyaw Myint','12/GHI300005','09323456705',3,'2019-03-25',NULL,1),(136,'Myo Zaw','12/GHI300006','09323456706',3,'2019-05-15',NULL,1),(137,'Tin Ko','12/GHI300007','09323456707',3,'2019-07-30',NULL,1),(138,'Soe Naing','12/GHI300008','09323456708',3,'2019-09-10',NULL,1),(139,'Aung Zaw','12/JKL400001','09423456701',4,'2017-03-15',NULL,1),(140,'Ko Myint','12/JKL400002','09423456702',4,'2017-05-20',NULL,1),(141,'Hla Kyaw','12/JKL400003','09423456703',4,'2017-07-05',NULL,1),(142,'Win Htun','12/JKL400004','09423456704',4,'2018-01-10',NULL,1),(143,'Kyaw Hla','12/JKL400005','09423456705',4,'2018-03-25',NULL,1),(144,'Myo Kyaw','12/JKL400006','09423456706',4,'2018-05-15',NULL,1),(145,'Aung Htun','12/MNO500001','09523456701',5,'2017-02-15',NULL,1),(146,'Ko Htay','12/MNO500002','09523456702',5,'2017-04-20',NULL,1),(147,'Hla Myo','12/MNO500003','09523456703',5,'2017-06-05',NULL,1),(148,'Win Zaw','12/MNO500004','09523456704',5,'2018-01-10',NULL,1),(149,'Kyaw Ko','12/MNO500005','09523456705',5,'2018-03-25',NULL,1),(150,'Aung Myint','12/PQR600001','09623456701',6,'2016-05-15',NULL,1),(151,'Ko Kyaw','12/PQR600002','09623456702',6,'2016-07-20',NULL,1),(152,'Hla Zaw','12/PQR600003','09623456703',6,'2016-09-05',NULL,1),(153,'Win Myo','12/PQR600004','09623456704',6,'2017-01-10',NULL,1),(154,'Aung Win','12/STU700001','09723456701',7,'2019-08-15',NULL,1),(155,'Ko Tun','12/STU700002','09723456702',7,'2019-10-20',NULL,1),(156,'Hla Ko','12/STU700003','09723456703',7,'2020-01-05',NULL,1),(157,'Win Hla','12/STU700004','09723456704',7,'2020-03-10',NULL,1),(158,'Aung Tun','12/VWX800001','09823456701',8,'2018-07-15',NULL,1),(159,'Ko Zin','12/VWX800002','09823456702',8,'2018-09-20',NULL,1),(160,'Hla Win','12/VWX800003','09823456703',8,'2019-01-05',NULL,1),(161,'Win Kyaw','12/VWX800004','09823456704',8,'2019-03-10',NULL,1),(162,'Aung Zin','12/YZA900001','09923456701',9,'2015-04-15',NULL,1),(163,'Ko Myo','12/YZA900002','09923456702',9,'2015-06-20',NULL,1),(164,'Hla Min','12/YZA900003','09923456703',9,'2015-08-05',NULL,1),(165,'Win Naing','12/YZA900004','09923456704',9,'2016-01-10',NULL,1),(166,'U Myint','12/BCD100001','09013456701',10,'2010-03-15',NULL,1),(167,'U Kyaw','12/BCD100002','09013456702',10,'2012-05-20',NULL,1),(168,'U Hla','12/BCD100003','09013456703',10,'2014-07-05',NULL,1),(169,'Daw Mya','12/EFG110001','09023456701',11,'2016-02-15',NULL,1),(170,'Daw Khin','12/EFG110002','09023456702',11,'2017-04-20',NULL,1),(171,'Aung Khin','12/HIJ120001','09033456701',12,'2019-03-15',NULL,1),(172,'Ko Thant','12/HIJ120002','09033456702',12,'2019-05-20',NULL,1),(173,'Aung Thant','12/KLM130001','09043456701',13,'2018-06-15',NULL,1),(174,'Ko San','12/KLM130002','09043456702',13,'2018-08-20',NULL,1),(175,'Aung San','12/NOP140001','09053456701',14,'2019-01-15',NULL,1),(176,'Ko Lin','12/NOP140002','09053456702',14,'2019-03-20',NULL,1),(177,'Aung Lin','12/QRS150001','09063456701',15,'2018-04-15',NULL,1),(178,'Ko Htun','12/QRS150002','09063456702',15,'2018-06-20',NULL,1),(179,'Aung Htwe','12/TUV160001','09073456701',16,'2017-05-15',NULL,1),(180,'Ko Shwe','12/TUV160002','09073456702',16,'2017-07-20',NULL,1),(181,'Aung Shwe','12/WXY170001','09083456701',17,'2019-02-15',NULL,1),(182,'Ko Mya','12/WXY170002','09083456702',17,'2019-04-20',NULL,1),(183,'Aung Mya','12/ZAB180001','09093456701',18,'2018-03-15',NULL,1),(184,'Ko Khin','12/ZAB180002','09093456702',18,'2018-05-20',NULL,1),(185,'Aung Khin','12/CDE190001','09103456701',19,'2019-07-15',NULL,1),(186,'Ko Tha','12/CDE190002','09103456702',19,'2019-09-20',NULL,1),(187,'Aung Tha','12/FGH200001','09113456701',20,'2018-08-15',NULL,1),(188,'Ko Nyunt','12/FGH200002','09113456702',20,'2018-10-20',NULL,1),(189,'U Tin','12/IJK210001','09123456801',21,'2010-06-15',NULL,1),(190,'U Ba','12/IJK210002','09123456802',21,'2012-08-20',NULL,1),(191,'Aung Nyunt','12/LMN220001','09133456701',22,'2017-09-15',NULL,1),(192,'Ko Maung','12/LMN220002','09133456702',22,'2017-11-20',NULL,1),(193,'U Than','12/OPQ230001','09143456701',23,'2008-05-15',NULL,1),(194,'U Sein','12/OPQ230002','09143456702',23,'2010-07-20',NULL,1),(195,'Sayadaw U','12/RST240001','09153456701',24,'2005-04-15',NULL,1),(196,'U Paw','12/RST240002','09153456702',24,'2007-06-20',NULL,1),(197,'Former A','12/XXX990001','09163456701',1,'2019-01-15',NULL,0),(198,'Former B','12/XXX990002','09163456702',2,'2019-03-20',NULL,0),(199,'Former C','12/XXX990003','09163456703',3,'2018-05-15',NULL,0),(200,'Former D','12/XXX990004','09163456704',4,'2017-07-30',NULL,0),(201,'Additional 1','12/ADD100001','09173456701',1,'2022-01-15',NULL,1),(202,'Additional 2','12/ADD100002','09173456702',2,'2022-02-20',NULL,1),(203,'Additional 3','12/ADD100003','09173456703',3,'2022-03-15',NULL,1),(204,'Additional 4','12/ADD100004','09173456704',4,'2022-04-10',NULL,1),(205,'Additional 5','12/ADD100005','09173456705',5,'2022-05-05',NULL,1),(206,'Additional 6','12/ADD100006','09173456706',6,'2022-06-20',NULL,1),(207,'Additional 7','12/ADD100007','09173456707',7,'2022-07-15',NULL,1),(208,'Additional 8','12/ADD100008','09173456708',8,'2022-08-10',NULL,1),(209,'Additional 9','12/ADD100009','09173456709',9,'2022-09-05',NULL,1),(210,'Additional 10','12/ADD100010','09173456710',10,'2022-10-20',NULL,1);
/*!40000 ALTER TABLE `labors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `projectdetails`
--

DROP TABLE IF EXISTS `projectdetails`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `projectdetails` (
  `projectDetailId` int NOT NULL AUTO_INCREMENT,
  `projectTypeId` int NOT NULL,
  `projectLevelId` int DEFAULT NULL,
  `projectBuildingId` int DEFAULT NULL,
  `minOverHeadCost` double DEFAULT NULL,
  `maxOverHeadCost` double DEFAULT NULL,
  PRIMARY KEY (`projectDetailId`),
  KEY `fk_pd_projectType` (`projectTypeId`),
  KEY `fk_pd_level` (`projectLevelId`),
  KEY `fk_pd_building` (`projectBuildingId`),
  CONSTRAINT `fk_pd_building` FOREIGN KEY (`projectBuildingId`) REFERENCES `buildings` (`projectBuildingId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_pd_level` FOREIGN KEY (`projectLevelId`) REFERENCES `projectlevels` (`projectLevelId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_pd_projectType` FOREIGN KEY (`projectTypeId`) REFERENCES `projecttypes` (`projectTypeId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `projectdetails`
--

LOCK TABLES `projectdetails` WRITE;
/*!40000 ALTER TABLE `projectdetails` DISABLE KEYS */;
INSERT INTO `projectdetails` VALUES (1,1,2,1,10,15),(2,1,3,1,12,18),(3,1,4,1,15,20),(4,1,3,2,12,18),(5,1,4,2,15,22),(6,1,2,3,8,12),(7,1,2,4,9,13),(8,1,3,4,11,17),(9,2,2,5,10,15),(10,2,3,5,12,18),(11,2,4,5,15,22),(12,2,2,6,8,12),(13,2,3,6,10,16),(14,2,3,7,12,18),(15,2,4,7,15,22),(16,2,3,8,12,18),(17,2,4,8,15,22),(18,3,1,9,8,12),(19,3,2,9,10,15),(20,3,1,10,7,10),(21,3,2,10,9,14),(22,3,1,11,12,18),(23,3,2,11,15,22),(24,4,6,12,5,10),(25,4,7,12,8,12),(26,4,8,12,10,15),(27,4,9,12,12,18),(28,4,7,13,10,18),(29,4,8,13,15,22),(30,4,8,14,18,25),(31,4,9,14,20,30),(32,4,6,15,6,12),(33,4,7,15,10,18),(34,5,6,16,8,12),(35,5,7,16,12,18),(36,5,8,16,15,22),(37,5,7,17,10,16),(38,5,8,17,14,20),(39,5,7,18,12,18),(40,5,8,18,15,22),(41,5,6,19,8,12),(42,5,7,19,10,16);
/*!40000 ALTER TABLE `projectdetails` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `projectlevels`
--

DROP TABLE IF EXISTS `projectlevels`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `projectlevels` (
  `projectLevelId` int NOT NULL AUTO_INCREMENT,
  `projectLevelName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`projectLevelId`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `projectlevels`
--

LOCK TABLES `projectlevels` WRITE;
/*!40000 ALTER TABLE `projectlevels` DISABLE KEYS */;
INSERT INTO `projectlevels` VALUES (1,'Single Floor'),(2,'Low Rise'),(3,'Medium Rise'),(4,'High Rise'),(5,'Extra High Rise'),(6,'Small Scale'),(7,'Medium Scale'),(8,'Large Scale'),(9,'Extra Large'),(10,'Grand Scale');
/*!40000 ALTER TABLE `projectlevels` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `projectstatus`
--

DROP TABLE IF EXISTS `projectstatus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `projectstatus` (
  `projectStatusId` int NOT NULL AUTO_INCREMENT,
  `projectStatusName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`projectStatusId`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `projectstatus`
--

LOCK TABLES `projectstatus` WRITE;
/*!40000 ALTER TABLE `projectstatus` DISABLE KEYS */;
INSERT INTO `projectstatus` VALUES (1,'planning'),(2,'inProgress'),(3,'delay'),(4,'finished'),(5,'cancel');
/*!40000 ALTER TABLE `projectstatus` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `projecttypes`
--

DROP TABLE IF EXISTS `projecttypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `projecttypes` (
  `projectTypeId` int NOT NULL AUTO_INCREMENT,
  `typeName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`projectTypeId`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `projecttypes`
--

LOCK TABLES `projecttypes` WRITE;
/*!40000 ALTER TABLE `projecttypes` DISABLE KEYS */;
INSERT INTO `projecttypes` VALUES (1,'Residential Building'),(2,'Commercial & Institutional Building'),(3,'Industrial Building'),(4,'Infrastructure'),(5,'Religious Building');
/*!40000 ALTER TABLE `projecttypes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `skills`
--

DROP TABLE IF EXISTS `skills`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `skills` (
  `skillId` int NOT NULL AUTO_INCREMENT,
  `skillName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`skillId`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `skills`
--

LOCK TABLES `skills` WRITE;
/*!40000 ALTER TABLE `skills` DISABLE KEYS */;
INSERT INTO `skills` VALUES (1,'General Laborer'),(2,'Mason'),(3,'Carpenter'),(4,'Electrician'),(5,'Plumber'),(6,'Welder'),(7,'Steel Fixer'),(8,'Concrete Finisher'),(9,'Heavy Equipment Operator'),(10,'Foreman/Supervisor'),(11,'Surveyor'),(12,'Scaffolder'),(13,'Tile Setter'),(14,'Painter'),(15,'Plasterer'),(16,'HVAC Technician'),(17,'Glazier'),(18,'Roofer'),(19,'Landscaper'),(20,'Paving Specialist'),(21,'Bridge Specialist'),(22,'Pipe Layer'),(23,'Dam Construction Specialist'),(24,'Religious Art Specialist');
/*!40000 ALTER TABLE `skills` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `taskdetails`
--

DROP TABLE IF EXISTS `taskdetails`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `taskdetails` (
  `taskDetailId` int NOT NULL AUTO_INCREMENT,
  `workItemDetailId` int DEFAULT NULL,
  `projectTaskId` int DEFAULT NULL,
  `minDuration` double DEFAULT NULL,
  `maxDuration` double DEFAULT NULL,
  PRIMARY KEY (`taskDetailId`),
  KEY `fk_td_workItemDetail` (`workItemDetailId`),
  KEY `fk_td_task` (`projectTaskId`),
  CONSTRAINT `fk_td_task` FOREIGN KEY (`projectTaskId`) REFERENCES `tasks` (`projectTaskId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_td_workItemDetail` FOREIGN KEY (`workItemDetailId`) REFERENCES `workitemdetails` (`workItemDetailId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=153 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `taskdetails`
--

LOCK TABLES `taskdetails` WRITE;
/*!40000 ALTER TABLE `taskdetails` DISABLE KEYS */;
INSERT INTO `taskdetails` VALUES (1,1,1,4,5),(2,1,2,7,8),(3,1,3,3,4),(4,1,4,2,2.5),(5,1,5,2,2.5),(6,6,1,4,5),(7,6,2,7,8.5),(8,6,3,3.5,4),(9,6,4,2.5,3),(10,6,5,2,2.5),(11,11,1,3.5,4.3),(12,11,2,6.5,7.5),(13,11,3,3.2,3.7),(14,11,4,2.5,2.9),(15,11,5,2.3,2.6),(16,16,1,4,5),(17,16,2,7,8.5),(18,16,3,3.5,4),(19,16,4,2.5,3),(20,16,5,2,2.5),(21,21,1,4,5.5),(22,21,2,8,9.5),(23,21,3,4,4.5),(24,21,4,3,3.5),(25,21,5,2.5,3),(26,26,1,3,4),(27,26,2,6,7),(28,26,3,2.5,3.5),(29,26,4,1.5,2),(30,26,5,1.5,2),(31,31,1,3,4),(32,31,2,6.5,7.5),(33,31,3,3,3.5),(34,31,4,2,2.5),(35,31,5,1.5,2),(36,36,1,3.5,4.5),(37,36,2,7,8),(38,36,3,3.5,4),(39,36,4,2.5,3),(40,36,5,2,2.5),(41,2,6,10,12),(42,2,7,8,10),(43,2,8,7,8),(44,2,9,8,9),(45,7,6,11,13),(46,7,7,9,11),(47,7,8,8,9),(48,7,9,9,10),(49,12,6,13,15),(50,12,7,10,12),(51,12,8,9,10),(52,12,9,10,11),(53,17,6,11,13),(54,17,7,9,11),(55,17,8,8,9),(56,17,9,9,10),(57,22,6,14,16),(58,22,7,11,13),(59,22,8,9,10),(60,22,9,11,12),(61,27,6,8,10),(62,27,7,6,8),(63,27,8,5,6),(64,27,9,12,14),(65,32,6,9,11),(66,32,7,7,9),(67,32,8,6,7),(68,32,9,10,12),(69,37,6,10,12),(70,37,7,8,10),(71,37,8,7,8),(72,37,9,11,13),(73,3,10,8,10),(74,3,11,6,7),(75,3,12,5,6),(76,3,13,4,5),(77,8,10,9,11),(78,8,11,7,8),(79,8,12,5.5,6.5),(80,8,13,4.5,5.5),(81,13,10,10,12),(82,13,11,8,9),(83,13,12,6,7),(84,13,13,5,6),(85,18,10,9,11),(86,18,11,8,9),(87,18,12,6,7),(88,18,13,5,6),(89,23,10,11,13),(90,23,11,9,10),(91,23,12,7,8),(92,23,13,6,7),(93,28,10,7,9),(94,28,11,8,10),(95,28,12,6,8),(96,28,13,5,7),(97,33,10,7,9),(98,33,11,6,7),(99,33,12,5,6),(100,33,13,4,5),(101,38,10,8,10),(102,38,11,7,8),(103,38,12,5.5,6.5),(104,38,13,4.5,5.5),(105,4,14,5,6),(106,4,15,4,5),(107,4,16,4,5),(108,9,14,6,7),(109,9,15,5,6),(110,9,16,5,6),(111,14,14,7,8),(112,14,15,6,7),(113,14,16,6,7),(114,19,14,6,7),(115,19,15,5,6),(116,19,16,5,6),(117,24,14,7,8),(118,24,15,6,7),(119,24,16,6,7),(120,29,14,4,5),(121,29,15,4,5),(122,29,16,3,4),(123,34,14,5,6),(124,34,15,4,5),(125,34,16,4,5),(126,39,14,5.5,6.5),(127,39,15,4.5,5.5),(128,39,16,4.5,5.5),(129,5,17,1.5,2),(130,5,18,1.5,2),(131,5,19,1,1.5),(132,10,17,1.5,2),(133,10,18,1.5,2),(134,10,19,1,1.5),(135,15,17,1.5,2),(136,15,18,1.5,2),(137,15,19,1,1.5),(138,20,17,1.5,2),(139,20,18,1.5,2),(140,20,19,1,1.5),(141,25,17,1.5,2),(142,25,18,1.5,2),(143,25,19,1,1.5),(144,30,17,2,3),(145,30,18,2,2.5),(146,30,19,1.5,2),(147,35,17,1.5,2),(148,35,18,1.5,2),(149,35,19,1,1.5),(150,40,17,1.5,2),(151,40,18,1.5,2),(152,40,19,1,1.5);
/*!40000 ALTER TABLE `taskdetails` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tasks`
--

DROP TABLE IF EXISTS `tasks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tasks` (
  `projectTaskId` int NOT NULL AUTO_INCREMENT,
  `projectTaskName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`projectTaskId`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tasks`
--

LOCK TABLES `tasks` WRITE;
/*!40000 ALTER TABLE `tasks` DISABLE KEYS */;
INSERT INTO `tasks` VALUES (1,'Excavation'),(2,'Foundation'),(3,'Formwork'),(4,'Reinforcement'),(5,'Concrete Pouring'),(6,'Column Construction'),(7,'Beam Construction'),(8,'Slab Construction'),(9,'Wall Construction'),(10,'Plastering'),(11,'Painting'),(12,'Flooring'),(13,'Tiling'),(14,'Electrical Wiring'),(15,'Plumbing Installation'),(16,'HVAC Installation'),(17,'Landscaping'),(18,'Paving'),(19,'Fencing'),(20,'Site Clearing & Grubbing'),(21,'Earthworks & Grading'),(22,'Subgrade Preparation'),(23,'Base Course Installation'),(24,'Asphalt/Concrete Paving'),(25,'Drainage Installation'),(26,'Bridge Deck Construction'),(27,'Pier/Abutment Construction'),(28,'Pipeline Trenching'),(29,'Pipe Laying & Jointing'),(30,'Backfilling & Compaction'),(31,'Pavement Markings'),(32,'Guardrail Installation'),(33,'Erosion Control'),(34,'Utility Connections');
/*!40000 ALTER TABLE `tasks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `userId` int NOT NULL AUTO_INCREMENT,
  `userName` varchar(255) DEFAULT NULL,
  `userRole` enum('manager','supervisor') DEFAULT NULL,
  `userPhone` varchar(255) DEFAULT NULL,
  `userEmail` varchar(255) DEFAULT NULL,
  `userDOB` date DEFAULT NULL,
  `userPassword` varchar(255) NOT NULL,
  `userPhoto` varchar(255) DEFAULT NULL,
  `userStartDate` date DEFAULT NULL,
  `userEndDate` date DEFAULT NULL,
  `isActive` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'manager','manager','099666','ant@gmail.com','2005-09-27','$2a$12$BMr7.0ZodXQq7n8SxTPfq.vvW4BJBsIcA0jpF/NYLam9UIS2xPOu6','','2026-01-21',NULL,1),(2,'supervisor','supervisor','099666','ant@gmail.com','2005-09-27','$2a$12$R/4YZ2ivHgXVohoeJktLRut.zGt48HElTMj/6AsOo39AjG9l2YA3y','','2026-01-21',NULL,1),(3,'Kyaw Kyaw','supervisor','099666','ant@gmail.com','2005-09-27','$2a$12$Hrol4WQ3QzJFrj6IdwSsxedOfKtqYm8NQFTnXs9B3kEP9MWlF4iMq','','2026-01-21',NULL,1),(4,'Mg Mg','supervisor','099666','ant@gmail.com','2005-09-27','$2a$12$adQYSH6.STwTMDTyCHfTa.YAf/GhsmCwBQ2iCn6HnEkKBPKa7DjRC','','2026-01-21',NULL,1),(5,'Zaw Zaw','supervisor','099666','ant@gmail.com','2005-09-27','$2a$12$EdDMgnYRuGQAM0YXd4iT6.gymOLmlKnQR/M59F.eANy.ixkwMeUOC','','2026-01-21',NULL,1),(6,'Hla Hla','supervisor','099666','ant@gmail.com','2005-09-27','$2a$12$YpX0LfEs9CQPJBCM6hNFmuaUYsg8berDVZR/ecp1Hl1RYLOVyKSlO','','2026-01-21',NULL,1),(7,'Mya Mya','supervisor','099666','ant@gmail.com','2005-09-27','$2a$12$EEclM2xBkM3ao8GeoRJ87.gfDZAs9xrI1cuSXAXlnU3RorydxEPgq','','2026-01-21',NULL,1);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `workitemdetails`
--

DROP TABLE IF EXISTS `workitemdetails`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `workitemdetails` (
  `workItemDetailId` int NOT NULL AUTO_INCREMENT,
  `projectDetailId` int DEFAULT NULL,
  `projectWorkItemId` int NOT NULL,
  `minDuration` double DEFAULT NULL,
  `maxDuration` double DEFAULT NULL,
  `minLabors` double DEFAULT NULL,
  `maxLabors` double DEFAULT NULL,
  `minCost` double DEFAULT NULL,
  `maxCost` double DEFAULT NULL,
  PRIMARY KEY (`workItemDetailId`),
  KEY `fk_wid_projectDetail` (`projectDetailId`),
  KEY `fk_wid_workItemId` (`projectWorkItemId`),
  CONSTRAINT `fk_wid_projectDetail` FOREIGN KEY (`projectDetailId`) REFERENCES `projectdetails` (`projectDetailId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_wid_workItemId` FOREIGN KEY (`projectWorkItemId`) REFERENCES `workitems` (`projectWorkItemId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=206 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `workitemdetails`
--

LOCK TABLES `workitemdetails` WRITE;
/*!40000 ALTER TABLE `workitemdetails` DISABLE KEYS */;
INSERT INTO `workitemdetails` VALUES (1,1,1,18,22,5,10,15,20),(2,1,2,33,38,8,15,30,35),(3,1,3,23,27,5,10,25,30),(4,1,4,13,17,4,8,20,25),(5,1,5,4,6,2,5,5,10),(6,2,1,18,22,6,12,15,20),(7,2,2,33,38,10,18,32,38),(8,2,3,23,27,6,12,26,32),(9,2,4,13,17,5,10,22,28),(10,2,5,4,6,3,6,5,10),(11,3,1,18,22,8,15,15,20),(12,3,2,33,38,12,22,35,42),(13,3,3,23,27,8,15,28,35),(14,3,4,13,17,6,12,25,32),(15,3,5,4,6,4,8,5,10),(16,4,1,18,22,6,12,15,20),(17,4,2,33,38,10,18,33,39),(18,4,3,23,27,6,12,28,34),(19,4,4,13,17,5,10,23,29),(20,4,5,4,6,3,6,5,10),(21,5,1,18,22,8,15,15,20),(22,5,2,33,38,12,22,36,43),(23,5,3,23,27,8,15,30,37),(24,5,4,13,17,6,12,27,34),(25,5,5,4,6,4,8,5,10),(26,6,1,18,22,4,8,15,20),(27,6,2,33,38,6,12,35,42),(28,6,3,23,27,4,8,30,38),(29,6,4,13,17,3,6,15,20),(30,6,5,4,6,2,4,10,15),(31,7,1,18,22,4,8,14,19),(32,7,2,33,38,6,12,33,40),(33,7,3,23,27,4,8,28,35),(34,7,4,13,17,3,6,18,24),(35,7,5,4,6,2,4,8,12),(36,8,1,18,22,5,10,14,19),(37,8,2,33,38,8,14,34,41),(38,8,3,23,27,5,10,29,36),(39,8,4,13,17,4,8,20,26),(40,8,5,4,6,3,6,8,12),(41,9,1,18,22,6,12,12,18),(42,9,2,33,38,10,20,30,38),(43,9,3,23,27,8,16,25,32),(44,9,4,13,17,6,12,25,30),(45,9,5,4,6,3,8,5,10),(46,10,1,18,22,8,15,13,19),(47,10,2,33,38,12,25,32,40),(48,10,3,23,27,10,20,28,35),(49,10,4,13,17,8,16,28,35),(50,10,5,4,6,4,10,5,10),(51,11,1,18,22,10,20,14,21),(52,11,2,33,38,15,30,35,44),(53,11,3,23,27,12,25,30,38),(54,11,4,13,17,10,20,30,38),(55,11,5,4,6,5,12,5,10),(56,12,1,18,22,5,10,10,15),(57,12,2,33,38,8,16,28,35),(58,12,3,23,27,6,12,30,38),(59,12,4,13,17,5,10,20,25),(60,12,5,4,6,3,8,8,12),(61,13,1,18,22,6,12,11,17),(62,13,2,33,38,10,20,30,38),(63,13,3,23,27,8,16,32,40),(64,13,4,13,17,6,12,22,28),(65,13,5,4,6,4,10,8,12),(66,14,1,18,22,8,16,15,22),(67,14,2,33,38,12,24,30,38),(68,14,3,23,27,10,20,25,32),(69,14,4,13,17,10,20,35,45),(70,14,5,4,6,5,12,8,12),(71,15,1,18,22,10,20,16,24),(72,15,2,33,38,15,30,32,40),(73,15,3,23,27,12,25,28,36),(74,15,4,13,17,12,25,38,48),(75,15,5,4,6,6,15,8,12),(76,16,1,18,22,7,14,12,18),(77,16,2,33,38,10,20,28,35),(78,16,3,23,27,12,24,35,45),(79,16,4,13,17,8,16,25,32),(80,16,5,4,6,4,10,10,15),(81,17,1,18,22,9,18,13,20),(82,17,2,33,38,12,25,30,38),(83,17,3,23,27,15,30,38,48),(84,17,4,13,17,10,20,28,36),(85,17,5,4,6,5,12,10,15),(86,19,1,18,22,5,10,10,15),(87,19,2,33,38,8,15,40,50),(88,19,3,23,27,4,8,15,20),(89,19,4,13,17,6,12,25,30),(90,19,5,4,6,2,5,5,10),(91,20,1,18,22,6,12,11,17),(92,20,2,33,38,10,18,42,52),(93,20,3,23,27,5,10,16,22),(94,20,4,13,17,7,14,27,33),(95,20,5,4,6,3,6,5,10),(96,21,1,18,22,4,8,8,12),(97,21,2,33,38,6,12,35,45),(98,21,3,23,27,3,6,10,15),(99,21,4,13,17,4,8,20,25),(100,21,5,4,6,2,4,5,10),(101,22,1,18,22,5,10,9,14),(102,22,2,33,38,8,15,38,48),(103,22,3,23,27,4,8,12,17),(104,22,4,13,17,5,10,22,28),(105,22,5,4,6,3,5,5,10),(106,23,1,18,22,8,15,15,22),(107,23,2,33,38,10,20,30,38),(108,23,3,23,27,5,10,10,15),(109,23,4,13,17,10,20,40,50),(110,23,5,4,6,4,8,8,12),(111,24,1,18,22,10,18,17,24),(112,24,2,33,38,12,24,32,40),(113,24,3,23,27,6,12,12,18),(114,24,4,13,17,12,25,42,53),(115,24,5,4,6,5,10,8,12),(116,25,1,20,25,10,20,15,20),(117,25,2,35,40,15,30,40,50),(118,25,3,25,30,8,15,25,30),(119,25,4,10,15,5,10,15,20),(120,25,5,5,8,3,8,5,10),(121,26,1,20,25,15,25,16,22),(122,26,2,35,40,20,40,42,53),(123,26,3,25,30,10,20,26,32),(124,26,4,10,15,8,15,16,22),(125,26,5,5,8,5,12,5,10),(126,27,1,20,25,20,40,17,24),(127,27,2,35,40,25,50,45,56),(128,27,3,25,30,15,30,28,35),(129,27,4,10,15,10,20,18,24),(130,27,5,5,8,8,15,5,10),(131,28,1,20,25,25,50,18,26),(132,28,2,35,40,30,60,48,60),(133,28,3,25,30,20,40,30,38),(134,28,4,10,15,12,25,20,26),(135,28,5,5,8,10,20,5,10),(136,29,1,25,30,20,40,25,35),(137,29,2,40,45,25,50,45,55),(138,29,3,20,25,15,30,15,20),(139,29,4,10,15,10,20,10,15),(140,29,5,3,5,5,10,5,10),(141,30,1,25,30,25,50,28,38),(142,30,2,40,45,30,60,48,58),(143,30,3,20,25,20,40,17,23),(144,30,4,10,15,15,30,12,17),(145,30,5,3,5,8,15,5,10),(146,31,1,30,35,50,100,40,50),(147,31,2,40,45,60,120,35,45),(148,31,3,15,20,20,40,10,15),(149,31,4,8,12,30,60,10,15),(150,31,5,5,8,15,30,5,10),(151,32,1,30,35,60,120,42,53),(152,32,2,40,45,70,140,38,48),(153,32,3,15,20,25,50,12,17),(154,32,4,8,12,35,70,12,17),(155,32,5,5,8,20,40,5,10),(156,33,1,25,30,10,20,30,40),(157,33,2,35,40,8,15,40,50),(158,33,3,20,25,5,10,15,20),(159,33,4,12,15,6,12,10,15),(160,33,5,5,8,3,6,5,10),(161,34,1,25,30,15,30,32,42),(162,34,2,35,40,12,25,42,52),(163,34,3,20,25,8,15,17,22),(164,34,4,12,15,8,16,12,17),(165,34,5,5,8,5,10,5,10),(166,35,1,18,22,5,10,12,18),(167,35,2,33,38,8,15,35,45),(168,35,3,23,27,6,12,30,40),(169,35,4,13,17,4,8,18,25),(170,35,5,4,6,3,6,10,15),(171,36,1,18,22,6,12,14,20),(172,36,2,33,38,10,20,38,48),(173,36,3,23,27,8,16,33,43),(174,36,4,13,17,5,10,20,28),(175,36,5,4,6,4,8,10,15),(176,37,1,18,22,8,15,16,23),(177,37,2,33,38,12,25,42,52),(178,37,3,23,27,10,20,36,46),(179,37,4,13,17,6,12,22,30),(180,37,5,4,6,5,10,10,15),(181,38,1,18,22,6,12,13,19),(182,38,2,33,38,10,20,36,46),(183,38,3,23,27,8,16,32,42),(184,38,4,13,17,5,10,19,26),(185,38,5,4,6,4,8,10,15),(186,39,1,18,22,8,15,15,22),(187,39,2,33,38,12,25,40,50),(188,39,3,23,27,10,20,35,45),(189,39,4,13,17,6,12,21,29),(190,39,5,4,6,5,10,10,15),(191,40,1,18,22,7,14,14,20),(192,40,2,33,38,10,20,38,48),(193,40,3,23,27,9,18,34,44),(194,40,4,13,17,5,10,18,25),(195,40,5,4,6,4,8,12,18),(196,41,1,18,22,9,18,16,24),(197,41,2,33,38,12,25,42,52),(198,41,3,23,27,11,22,38,48),(199,41,4,13,17,6,12,20,28),(200,41,5,4,6,5,10,12,18),(201,42,1,18,22,5,10,12,18),(202,42,2,33,38,8,15,32,42),(203,42,3,23,27,6,12,28,38),(204,42,4,13,17,4,8,16,22),(205,42,5,4,6,3,6,15,20);
/*!40000 ALTER TABLE `workitemdetails` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `workitemrequireskills`
--

DROP TABLE IF EXISTS `workitemrequireskills`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `workitemrequireskills` (
  `workItemRequireSkillId` int NOT NULL AUTO_INCREMENT,
  `workItemDetailId` int DEFAULT NULL,
  `skillId` int DEFAULT NULL,
  `minRequireLabors` double DEFAULT NULL,
  `maxRequireLabors` double DEFAULT NULL,
  `minDailyWage` double DEFAULT NULL,
  `maxDailyWage` double DEFAULT NULL,
  PRIMARY KEY (`workItemRequireSkillId`),
  KEY `fk_wirs_taskDetail` (`workItemDetailId`),
  KEY `fk_wirs_skill` (`skillId`),
  CONSTRAINT `fk_wirs_skill` FOREIGN KEY (`skillId`) REFERENCES `skills` (`skillId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_wirs_taskDetail` FOREIGN KEY (`workItemDetailId`) REFERENCES `workitemdetails` (`workItemDetailId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `workitemrequireskills`
--

LOCK TABLES `workitemrequireskills` WRITE;
/*!40000 ALTER TABLE `workitemrequireskills` DISABLE KEYS */;
/*!40000 ALTER TABLE `workitemrequireskills` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `workitems`
--

DROP TABLE IF EXISTS `workitems`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `workitems` (
  `projectWorkItemId` int NOT NULL AUTO_INCREMENT,
  `projectWorkItemName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`projectWorkItemId`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `workitems`
--

LOCK TABLES `workitems` WRITE;
/*!40000 ALTER TABLE `workitems` DISABLE KEYS */;
INSERT INTO `workitems` VALUES (1,'Substructure'),(2,'Superstructure'),(3,'Finishing'),(4,'MEP'),(5,'External');
/*!40000 ALTER TABLE `workitems` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'ippsystem'
--
/*!50003 DROP PROCEDURE IF EXISTS `addSkillToWorkItem` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `addSkillToWorkItem`(
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
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `addTaskDetailRecord` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `addTaskDetailRecord`(
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
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `assignProjects` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `assignProjects`(
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
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `assignTaskToWorkItem` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `assignTaskToWorkItem`(
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
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `assignWorkItems` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `assignWorkItems`(
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
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getAllLabors` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllLabors`()
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
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getAllLaborsByProjectId` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllLaborsByProjectId`(IN p_assignProjectId INT)
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
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getAllProjects` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllProjects`()
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
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getAllSkillDetailsByAssignWorkItem` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllSkillDetailsByAssignWorkItem`(
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
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getAllTasksByAssignWorkItem` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllTasksByAssignWorkItem`(
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
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getAllTasksDetailsByWorkItem` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllTasksDetailsByWorkItem`(
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
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getAllWorkItemByAssignProjectId` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllWorkItemByAssignProjectId`(
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
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getAllWorkItemDetails` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllWorkItemDetails`(
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
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getBuildingNameByProjectId` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `getBuildingNameByProjectId`(IN p_assignProjectId INT)
BEGIN
    SELECT
        b.projectBuildingId,
        b.projectBuildingName
    FROM assignProjects ap
    INNER JOIN buildings b
        ON b.projectBuildingId = ap.projectBuildingId
    WHERE ap.assignProjectId = p_assignProjectId;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getLevelByProjectId` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `getLevelByProjectId`(IN p_assignProjectId INT)
BEGIN
    SELECT
        pl.projectLevelId,
        pl.projectLevelName
    FROM assignProjects ap
    INNER JOIN projectLevels pl
        ON pl.projectLevelId = ap.projectLevelId
    WHERE ap.assignProjectId = p_assignProjectId;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getProjectDetails` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `getProjectDetails`(
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
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `getSkillByWorkItem` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `getSkillByWorkItem`(
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
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `updateAssignProject` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `updateAssignProject`(
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
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-21 23:21:53
