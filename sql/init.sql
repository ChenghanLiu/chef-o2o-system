-- MySQL dump 10.13  Distrib 8.0.44, for Linux (aarch64)
--
-- Host: localhost    Database: chef_o2o
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `accounts`
--
CREATE DATABASE IF NOT EXISTS `chef_o2o` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `chef_o2o`;

-- MySQL dump 10.13  Distrib 8.0.44, for Linux (aarch64)
--
-- Host: localhost    Database: chef_o2o

DROP TABLE IF EXISTS `accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `accounts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `password_hash` varchar(100) NOT NULL,
  `role` varchar(20) NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_accounts_username` (`username`),
  UNIQUE KEY `uk_accounts_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `accounts`
--

LOCK TABLES `accounts` WRITE;
/*!40000 ALTER TABLE `accounts` DISABLE KEYS */;
INSERT INTO `accounts` VALUES (1,'user18','13821000019','$2a$10$GPYBr23dM/J2F4KFtmy7beM6Z.MiUi6odJfwfzbOTwrelhGm8C6UO','USER','ACTIVE','2026-02-25 02:27:27','2026-02-25 23:54:38'),(2,'chef1','13821000119','$2a$10$U6M1VWmoza4kAyYbPBcus.wOQK0G.tMOT.91o.9x9EMUPsVjF0EEG','USER','ACTIVE','2026-02-25 02:28:34','2026-02-25 02:28:34'),(3,'chef2','13851000119','$2a$10$jBB8YDehCH9yfRZZhwHFh.zSnJvvgEumJP.QevmnbWWBxttE89J9e','CHEF','ACTIVE','2026-02-25 02:29:22','2026-02-25 02:29:22'),(4,'admin1','13851002119','$2a$10$uN6rJe1Xk6/MApvFNrE5bOCm1tSmIyPrXjjJciS6454xdlIflHvem','ADMIN','ACTIVE','2026-02-25 02:49:50','2026-02-25 02:49:50'),(5,'admin','13900000000','$2a$10$nlmCh0qWAxH0FZiPEUIrwec3.3FdtPPV7umgTNj5w9v6Mo7u1o9NK','ADMIN','ACTIVE','2026-02-25 13:03:27','2026-02-25 13:03:27'),(6,'user_demo','13800000001','$2a$10$nlmCh0qWAxH0FZiPEUIrwec3.3FdtPPV7umgTNj5w9v6Mo7u1o9NK','USER','ACTIVE','2026-02-25 13:03:27','2026-03-01 10:41:24'),(7,'chef_demo','13800000002','$2a$10$nlmCh0qWAxH0FZiPEUIrwec3.3FdtPPV7umgTNj5w9v6Mo7u1o9NK','CHEF','ACTIVE','2026-02-25 13:03:27','2026-02-25 13:03:27'),(8,'user16','12345677890','$2a$10$kKFFgErBWFiPbfqOhcDwZOzEQpqaKu.KVLfEeLdQUzX.ubP9S9dHy','USER','ACTIVE','2026-02-25 23:22:21','2026-02-25 23:22:21'),(9,'chef16','123','$2a$10$go7YqQalr8ws9P09p8kyNuE.2YrqbyYvsCrfSrfExKl.Y3DhiFtTC','CHEF','ACTIVE','2026-02-25 23:28:16','2026-02-25 23:28:16');
/*!40000 ALTER TABLE `accounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chef_cuisines`
--

DROP TABLE IF EXISTS `chef_cuisines`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chef_cuisines` (
  `chef_id` bigint NOT NULL,
  `cuisine_id` bigint NOT NULL,
  PRIMARY KEY (`chef_id`,`cuisine_id`),
  KEY `fk_chef_cuisines_cuisine` (`cuisine_id`),
  CONSTRAINT `fk_chef_cuisines_chef` FOREIGN KEY (`chef_id`) REFERENCES `chef_profiles` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_chef_cuisines_cuisine` FOREIGN KEY (`cuisine_id`) REFERENCES `cuisine_categories` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chef_cuisines`
--

LOCK TABLES `chef_cuisines` WRITE;
/*!40000 ALTER TABLE `chef_cuisines` DISABLE KEYS */;
INSERT INTO `chef_cuisines` VALUES (1,1),(2,1),(3,1),(1,2),(2,2),(3,2),(1,3),(2,3),(3,3),(1,8);
/*!40000 ALTER TABLE `chef_cuisines` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chef_profiles`
--

DROP TABLE IF EXISTS `chef_profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chef_profiles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account_id` bigint NOT NULL,
  `avatar_url` varchar(255) DEFAULT NULL,
  `bio` varchar(500) DEFAULT NULL,
  `service_area` varchar(100) DEFAULT NULL,
  `work_time_desc` varchar(100) DEFAULT NULL,
  `years_experience` int DEFAULT NULL,
  `base_price_cents` int DEFAULT NULL,
  `avg_rating` decimal(3,2) NOT NULL DEFAULT '0.00',
  `total_orders` int NOT NULL DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` varchar(20) NOT NULL DEFAULT 'PENDING',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_chef_profiles_account` (`account_id`),
  CONSTRAINT `fk_chef_profiles_account` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chef_profiles`
--

LOCK TABLES `chef_profiles` WRITE;
/*!40000 ALTER TABLE `chef_profiles` DISABLE KEYS */;
INSERT INTO `chef_profiles` VALUES (1,3,NULL,NULL,NULL,NULL,NULL,NULL,5.00,1,'2026-02-25 02:29:22','2026-03-02 00:26:54','APPROVED'),(2,7,'https://example.com/avatar/chef_demo.png','Home chef. Good at family meals & light banquets.','Downtown / 5km radius','Mon-Sun 10:00-20:00',5,3000,5.00,1,'2026-02-25 13:03:27','2026-02-25 13:03:27','APPROVED'),(3,9,NULL,NULL,NULL,NULL,NULL,NULL,0.00,0,'2026-02-25 23:28:16','2026-02-26 02:01:53','APPROVED');
/*!40000 ALTER TABLE `chef_profiles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `circle_comments`
--

DROP TABLE IF EXISTS `circle_comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `circle_comments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `post_id` bigint NOT NULL,
  `author_account_id` bigint NOT NULL,
  `content` varchar(800) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PUBLISHED',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_circle_comments_post` (`post_id`),
  KEY `idx_circle_comments_author` (`author_account_id`),
  KEY `idx_circle_comments_status` (`status`),
  KEY `idx_circle_comments_created_at` (`created_at`),
  CONSTRAINT `fk_circle_comments_author` FOREIGN KEY (`author_account_id`) REFERENCES `accounts` (`id`),
  CONSTRAINT `fk_circle_comments_post` FOREIGN KEY (`post_id`) REFERENCES `circle_posts` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `circle_comments`
--

LOCK TABLES `circle_comments` WRITE;
/*!40000 ALTER TABLE `circle_comments` DISABLE KEYS */;
INSERT INTO `circle_comments` VALUES (1,1,1,'Nice post!','PUBLISHED','2026-02-25 11:31:58'),(2,3,7,'Hi! I can help. Check my profile and service items :)','PUBLISHED','2026-02-25 13:03:31'),(3,1,3,'Nice post!','PUBLISHED','2026-03-01 12:52:02'),(4,6,1,'好','PUBLISHED','2026-03-02 00:54:49'),(5,5,1,'测试评论1','PUBLISHED','2026-03-02 00:55:10');
/*!40000 ALTER TABLE `circle_comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `circle_posts`
--

DROP TABLE IF EXISTS `circle_posts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `circle_posts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `author_account_id` bigint NOT NULL,
  `title` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PUBLISHED',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_circle_posts_author` (`author_account_id`),
  KEY `idx_circle_posts_status` (`status`),
  KEY `idx_circle_posts_created_at` (`created_at`),
  CONSTRAINT `fk_circle_posts_author` FOREIGN KEY (`author_account_id`) REFERENCES `accounts` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `circle_posts`
--

LOCK TABLES `circle_posts` WRITE;
/*!40000 ALTER TABLE `circle_posts` DISABLE KEYS */;
INSERT INTO `circle_posts` VALUES (1,1,'Hello circle','First post content.','HIDDEN','2026-02-25 11:30:00','2026-03-01 19:19:50'),(2,1,'Hello circle','First post content.','HIDDEN','2026-02-25 11:36:38','2026-03-02 00:51:43'),(3,6,'First post','Anyone recommend a good home chef?','HIDDEN','2026-02-25 13:03:27','2026-03-02 00:51:45'),(4,8,'Hello circle','First post content.','HIDDEN','2026-03-01 12:50:58','2026-03-02 00:51:47'),(5,8,'第一条帖子','内容1','PUBLISHED','2026-03-02 00:53:38','2026-03-02 00:53:38'),(6,8,'第二条帖子','2','PUBLISHED','2026-03-02 00:53:53','2026-03-02 00:53:53');
/*!40000 ALTER TABLE `circle_posts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cuisine_categories`
--

DROP TABLE IF EXISTS `cuisine_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuisine_categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `sort_order` int NOT NULL DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_cuisine_categories_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cuisine_categories`
--

LOCK TABLES `cuisine_categories` WRITE;
/*!40000 ALTER TABLE `cuisine_categories` DISABLE KEYS */;
INSERT INTO `cuisine_categories` VALUES (1,'淮扬菜',3,'2026-02-25 09:25:49'),(2,'杭帮菜',4,'2026-02-25 09:25:49'),(3,'美式快餐',5,'2026-02-25 09:25:49'),(4,'川菜',1,'2026-02-25 09:25:49'),(8,'日料',6,'2026-02-25 13:03:27'),(10,'粤菜',2,'2026-03-01 19:42:45');
/*!40000 ALTER TABLE `cuisine_categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `script` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flyway_schema_history`
--

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` VALUES (1,'1','init chef o2o','SQL','V1__init_chef_o2o.sql',-4856371,'root','2026-02-25 09:25:50',223,1),(2,'2','add status to chef profiles','SQL','V2__add_status_to_chef_profiles.sql',604357169,'root','2026-02-25 09:25:50',30,1),(3,'3','create orders','SQL','V3__create_orders.sql',1142308224,'root','2026-02-25 09:25:50',308,1),(4,'4','create circle posts and comments','SQL','V4__create_circle_posts_and_comments.sql',-570014464,'root','2026-02-25 11:18:04',176,1),(5,'5','create news','SQL','V5__create_news.sql',488731256,'root','2026-02-25 12:17:30',89,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `news`
--

DROP TABLE IF EXISTS `news`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `news` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PUBLISHED',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_news_status` (`status`),
  KEY `idx_news_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `news`
--

LOCK TABLES `news` WRITE;
/*!40000 ALTER TABLE `news` DISABLE KEYS */;
INSERT INTO `news` VALUES (1,'新闻2','新闻2','PUBLISHED','2026-02-25 12:24:37','2026-03-01 23:06:35'),(2,'欢迎来到舌尖到家','新闻1','PUBLISHED','2026-02-25 13:03:27','2026-03-01 23:06:13');
/*!40000 ALTER TABLE `news` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_account_id` bigint NOT NULL,
  `chef_id` bigint NOT NULL,
  `service_item_id` bigint NOT NULL,
  `scheduled_at` datetime NOT NULL,
  `address` varchar(200) NOT NULL,
  `note` varchar(300) DEFAULT NULL,
  `status` varchar(30) NOT NULL,
  `total_price_cents` int NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_orders_service` (`service_item_id`),
  KEY `idx_orders_user` (`user_account_id`),
  KEY `idx_orders_chef` (`chef_id`),
  KEY `idx_orders_status` (`status`),
  CONSTRAINT `fk_orders_chef` FOREIGN KEY (`chef_id`) REFERENCES `chef_profiles` (`id`),
  CONSTRAINT `fk_orders_service` FOREIGN KEY (`service_item_id`) REFERENCES `service_items` (`id`),
  CONSTRAINT `fk_orders_user` FOREIGN KEY (`user_account_id`) REFERENCES `accounts` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,1,1,1,'2026-02-26 04:00:00','Test address','Test order','COMPLETED',19900,'2026-02-25 02:52:21','2026-02-25 02:55:08'),(2,6,2,2,'2026-02-26 21:03:27','Demo Address 1, Building A, Room 1001','No spicy, please.','COMPLETED',6800,'2026-02-25 13:03:27','2026-02-25 13:03:27'),(3,6,2,2,'2026-02-27 21:03:27','Demo Address 2, Building B, Room 2002','Bring less oil.','PENDING',6800,'2026-02-25 13:03:27','2026-02-25 13:03:27'),(4,1,1,1,'2026-03-01 10:00:00','Some address','Optional','COMPLETED',19900,'2026-02-25 09:14:32','2026-03-01 10:43:45'),(5,1,1,1,'2026-02-01 16:00:00','123','123','COMPLETED',19900,'2026-02-25 09:19:30','2026-03-02 00:28:06'),(6,8,1,1,'2027-03-01 10:00:00','Some address','Optional','COMPLETED',19900,'2026-02-26 00:33:58','2026-02-26 02:14:08'),(7,8,1,1,'2026-03-10 16:00:00','123','','PENDING',26900,'2026-03-02 00:14:21','2026-03-02 00:14:21'),(8,8,1,1,'2026-04-14 16:00:00','123','123','PENDING',26900,'2026-03-02 00:26:44','2026-03-02 00:26:44');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reviews` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `user_account_id` bigint NOT NULL,
  `chef_id` bigint NOT NULL,
  `rating` int NOT NULL,
  `comment` varchar(500) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_reviews_order` (`order_id`),
  KEY `fk_reviews_user` (`user_account_id`),
  KEY `idx_reviews_chef` (`chef_id`),
  CONSTRAINT `fk_reviews_chef` FOREIGN KEY (`chef_id`) REFERENCES `chef_profiles` (`id`),
  CONSTRAINT `fk_reviews_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_reviews_user` FOREIGN KEY (`user_account_id`) REFERENCES `accounts` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reviews`
--

LOCK TABLES `reviews` WRITE;
/*!40000 ALTER TABLE `reviews` DISABLE KEYS */;
INSERT INTO `reviews` VALUES (2,2,6,2,5,'Great service, tasty dishes.','2026-02-25 13:03:27'),(4,6,8,1,5,'hao','2026-03-02 00:26:54');
/*!40000 ALTER TABLE `reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service_items`
--

DROP TABLE IF EXISTS `service_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `chef_id` bigint NOT NULL,
  `title` varchar(80) NOT NULL,
  `duration_minutes` int NOT NULL,
  `price_cents` int NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_service_items_chef` (`chef_id`),
  CONSTRAINT `fk_service_items_chef` FOREIGN KEY (`chef_id`) REFERENCES `chef_profiles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_items`
--

LOCK TABLES `service_items` WRITE;
/*!40000 ALTER TABLE `service_items` DISABLE KEYS */;
INSERT INTO `service_items` VALUES (1,1,'上门私厨（3小时升级版）',180,26900,'ACTIVE','2026-02-25 02:52:15','2026-03-01 22:06:00'),(2,2,'Family Meal (3 dishes + soup)',120,6800,'ACTIVE','2026-02-25 13:03:27','2026-02-25 13:03:27'),(3,2,'Healthy Light Meal',90,5200,'ACTIVE','2026-02-25 13:03:27','2026-02-25 13:03:27'),(4,2,'Small Party Set (6 dishes)',180,9800,'ACTIVE','2026-02-25 13:03:27','2026-02-25 13:03:27'),(5,3,'上门烧烤',120,19900,'ACTIVE','2026-03-01 23:33:00','2026-03-01 23:33:00');
/*!40000 ALTER TABLE `service_items` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-11 19:10:52
