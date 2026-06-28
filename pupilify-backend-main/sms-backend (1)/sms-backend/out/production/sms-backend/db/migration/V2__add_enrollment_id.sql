-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: localhost    Database: smart_school_pro
-- ------------------------------------------------------
-- Server version	8.0.36

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
-- Table structure for table `academic_year_config`
--

DROP TABLE IF EXISTS `academic_year_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `academic_year_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `changed_at` datetime(6) DEFAULT NULL,
  `changed_by_user_id` bigint DEFAULT NULL,
  `current_year` varchar(255) NOT NULL,
  `email_sent` bit(1) NOT NULL,
  `emails_sent_count` int NOT NULL,
  `previous_year` varchar(255) DEFAULT NULL,
  `status` enum('ACTIVE','CHANGING','COMPLETED') DEFAULT NULL,
  `school_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK84ijx2urchx4gtxs5dgq1li7v` (`school_id`),
  CONSTRAINT `FK84ijx2urchx4gtxs5dgq1li7v` FOREIGN KEY (`school_id`) REFERENCES `schools` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `academic_year_config`
--

LOCK TABLES `academic_year_config` WRITE;
/*!40000 ALTER TABLE `academic_year_config` DISABLE KEYS */;
/*!40000 ALTER TABLE `academic_year_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `attendance`
--

DROP TABLE IF EXISTS `attendance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attendance` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `attendance_date` date DEFAULT NULL,
  `roll_number` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `academic_year_id` bigint DEFAULT NULL,
  `school_id` bigint DEFAULT NULL,
  `school_class_id` bigint DEFAULT NULL,
  `section_id` bigint DEFAULT NULL,
  `student_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKi70479lntx26rcrxb8khhr5o1` (`academic_year_id`),
  KEY `FKg723c1bx2vmwdffyy27kd2rd7` (`school_id`),
  KEY `FK1h9fbpx656h8sxyb8lewq5swy` (`school_class_id`),
  KEY `FKdhc0y7i6skyu3xt4jowibdu0n` (`section_id`),
  KEY `FK7121lveuhtmu9wa6m90ayd5yg` (`student_id`),
  CONSTRAINT `FK1h9fbpx656h8sxyb8lewq5swy` FOREIGN KEY (`school_class_id`) REFERENCES `school_classes` (`id`),
  CONSTRAINT `FK7121lveuhtmu9wa6m90ayd5yg` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`),
  CONSTRAINT `FKdhc0y7i6skyu3xt4jowibdu0n` FOREIGN KEY (`section_id`) REFERENCES `sections` (`id`),
  CONSTRAINT `FKg723c1bx2vmwdffyy27kd2rd7` FOREIGN KEY (`school_id`) REFERENCES `schools` (`id`),
  CONSTRAINT `FKi70479lntx26rcrxb8khhr5o1` FOREIGN KEY (`academic_year_id`) REFERENCES `academic_year_config` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attendance`
--

LOCK TABLES `attendance` WRITE;
/*!40000 ALTER TABLE `attendance` DISABLE KEYS */;
/*!40000 ALTER TABLE `attendance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `class_teacher_mappings`
--

DROP TABLE IF EXISTS `class_teacher_mappings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `class_teacher_mappings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `is_active` bit(1) NOT NULL,
  `academic_year_id` bigint DEFAULT NULL,
  `school_id` bigint DEFAULT NULL,
  `class_id` bigint DEFAULT NULL,
  `section_id` bigint DEFAULT NULL,
  `teacher_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKe0q4v4c8iosf9ilulcn3ipvca` (`academic_year_id`),
  KEY `FKpiaddlhiax7ebmdovrcao32r0` (`school_id`),
  KEY `FK2oiyyp3su9g61vq3cjso6tp9s` (`class_id`),
  KEY `FKe7e7you0v0ai8rlhsycrtuabf` (`section_id`),
  KEY `FK9uw7b6tk2hxx235pkat84rthj` (`teacher_id`),
  CONSTRAINT `FK2oiyyp3su9g61vq3cjso6tp9s` FOREIGN KEY (`class_id`) REFERENCES `school_classes` (`id`),
  CONSTRAINT `FK9uw7b6tk2hxx235pkat84rthj` FOREIGN KEY (`teacher_id`) REFERENCES `teachers` (`id`),
  CONSTRAINT `FKe0q4v4c8iosf9ilulcn3ipvca` FOREIGN KEY (`academic_year_id`) REFERENCES `academic_year_config` (`id`),
  CONSTRAINT `FKe7e7you0v0ai8rlhsycrtuabf` FOREIGN KEY (`section_id`) REFERENCES `sections` (`id`),
  CONSTRAINT `FKpiaddlhiax7ebmdovrcao32r0` FOREIGN KEY (`school_id`) REFERENCES `schools` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `class_teacher_mappings`
--

LOCK TABLES `class_teacher_mappings` WRITE;
/*!40000 ALTER TABLE `class_teacher_mappings` DISABLE KEYS */;
/*!40000 ALTER TABLE `class_teacher_mappings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `daily_time_table`
--

DROP TABLE IF EXISTS `daily_time_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `daily_time_table` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `day_of_week` varchar(255) DEFAULT NULL,
  `end_time` varchar(255) DEFAULT NULL,
  `is_active` bit(1) DEFAULT b'1',
  `start_time` varchar(255) DEFAULT NULL,
  `academic_year_id` bigint DEFAULT NULL,
  `school_id` bigint DEFAULT NULL,
  `class_id` bigint DEFAULT NULL,
  `section_id` bigint DEFAULT NULL,
  `subject_id` bigint DEFAULT NULL,
  `teacher_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6xxgp5r0wvvx3i8ehuuumf6f4` (`academic_year_id`),
  KEY `FKe2v0bhuffouhrd0vgo9bajxm4` (`school_id`),
  KEY `FKj7olv078t4w8un2ov0p44wg85` (`class_id`),
  KEY `FKg11jhuu9466e91m7qwrc47jdh` (`section_id`),
  KEY `FK2xlikpjcvurtdjvns62g3gvpw` (`subject_id`),
  KEY `FKbh20hobmia2st6hcxaaqlik0y` (`teacher_id`),
  CONSTRAINT `FK2xlikpjcvurtdjvns62g3gvpw` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`),
  CONSTRAINT `FK6xxgp5r0wvvx3i8ehuuumf6f4` FOREIGN KEY (`academic_year_id`) REFERENCES `academic_year_config` (`id`),
  CONSTRAINT `FKbh20hobmia2st6hcxaaqlik0y` FOREIGN KEY (`teacher_id`) REFERENCES `teachers` (`id`),
  CONSTRAINT `FKe2v0bhuffouhrd0vgo9bajxm4` FOREIGN KEY (`school_id`) REFERENCES `schools` (`id`),
  CONSTRAINT `FKg11jhuu9466e91m7qwrc47jdh` FOREIGN KEY (`section_id`) REFERENCES `sections` (`id`),
  CONSTRAINT `FKj7olv078t4w8un2ov0p44wg85` FOREIGN KEY (`class_id`) REFERENCES `school_classes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `daily_time_table`
--

LOCK TABLES `daily_time_table` WRITE;
/*!40000 ALTER TABLE `daily_time_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `daily_time_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exam_results`
--

DROP TABLE IF EXISTS `exam_results`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exam_results` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `grade` varchar(255) DEFAULT NULL,
  `is_absent` bit(1) DEFAULT b'0',
  `is_passed` bit(1) DEFAULT b'0',
  `marks_obtained_practical` int DEFAULT NULL,
  `marks_obtained_theory` int DEFAULT NULL,
  `percentage` double DEFAULT NULL,
  `total_marks_obtained` int DEFAULT NULL,
  `total_max_marks` int DEFAULT NULL,
  `total_practical_marks` int DEFAULT NULL,
  `total_theory_marks` int DEFAULT NULL,
  `uploaded_at` date DEFAULT NULL,
  `uploaded_by_teacher_id` bigint DEFAULT NULL,
  `academic_year_id` bigint NOT NULL,
  `exam_schedule_id` bigint NOT NULL,
  `school_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `subject_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbkebndk3cc2kyxwo7ktn150fn` (`academic_year_id`),
  KEY `FKpg83u0u7rb6x54fne81sk381e` (`exam_schedule_id`),
  KEY `FK7jb223xkbx0iusj9xvu9m36o0` (`school_id`),
  KEY `FKr7qgl670f47u65kkdm8ex5119` (`student_id`),
  KEY `FK1h1qupqh86nvnjvuui13tv89u` (`subject_id`),
  CONSTRAINT `FK1h1qupqh86nvnjvuui13tv89u` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`),
  CONSTRAINT `FK7jb223xkbx0iusj9xvu9m36o0` FOREIGN KEY (`school_id`) REFERENCES `schools` (`id`),
  CONSTRAINT `FKbkebndk3cc2kyxwo7ktn150fn` FOREIGN KEY (`academic_year_id`) REFERENCES `academic_year_config` (`id`),
  CONSTRAINT `FKpg83u0u7rb6x54fne81sk381e` FOREIGN KEY (`exam_schedule_id`) REFERENCES `exam_schedule` (`id`),
  CONSTRAINT `FKr7qgl670f47u65kkdm8ex5119` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exam_results`
--

LOCK TABLES `exam_results` WRITE;
/*!40000 ALTER TABLE `exam_results` DISABLE KEYS */;
/*!40000 ALTER TABLE `exam_results` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exam_schedule`
--

DROP TABLE IF EXISTS `exam_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exam_schedule` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `end_time` varchar(255) DEFAULT NULL,
  `exam_date` date DEFAULT NULL,
  `exam_name` varchar(255) NOT NULL,
  `is_active` bit(1) DEFAULT b'1',
  `start_time` varchar(255) DEFAULT NULL,
  `academic_year_id` bigint NOT NULL,
  `school_id` bigint NOT NULL,
  `class_id` bigint NOT NULL,
  `section_id` bigint DEFAULT NULL,
  `subject_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqfi5p5ms0layopnor8jsg6ibk` (`academic_year_id`),
  KEY `FKbd1d9kyv1bu5sw24nut5ceji4` (`school_id`),
  KEY `FKey8bfbji516tn9kwx2nmrqce5` (`class_id`),
  KEY `FKkytu20s7n6q6pfb3g7d86jc7r` (`section_id`),
  KEY `FKe7wasy5g2kbjbpbsm5k7bws7m` (`subject_id`),
  CONSTRAINT `FKbd1d9kyv1bu5sw24nut5ceji4` FOREIGN KEY (`school_id`) REFERENCES `schools` (`id`),
  CONSTRAINT `FKe7wasy5g2kbjbpbsm5k7bws7m` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`),
  CONSTRAINT `FKey8bfbji516tn9kwx2nmrqce5` FOREIGN KEY (`class_id`) REFERENCES `school_classes` (`id`),
  CONSTRAINT `FKkytu20s7n6q6pfb3g7d86jc7r` FOREIGN KEY (`section_id`) REFERENCES `sections` (`id`),
  CONSTRAINT `FKqfi5p5ms0layopnor8jsg6ibk` FOREIGN KEY (`academic_year_id`) REFERENCES `academic_year_config` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exam_schedule`
--

LOCK TABLES `exam_schedule` WRITE;
/*!40000 ALTER TABLE `exam_schedule` DISABLE KEYS */;
/*!40000 ALTER TABLE `exam_schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `expenses`
--

DROP TABLE IF EXISTS `expenses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `expenses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` double DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `expense_name` varchar(255) DEFAULT NULL,
  `academic_year_id` bigint DEFAULT NULL,
  `school_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9a6jelb13io17ns8xhgguugsy` (`academic_year_id`),
  KEY `FKfuh2at2qwitvh9t0opcgixrse` (`school_id`),
  CONSTRAINT `FK9a6jelb13io17ns8xhgguugsy` FOREIGN KEY (`academic_year_id`) REFERENCES `academic_year_config` (`id`),
  CONSTRAINT `FKfuh2at2qwitvh9t0opcgixrse` FOREIGN KEY (`school_id`) REFERENCES `schools` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `expenses`
--

LOCK TABLES `expenses` WRITE;
/*!40000 ALTER TABLE `expenses` DISABLE KEYS */;
/*!40000 ALTER TABLE `expenses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fee_payment`
--

DROP TABLE IF EXISTS `fee_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fee_payment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount_paid` double DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `payment_date` date DEFAULT NULL,
  `payment_mode` varchar(255) DEFAULT NULL,
  `receipt_number` varchar(255) DEFAULT NULL,
  `remaining_balance` double DEFAULT NULL,
  `roll_number` varchar(255) DEFAULT NULL,
  `total_class_fees` double DEFAULT NULL,
  `academic_year_id` bigint DEFAULT NULL,
  `school_id` bigint DEFAULT NULL,
  `class_id` bigint DEFAULT NULL,
  `section_id` bigint DEFAULT NULL,
  `student_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9ah6fk8ahry84e8tlymmtvs9y` (`academic_year_id`),
  KEY `FKc3hvh4050ikynbwrx751p33ty` (`school_id`),
  KEY `FK9nftt5j02g3gxdr925vqx86to` (`class_id`),
  KEY `FKqam0rwtr28kn29k09ejd2bihv` (`section_id`),
  KEY `FKt8nf00wna79wkrcegl5pipyv2` (`student_id`),
  CONSTRAINT `FK9ah6fk8ahry84e8tlymmtvs9y` FOREIGN KEY (`academic_year_id`) REFERENCES `academic_year_config` (`id`),
  CONSTRAINT `FK9nftt5j02g3gxdr925vqx86to` FOREIGN KEY (`class_id`) REFERENCES `school_classes` (`id`),
  CONSTRAINT `FKc3hvh4050ikynbwrx751p33ty` FOREIGN KEY (`school_id`) REFERENCES `schools` (`id`),
  CONSTRAINT `FKqam0rwtr28kn29k09ejd2bihv` FOREIGN KEY (`section_id`) REFERENCES `sections` (`id`),
  CONSTRAINT `FKt8nf00wna79wkrcegl5pipyv2` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fee_payment`
--

LOCK TABLES `fee_payment` WRITE;
/*!40000 ALTER TABLE `fee_payment` DISABLE KEYS */;
/*!40000 ALTER TABLE `fee_payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fee_structure`
--

DROP TABLE IF EXISTS `fee_structure`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fee_structure` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `is_active` bit(1) NOT NULL,
  `total_fees` double DEFAULT NULL,
  `academic_year_id` bigint DEFAULT NULL,
  `school_id` bigint DEFAULT NULL,
  `class_id` bigint DEFAULT NULL,
  `section_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKkj6u0w6sknm4bkrm3fmgx0xd5` (`academic_year_id`),
  KEY `FKl6q26i223miahmpb5l8exc26x` (`school_id`),
  KEY `FK34vj13rgd3xcv325rjneocjvd` (`class_id`),
  KEY `FKe6u4r73ef2o77olx3lw9dfj6c` (`section_id`),
  CONSTRAINT `FK34vj13rgd3xcv325rjneocjvd` FOREIGN KEY (`class_id`) REFERENCES `school_classes` (`id`),
  CONSTRAINT `FKe6u4r73ef2o77olx3lw9dfj6c` FOREIGN KEY (`section_id`) REFERENCES `sections` (`id`),
  CONSTRAINT `FKkj6u0w6sknm4bkrm3fmgx0xd5` FOREIGN KEY (`academic_year_id`) REFERENCES `academic_year_config` (`id`),
  CONSTRAINT `FKl6q26i223miahmpb5l8exc26x` FOREIGN KEY (`school_id`) REFERENCES `schools` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fee_structure`
--

LOCK TABLES `fee_structure` WRITE;
/*!40000 ALTER TABLE `fee_structure` DISABLE KEYS */;
/*!40000 ALTER TABLE `fee_structure` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `school_classes`
--

DROP TABLE IF EXISTS `school_classes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `school_classes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `class_name` varchar(255) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `school_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5pyw0mtl3c0033cwb8v2grg9c` (`school_id`),
  CONSTRAINT `FK5pyw0mtl3c0033cwb8v2grg9c` FOREIGN KEY (`school_id`) REFERENCES `schools` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `school_classes`
--

LOCK TABLES `school_classes` WRITE;
/*!40000 ALTER TABLE `school_classes` DISABLE KEYS */;
/*!40000 ALTER TABLE `school_classes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `schools`
--

DROP TABLE IF EXISTS `schools`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `schools` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `mail_id` varchar(255) NOT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `school_logo` varchar(255) DEFAULT NULL,
  `school_name` varchar(255) NOT NULL,
  `subscription_status` enum('ACTIVE','INACTIVE','TRIAL') DEFAULT NULL,
  `current_academic_year_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_616jccsw4aexuo50rvt6ab4vx` (`mail_id`),
  KEY `FKr91ww9853e6lneub9xkakqm9y` (`current_academic_year_id`),
  CONSTRAINT `FKr91ww9853e6lneub9xkakqm9y` FOREIGN KEY (`current_academic_year_id`) REFERENCES `academic_year_config` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schools`
--

LOCK TABLES `schools` WRITE;
/*!40000 ALTER TABLE `schools` DISABLE KEYS */;
/*!40000 ALTER TABLE `schools` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sections`
--

DROP TABLE IF EXISTS `sections`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sections` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `is_active` bit(1) NOT NULL,
  `section_name` varchar(255) DEFAULT NULL,
  `school_id` bigint DEFAULT NULL,
  `class_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKob22i8tnkvrk56r77epe2ks7c` (`school_id`),
  KEY `FKjgardfpl7eeqvk68gyq2eovbb` (`class_id`),
  CONSTRAINT `FKjgardfpl7eeqvk68gyq2eovbb` FOREIGN KEY (`class_id`) REFERENCES `school_classes` (`id`),
  CONSTRAINT `FKob22i8tnkvrk56r77epe2ks7c` FOREIGN KEY (`school_id`) REFERENCES `schools` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sections`
--

LOCK TABLES `sections` WRITE;
/*!40000 ALTER TABLE `sections` DISABLE KEYS */;
/*!40000 ALTER TABLE `sections` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_fee_dues`
--

DROP TABLE IF EXISTS `student_fee_dues`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_fee_dues` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cleared` bit(1) NOT NULL,
  `created_at` date DEFAULT NULL,
  `due_amount` double DEFAULT NULL,
  `from_year` varchar(255) DEFAULT NULL,
  `paid_from_due` double DEFAULT NULL,
  `remaining_due` double DEFAULT NULL,
  `to_year` varchar(255) DEFAULT NULL,
  `total_fees` double DEFAULT NULL,
  `total_paid` double DEFAULT NULL,
  `from_academic_year_id` bigint DEFAULT NULL,
  `school_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `to_academic_year_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjiq1muhsjkhidnmolyn8m9gr` (`from_academic_year_id`),
  KEY `FKrwubqgd0hbw0dsc44m7rqsi78` (`school_id`),
  KEY `FK4x9m786t3uwtr034q8wt7vo78` (`student_id`),
  KEY `FKfivuh4s6nep4obj1vjs6eww05` (`to_academic_year_id`),
  CONSTRAINT `FK4x9m786t3uwtr034q8wt7vo78` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`),
  CONSTRAINT `FKfivuh4s6nep4obj1vjs6eww05` FOREIGN KEY (`to_academic_year_id`) REFERENCES `academic_year_config` (`id`),
  CONSTRAINT `FKjiq1muhsjkhidnmolyn8m9gr` FOREIGN KEY (`from_academic_year_id`) REFERENCES `academic_year_config` (`id`),
  CONSTRAINT `FKrwubqgd0hbw0dsc44m7rqsi78` FOREIGN KEY (`school_id`) REFERENCES `schools` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_fee_dues`
--

LOCK TABLES `student_fee_dues` WRITE;
/*!40000 ALTER TABLE `student_fee_dues` DISABLE KEYS */;
/*!40000 ALTER TABLE `student_fee_dues` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `students`
--

DROP TABLE IF EXISTS `students`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `students` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `aadhar_card_image` varchar(255) DEFAULT NULL,
  `aadhar_card_no` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `apaar_card_image` varchar(255) DEFAULT NULL,
  `apaar_id` varchar(255) DEFAULT NULL,
  `bank_passbook_image` varchar(255) DEFAULT NULL,
  `caste` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `dob` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `enrollment_id` varchar(255) DEFAULT NULL,
  `father_contact_number` varchar(255) DEFAULT NULL,
  `father_name` varchar(255) DEFAULT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `last_class_marksheet` varchar(255) DEFAULT NULL,
  `mother_name` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone_no` varchar(255) DEFAULT NULL,
  `roll_number` int DEFAULT NULL,
  `samagra_id` varchar(255) DEFAULT NULL,
  `samagra_id_image` varchar(255) DEFAULT NULL,
  `student_photo` varchar(255) DEFAULT NULL,
  `tc_image` varchar(255) DEFAULT NULL,
  `academic_year_id` bigint DEFAULT NULL,
  `school_id` bigint DEFAULT NULL,
  `class_id` bigint DEFAULT NULL,
  `section_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_h56tdcm2xuw2iakmf8cu7cjci` (`apaar_id`),
  UNIQUE KEY `UK_h5inprq4hjbia373wrqcw8m9r` (`enrollment_id`),
  UNIQUE KEY `UK_g4fwvutq09fjdlb4bb0byp7t` (`user_id`),
  KEY `FKm1nojwebnl5m9qk18yv9gc16n` (`academic_year_id`),
  KEY `FKdojmg8v3rw2ow4dev2b8q5oqq` (`school_id`),
  KEY `FKnvr9y8csmtxo56rrsp0bdrcqf` (`class_id`),
  KEY `FKbu72kq4xd8qjcemytgfxel71l` (`section_id`),
  CONSTRAINT `FKbu72kq4xd8qjcemytgfxel71l` FOREIGN KEY (`section_id`) REFERENCES `sections` (`id`),
  CONSTRAINT `FKdojmg8v3rw2ow4dev2b8q5oqq` FOREIGN KEY (`school_id`) REFERENCES `schools` (`id`),
  CONSTRAINT `FKdt1cjx5ve5bdabmuuf3ibrwaq` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKm1nojwebnl5m9qk18yv9gc16n` FOREIGN KEY (`academic_year_id`) REFERENCES `academic_year_config` (`id`),
  CONSTRAINT `FKnvr9y8csmtxo56rrsp0bdrcqf` FOREIGN KEY (`class_id`) REFERENCES `school_classes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `students`
--

LOCK TABLES `students` WRITE;
/*!40000 ALTER TABLE `students` DISABLE KEYS */;
/*!40000 ALTER TABLE `students` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subjects`
--

DROP TABLE IF EXISTS `subjects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subjects` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `has_practical` bit(1) NOT NULL DEFAULT b'0',
  `is_active` bit(1) DEFAULT b'1',
  `passing_practical_marks` int DEFAULT NULL,
  `passing_theory_marks` int NOT NULL,
  `subject_code` varchar(255) DEFAULT NULL,
  `subject_name` varchar(255) NOT NULL,
  `total_practical_marks` int DEFAULT NULL,
  `total_theory_marks` int NOT NULL,
  `academic_year_id` bigint DEFAULT NULL,
  `school_id` bigint DEFAULT NULL,
  `class_id` bigint DEFAULT NULL,
  `section_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK43ls88rl144127q9phlc2m01i` (`academic_year_id`),
  KEY `FKmuktvnrq4ft25nduvev1wseqd` (`school_id`),
  KEY `FKimux8baf5i559ob50i447m9yc` (`class_id`),
  KEY `FKrhh429gjxpqh31k82q9ha82ww` (`section_id`),
  CONSTRAINT `FK43ls88rl144127q9phlc2m01i` FOREIGN KEY (`academic_year_id`) REFERENCES `academic_year_config` (`id`),
  CONSTRAINT `FKimux8baf5i559ob50i447m9yc` FOREIGN KEY (`class_id`) REFERENCES `school_classes` (`id`),
  CONSTRAINT `FKmuktvnrq4ft25nduvev1wseqd` FOREIGN KEY (`school_id`) REFERENCES `schools` (`id`),
  CONSTRAINT `FKrhh429gjxpqh31k82q9ha82ww` FOREIGN KEY (`section_id`) REFERENCES `sections` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subjects`
--

LOCK TABLES `subjects` WRITE;
/*!40000 ALTER TABLE `subjects` DISABLE KEYS */;
/*!40000 ALTER TABLE `subjects` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teachers`
--

DROP TABLE IF EXISTS `teachers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teachers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `aadhar_image` varchar(255) DEFAULT NULL,
  `aadhar_no` varchar(255) DEFAULT NULL,
  `is_active` bit(1) DEFAULT b'1',
  `address` varchar(255) DEFAULT NULL,
  `alternate_number` varchar(255) DEFAULT NULL,
  `bank_passbook_image` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `dob` varchar(255) DEFAULT NULL,
  `mail` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `qualification` varchar(255) DEFAULT NULL,
  `salary` double DEFAULT NULL,
  `subject_expertise` varchar(255) DEFAULT NULL,
  `teacher_photo` varchar(255) DEFAULT NULL,
  `school_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_cd1k6xwg9jqtiwx9ybnxpmoh9` (`user_id`),
  KEY `FK25tvrvw3ww2p7mbt62abrbwev` (`school_id`),
  CONSTRAINT `FK25tvrvw3ww2p7mbt62abrbwev` FOREIGN KEY (`school_id`) REFERENCES `schools` (`id`),
  CONSTRAINT `FKb8dct7w2j1vl1r2bpstw5isc0` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teachers`
--

LOCK TABLES `teachers` WRITE;
/*!40000 ALTER TABLE `teachers` DISABLE KEYS */;
/*!40000 ALTER TABLE `teachers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('ROLE_SUPER_ADMIN','ROLE_ADMIN','ROLE_TEACHER','ROLE_STUDENT') DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  `school_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_r43af9ap4edm43mmtq01oddj6` (`username`),
  KEY `FK3gj5j7vnsoxf1wp9n5hsqdiq3` (`school_id`),
  CONSTRAINT `FK3gj5j7vnsoxf1wp9n5hsqdiq3` FOREIGN KEY (`school_id`) REFERENCES `schools` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,_binary '','$2a$10$0gbwXMLbi1YqYRUJzUsCh.2lbjQn.ibNAB.9HW9SV8/hYWdM2zrz6','ROLE_SUPER_ADMIN','nikunjpatidar8888@gmail.com',NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-26  8:31:03
