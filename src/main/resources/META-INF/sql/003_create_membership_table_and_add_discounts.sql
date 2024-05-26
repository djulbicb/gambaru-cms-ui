-- Add discount field to barcode
ALTER TABLE `barcode`
ADD COLUMN `discount` int DEFAULT 0;

-- Create membership table
CREATE TABLE `person_membership` (
  `membership_id` bigint(20) NOT NULL,
  `barcode_id` bigint(20) NOT NULL,
  `timestamp` datetime(6) NOT NULL,
  `compact_date` int NOT NULL,
  `fee` int DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `person_membership`
  MODIFY COLUMN `membership_id` bigint(20) NOT NULL AUTO_INCREMENT,
  ADD PRIMARY KEY (`membership_id`);

CREATE INDEX idx_membership_compact_date ON `person_membership` (`compact_date`);