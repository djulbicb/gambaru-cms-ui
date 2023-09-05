--
-- Database: `gambaru`
--

-- --------------------------------------------------------

--
-- Table structure for table `barcode`
--

CREATE TABLE `barcode` (
  `barcode_id` bigint(20) NOT NULL,
  `assigned_timestamp` datetime(6) DEFAULT NULL,
  `status` enum('ASSIGNED','DEACTIVATED','DELETED','NOT_USED') DEFAULT NULL,
  `person_id` bigint(20) DEFAULT NULL,
  `team_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `person`
--

CREATE TABLE `person` (
  `person_id` bigint(20) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `first_name` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `gender` enum('FEMALE','MALE') DEFAULT NULL,
  `last_name` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `person_attendance`
--

CREATE TABLE `person_attendance` (
  `attendance_id` bigint(20) NOT NULL,
  `timestamp` datetime(6) DEFAULT NULL,
  `barcode_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `person_picture`
--

CREATE TABLE `person_picture` (
  `picture_id` bigint(20) NOT NULL,
  `picture_data` longblob,
  `person_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `subscription`
--

CREATE TABLE `subscription` (
  `subscription_id` bigint(20) NOT NULL,
  `endDate` date DEFAULT NULL,
  `isFreeOfCharge` bit(1) NOT NULL,
  `startDate` date DEFAULT NULL,
  `barcode_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `team`
--

CREATE TABLE `team` (
  `team_id` bigint(20) NOT NULL,
  `membership_payment` decimal(38,2) DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `status` enum('ACTIVE','DELETED') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `barcode`
--
ALTER TABLE `barcode`
  ADD PRIMARY KEY (`barcode_id`),
  ADD KEY `idx_barcode_team` (`team_id`),
  ADD KEY `fk_barcode_person` (`person_id`);

--
-- Indexes for table `person`
--
ALTER TABLE `person`
  ADD PRIMARY KEY (`person_id`);

--
-- Indexes for table `person_attendance`
--
ALTER TABLE `person_attendance`
  ADD PRIMARY KEY (`attendance_id`),
  ADD KEY `idx_attendance_timestamp` (`timestamp`),
  ADD KEY `fk_person_attendance_barcode` (`barcode_id`);

--
-- Indexes for table `person_picture`
--
ALTER TABLE `person_picture`
  ADD PRIMARY KEY (`picture_id`),
  ADD UNIQUE KEY `UNQ_PERSON_PICTURE_PERSON` (`person_id`);

--
-- Indexes for table `subscription`
--
ALTER TABLE `subscription`
  ADD PRIMARY KEY (`subscription_id`),
  ADD UNIQUE KEY `UNQ_SUBSCRIPTION_BARCODE` (`barcode_id`);

--
-- Indexes for table `team`
--
ALTER TABLE `team`
  ADD PRIMARY KEY (`team_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `barcode`
--
ALTER TABLE `barcode`
  MODIFY `barcode_id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `person`
--
ALTER TABLE `person`
  MODIFY `person_id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `person_attendance`
--
ALTER TABLE `person_attendance`
  MODIFY `attendance_id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `person_picture`
--
ALTER TABLE `person_picture`
  MODIFY `picture_id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `subscription`
--
ALTER TABLE `subscription`
  MODIFY `subscription_id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `team`
--
ALTER TABLE `team`
  MODIFY `team_id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `barcode`
--
ALTER TABLE `barcode`
  ADD CONSTRAINT `fk_barcode_person` FOREIGN KEY (`person_id`) REFERENCES `person` (`person_id`),
  ADD CONSTRAINT `fk_barcode_team` FOREIGN KEY (`team_id`) REFERENCES `team` (`team_id`);

--
-- Constraints for table `person_attendance`
--
ALTER TABLE `person_attendance`
  ADD CONSTRAINT `fk_person_attendance_barcode` FOREIGN KEY (`barcode_id`) REFERENCES `barcode` (`barcode_id`);

--
-- Constraints for table `person_picture`
--
ALTER TABLE `person_picture`
  ADD CONSTRAINT `fk_person_picture_person` FOREIGN KEY (`person_id`) REFERENCES `person` (`person_id`);

--
-- Constraints for table `subscription`
--
ALTER TABLE `subscription`
  ADD CONSTRAINT `fk_subscription_barcode` FOREIGN KEY (`barcode_id`) REFERENCES `barcode` (`barcode_id`);
COMMIT;