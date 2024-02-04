CREATE TABLE `team_logo` (
  `team_logo_id` bigint(20) NOT NULL,
  `picture_data` longblob,
  `team_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `team_logo`
  ADD PRIMARY KEY (`team_logo_id`),
  ADD UNIQUE KEY `UNQ_PERSON_PICTURE_PERSON` (`team_id`);

ALTER TABLE `team_logo`
  MODIFY `team_logo_id` bigint(20) NOT NULL AUTO_INCREMENT;