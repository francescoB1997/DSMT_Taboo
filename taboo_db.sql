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
INSERT INTO `friendship` VALUES ('alex_cook','ella_miller'),('alex_cook','mia_brown'),('cia','fra'),('cia','gsf'),('cia2','fra'),('cia2','gsf'),('ella_miller','luke_smith'),('emma_taylor','owen_hall'),('fra','ella_miller'),('fra','emma_taylor'),('fra','luke_smith'),('fra','max_wilson'),('fra','mia_brown'),('fra','noah_carter'),('fra','owen_hall'),('fra2','fra'),('fra2','gsf'),('g.allevi100','fra'),('g.allevi100','gsf'),('g.allevi13','amy_carter'),('g.allevi13','david_moore'),('g.allevi13','ella_miller'),('g.allevi13','emma_taylor'),('g.allevi13','fra'),('g.allevi13','g.allevi100'),('g.allevi13','grace_clark'),('g.allevi13','gsf'),('g.allevi13','jack_robinson'),('g.allevi13','mia_brown'),('g.allevi13','noah_carter'),('gsf','amy_carter'),('gsf','david_moore'),('gsf','emma_taylor'),('gsf','fra'),('gsf','grace_clark'),('gsf','zoe_jenkins'),('gsf2','fra'),('gsf2','g.allevi100'),('gsf2','g.allevi13'),('gsf2','gsf'),('jackson84','max_wilson'),('jackson84','sarah_johnson'),('max_wilson','emma_taylor'),('max_wilson','owen_hall'),('owen_hall','alex_cook'),('owen_hall','ella_miller'),('owen_hall','sarah_johnson'),('sarah_johnson','alex_cook'),('sarah_johnson','david_moore'),('sarah_johnson','jackson84'),('sophie_davis','david_moore'),('user123','jackson84'),('user456','lily22');
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
  `Timestamp` varchar(50) UNIQUE DEFAULT NULL,
  PRIMARY KEY (`idMatch`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `match`
--

LOCK TABLES `match` WRITE;
/*!40000 ALTER TABLE `match` DISABLE KEYS */;
INSERT INTO `match` VALUES (1,'[fra, fra2]','[gsf, cia]',0,0,'2024-04-05 12:02:24'),(2,'[fra, fra2]','[gsf, cia]',0,0,'2024-04-05 12:02:24'),(3,'[fra, fra2]','[gsf, cia]',0,0,'2024-04-05 12:12:00'),(4,'[fra, fra2]','[gsf, cia]',0,0,'2024-04-05 12:12:00'),(5,'[fra, fra2]','[gsf, cia]',0,0,'2024-04-05 12:12:00');
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
INSERT INTO `user` VALUES ('alex_cook','Alex','Cook','OceanWave456'),('amy_carter','Amy','Carter','Garden789'),('cia','ciaone','ciaonissimo','c'),('cia2','ciao','salve','c'),('david_moore','David','Moore','Moonlight789'),('ella_miller','Ella','Miller','SweetDreams123'),('emma_taylor','Emma','Taylor','Butterfly123'),('fra','Francesco','Bruno','f'),('fra2','francesco','brunos','f'),('g.allevi100','Giovanni','Allevi','g'),('g.allevi13','Giovanni','Allevi','g'),('grace_clark','Grace','Clark','PurpleRain123'),('gsf','Gaetano','Sferrazza','g'),('gsf2','gaetano','sferrazza','g'),('jack_robinson','Jack','Robinson','CoffeeTime456'),('jackson84','Jackson','Smith','Secure123!'),('lily22','Lily','Williams','Rainbow456'),('luke_smith','Luke','Smith','GoldenSun456'),('max_wilson','Max','Wilson','BlueSky123'),('mia_brown','Mia','Brown','HappyDay123'),('mike_jones','Mike','Jones','Summer2023!'),('noah_carter','Noah','Carter','SunnyDay789'),('olivia_white','Olivia','White','HappyPlace456'),('owen_hall','Owen','Hall','Starlight456'),('sophie_davis','Sophie','Davis','Sunflower456'),('user1','John','Doe','pass123'),('user10','Emma','Anderson','access'),('user123','Sarah','Johnson','P@ssw0rd123'),('user2','Alice','Smith','qwerty'),('user3','Bob','Johnson','secret'),('user4','Eva','Williams','mypassword'),('user5','Charlie','Brown','letmein'),('user6','Olivia','Davis','p@ssw0rd'),('user7','Michael','Miller','secure123'),('user8','Sophia','Moore','password123'),('user9','Liam','Taylor','123456'),('zoe_jenkins','Zoe','Jenkins','MountainView123');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'taboo'
--
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

-- Dump completed on 2024-04-05 12:19:35
