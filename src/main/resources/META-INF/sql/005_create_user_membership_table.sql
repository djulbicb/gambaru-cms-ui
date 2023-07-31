SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `gambaru`
--

-- --------------------------------------------------------

--
-- Table structure for table `user_membership_payments`
--

CREATE TABLE `user_membership_payments` (
  `membership_payment_id` bigint(20) NOT NULL,
  `money` decimal(38,2) DEFAULT NULL,
  `month` int(11) NOT NULL,
  `timestamp` datetime(6) DEFAULT NULL,
  `year` int(11) NOT NULL,
  `barcode_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `user_membership_payments`
--
ALTER TABLE `user_membership_payments`
  ADD PRIMARY KEY (`membership_payment_id`),
  ADD KEY `FK_membership_barcode` (`barcode_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `user_membership_payments`
--
ALTER TABLE `user_membership_payments`
  MODIFY `membership_payment_id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `user_membership_payments`
--
ALTER TABLE `user_membership_payments`
  ADD CONSTRAINT `FK_membership_barcode` FOREIGN KEY (`barcode_id`) REFERENCES `barcode` (`barcode_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;