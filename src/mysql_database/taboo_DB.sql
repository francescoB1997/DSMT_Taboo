-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: localhost    Database: taboo
-- ------------------------------------------------------
-- Server version	8.0.36

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

--
-- Table structure for table `friendship`
--
DROP DATABASE IF EXISTS taboo;
CREATE DATABASE taboo;
USE taboo;

DROP TABLE IF EXISTS `friendship`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `friendship` (
  `Username1` varchar(45) NOT NULL,
  `Username2` varchar(45) NOT NULL,
  PRIMARY KEY (`Username1`,`Username2`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `friendship`
--

LOCK TABLES `friendship` WRITE;
/*!40000 ALTER TABLE `friendship` DISABLE KEYS */;
INSERT INTO `friendship` VALUES ('alex_cook','ella_miller'),('alex_cook','mia_brown'),('cia','fra'),('cia','gsf'),('cia2','cia'),('cia2','fra'),('cia2','gsf'),('cia2','gsf2'),('ella_miller','luke_smith'),('emma_taylor','owen_hall'),('emma_taylor','sophie_davis'),('fra','ella_miller'),('fra','emma_taylor'),('fra','luke_smith'),('fra','max_wilson'),('fra','mia_brown'),('fra','noah_carter'),('fra','owen_hall'),('fra','sophie_davis'),('fra2','cia2'),('fra2','fra'),('fra2','gsf'),('fra2','gsf2'),('g.allevi100','fra'),('g.allevi100','gsf'),('g.allevi13','amy_carter'),('g.allevi13','david_moore'),('g.allevi13','ella_miller'),('g.allevi13','emma_taylor'),('g.allevi13','fra'),('g.allevi13','g.allevi100'),('g.allevi13','grace_clark'),('g.allevi13','gsf'),('g.allevi13','jack_robinson'),('g.allevi13','mia_brown'),('g.allevi13','noah_carter'),('gsf','amy_carter'),('gsf','david_moore'),('gsf','emma_taylor'),('gsf','fra'),('gsf','grace_clark'),('gsf','zoe_jenkins'),('gsf2','cia'),('gsf2','fra'),('gsf2','g.allevi100'),('gsf2','g.allevi13'),('gsf2','gsf'),('jackson84','max_wilson'),('jackson84','sarah_johnson'),('max_wilson','emma_taylor'),('max_wilson','owen_hall'),('owen_hall','alex_cook'),('owen_hall','ella_miller'),('owen_hall','sarah_johnson'),('sarah_johnson','alex_cook'),('sarah_johnson','david_moore'),('sarah_johnson','jackson84'),('sophie_davis','david_moore'),('user123','jackson84'),('user456','lily22');
/*!40000 ALTER TABLE `friendship` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `match`
--

DROP TABLE IF EXISTS `match`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `match` (
  `idMatch` int NOT NULL AUTO_INCREMENT,
  `Team1` varchar(200) DEFAULT NULL,
  `Team2` varchar(200) DEFAULT NULL,
  `ScoreTeam1` int DEFAULT NULL,
  `ScoreTeam2` int DEFAULT NULL,
  `Timestamp` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`idMatch`),
  UNIQUE KEY `Timestamp` (`Timestamp`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `match`
--

LOCK TABLES `match` WRITE;
/*!40000 ALTER TABLE `match` DISABLE KEYS */;
INSERT INTO `match` VALUES (7,'[fra, fra2]','[gsf, gsf2]',1,0,'2024-04-14 10:01:25.61'),(8,'[fra, fra2]','[gsf, gsf2]',0,2,'2024-04-14 10:19:04.038'),(11,'[fra, fra2]','[gsf, gsf2]',0,0,'2024-04-14 10:53:29.059'),(12,'[fra, fra2]','[gsf, gsf2]',1,0,'2024-04-14 10:55:25.321'),(13,'[fra, fra2]','[gsf, gsf2]',0,2,'2024-04-14 11:01:02.786'),(15,'[fra, fra2]','[gsf, gsf2]',0,1,'2024-04-14 11:14:16.511'),(18,'[fra, gsf]','[fra2, gsf2]',1,0,'2024-04-14 11:15:23.603'),(19,'[fra, gsf]','[fra2, gsf2]',0,1,'2024-04-14 11:29:24.884'),(21,'[fra, gsf]','[fra2, gsf2]',1,0,'2024-04-14 11:32:56.659'),(24,'[gsf, fra]','[fra2, gsf2]',0,0,'2024-04-14 11:34:20.755'),(25,'[fra2, gsf]','[fra, gsf2]',0,0,'2024-04-14 11:35:26.569'),(27,'[fra, gsf]','[fra2, gsf2]',0,0,'2024-04-14 11:36:24.679'),(29,'[fra2, gsf]','[fra, gsf2]',0,0,'2024-04-14 11:37:25.629'),(31,'[fra, gsf, cia2]','[gsf2, cia, fra2]',0,0,'2024-04-14 11:43:48.608'),(33,'[fra, cia, cia2]','[gsf, fra2, gsf2]',0,1,'2024-04-14 11:50:55.115'),(36,'[fra, gsf, cia]','[fra2, cia2, gsf2]',2,0,'2024-04-14 11:53:09.355'),(37,'[fra, fra2, gsf2]','[cia2, gsf, cia]',0,0,'2024-04-14 12:02:37.155'),(40,'[fra, gsf, cia]','[fra2, gsf2, cia2]',1,0,'2024-04-14 12:22:08.928'),(41,'[fra2, fra, gsf]','[cia2, gsf2, cia]',1,1,'2024-04-14 12:24:14.673'),(44,'[cia2, gsf2, fra2]','[gsf, fra, cia]',2,8,'2024-04-14 12:32:05.703'),(45,'[gsf, fra]','[emma_taylor, sophie_davis]',9,5,'2024-04-15 11:43:31.752'),(47,'[gsf, fra]','[emma_taylor, sophie_davis]',10,6,'2024-04-15 11:50:48.942'),(49,'[gsf, fra]','[emma_taylor, sophie_davis]',11,6,'2024-04-15 11:57:03.254'),(51,'[gsf, emma_taylor]','[fra, sophie_davis]',6,10,'2024-04-15 12:05:05.614');
/*!40000 ALTER TABLE `match` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `Username` varchar(45) NOT NULL,
  `Name` varchar(45) NOT NULL,
  `Surname` varchar(45) NOT NULL,
  `Password` varchar(100) NOT NULL,
  PRIMARY KEY (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('admin','administrator','administrator','admin'),('alex_cook','Alex','Cook','OceanWave456'),('amy_carter','Amy','Carter','Garden789'),('cia','ciaone','ciaonissimo','c'),('cia2','ciao','salve','c'),('david_moore','David','Moore','Moonlight789'),('ella_miller','Ella','Miller','SweetDreams123'),('emma_taylor','Emma','Taylor','Butterfly123'),('fra','Francesco','Bruno','f'),('fra2','francesco','brunos','f'),('g.allevi100','Giovanni','Allevi','g'),('g.allevi13','Giovanni','Allevi','g'),('grace_clark','Grace','Clark','PurpleRain123'),('gsf','Gaetano','Sferrazza','g'),('gsf2','gaetano','sferrazza','g'),('jack_robinson','Jack','Robinson','CoffeeTime456'),('jackson84','Jackson','Smith','Secure123!'),('lily22','Lily','Williams','Rainbow456'),('luke_smith','Luke','Smith','GoldenSun456'),('max_wilson','Max','Wilson','BlueSky123'),('mia_brown','Mia','Brown','HappyDay123'),('mike_jones','Mike','Jones','Summer2023!'),('noah_carter','Noah','Carter','SunnyDay789'),('olivia_white','Olivia','White','HappyPlace456'),('owen_hall','Owen','Hall','Starlight456'),('sophie_davis','Sophie','Davis','Sunflower456'),('user1','John','Doe','pass123'),('user10','Emma','Anderson','access'),('user123','Sarah','Johnson','P@ssw0rd123'),('user2','Alice','Smith','qwerty'),('user3','Bob','Johnson','secret'),('user4','Eva','Williams','mypassword'),('user5','Charlie','Brown','letmein'),('user6','Olivia','Davis','p@ssw0rd'),('user7','Michael','Miller','secure123'),('user8','Sophia','Moore','password123'),('user9','Liam','Taylor','123456'),('zoe_jenkins','Zoe','Jenkins','MountainView123');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'taboo'
--
/*!50003 DROP FUNCTION IF EXISTS `alreadyFriend` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `alreadyFriend`(username VARCHAR(45), usernameFriend VARCHAR(45)) RETURNS tinyint(1)
    DETERMINISTIC
BEGIN
    DECLARE alreadyFriend_ INT;

	SELECT Count(*) INTO alreadyFriend_
	FROM taboo.friendship as F
	WHERE ( (Username1 = username) AND (Username2 = usernameFriend)
					OR
                    (Username1 =usernameFriend ) AND (Username2 = username));

    RETURN alreadyFriend_ > 0;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP FUNCTION IF EXISTS `userExists` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `userExists`(username VARCHAR(45)) RETURNS tinyint(1)
    DETERMINISTIC
BEGIN
    DECLARE exists_ INT;

    SELECT COUNT(*) INTO exists_
    FROM user as U
    WHERE U.username = username;

    RETURN exists_ > 0;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-04-17 11:50:12
