CREATE database bank;
CREATE USER 'appcontroller'@'localhost' identified BY '';
GRANT ALL ON bank.* TO 'appcontroller'@'localhost';
CREATE TABLE `transactions` (
  `id` int NOT NULL,
  `amount` decimal(10,0) NOT NULL,
  `timestamp` datetime NOT NULL,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `hibernate_sequence` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
LOCK TABLES `hibernate_sequence` WRITE;
INSERT INTO `hibernate_sequence` VALUES (1);
UNLOCK TABLES;
