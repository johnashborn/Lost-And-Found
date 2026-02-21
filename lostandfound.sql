-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Feb 21, 2026 at 12:21 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `lostandfound`
--

-- --------------------------------------------------------

--
-- Table structure for table `itemhistory`
--

CREATE TABLE `itemhistory` (
  `HISTORY_ID` int(11) NOT NULL,
  `ITEM_ID` int(11) NOT NULL,
  `EVENT_TYPE` varchar(20) NOT NULL,
  `EVENT_DATE` timestamp NOT NULL DEFAULT current_timestamp(),
  `DETAILS` varchar(200) DEFAULT NULL,
  `STUDENT_ID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `items`
--

CREATE TABLE `items` (
  `ITEM_ID` int(11) NOT NULL,
  `REPORTED_BY` int(11) NOT NULL,
  `FOUND_BY` int(11) DEFAULT NULL,
  `CLAIMED_BY` int(11) DEFAULT NULL,
  `ITEM_NAME` varchar(50) NOT NULL,
  `CATEGORY` varchar(50) NOT NULL,
  `DATE_LOST` date NOT NULL,
  `LOCATION_LAST_SEEN` varchar(100) NOT NULL,
  `DESCRIPTION` varchar(500) DEFAULT NULL,
  `PHOTO` longblob DEFAULT NULL,
  `STATUS` varchar(20) NOT NULL,
  `FOUND_AT` varchar(100) DEFAULT NULL,
  `CLAIMED_AT` varchar(100) DEFAULT NULL,
  `date_found` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `student`
--

CREATE TABLE `student` (
  `ID` int(11) NOT NULL,
  `EMAIL` varchar(50) NOT NULL,
  `PASSWORD` varchar(50) NOT NULL,
  `full_name` varchar(100) DEFAULT NULL,
  `contact_number` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `itemhistory`
--
ALTER TABLE `itemhistory`
  ADD PRIMARY KEY (`HISTORY_ID`),
  ADD KEY `ITEM_ID` (`ITEM_ID`),
  ADD KEY `fk_item_history_student` (`STUDENT_ID`);

--
-- Indexes for table `items`
--
ALTER TABLE `items`
  ADD PRIMARY KEY (`ITEM_ID`),
  ADD KEY `REPORTED_BY` (`REPORTED_BY`),
  ADD KEY `FOUND_BY` (`FOUND_BY`),
  ADD KEY `CLAIMED_BY` (`CLAIMED_BY`);

--
-- Indexes for table `student`
--
ALTER TABLE `student`
  ADD PRIMARY KEY (`ID`),
  ADD UNIQUE KEY `EMAIL` (`EMAIL`),
  ADD UNIQUE KEY `unique_contact` (`contact_number`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `itemhistory`
--
ALTER TABLE `itemhistory`
  MODIFY `HISTORY_ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `items`
--
ALTER TABLE `items`
  MODIFY `ITEM_ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `student`
--
ALTER TABLE `student`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `itemhistory`
--
ALTER TABLE `itemhistory`
  ADD CONSTRAINT `fk_item_history_student` FOREIGN KEY (`STUDENT_ID`) REFERENCES `student` (`ID`),
  ADD CONSTRAINT `itemhistory_ibfk_1` FOREIGN KEY (`ITEM_ID`) REFERENCES `items` (`ITEM_ID`);

--
-- Constraints for table `items`
--
ALTER TABLE `items`
  ADD CONSTRAINT `items_ibfk_1` FOREIGN KEY (`REPORTED_BY`) REFERENCES `student` (`ID`),
  ADD CONSTRAINT `items_ibfk_2` FOREIGN KEY (`FOUND_BY`) REFERENCES `student` (`ID`),
  ADD CONSTRAINT `items_ibfk_3` FOREIGN KEY (`CLAIMED_BY`) REFERENCES `student` (`ID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
