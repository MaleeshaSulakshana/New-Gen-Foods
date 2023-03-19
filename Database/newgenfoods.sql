-- phpMyAdmin SQL Dump
-- version 5.1.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 07, 2022 at 09:27 PM
-- Server version: 10.4.19-MariaDB
-- PHP Version: 7.4.19

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `newgenfoods`
--

-- --------------------------------------------------------

--
-- Table structure for table `addon`
--

CREATE TABLE `addon` (
  `id` int(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `price` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `addon`
--

INSERT INTO `addon` (`id`, `name`, `price`) VALUES
(1, 'Cheese', '100.00'),
(2, 'Galic', '50.00');

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `psw` varchar(1000) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`name`, `email`, `psw`) VALUES
('Maleesha', 'maleesha@gmail.com', '12345678');

-- --------------------------------------------------------

--
-- Table structure for table `category`
--

CREATE TABLE `category` (
  `id` int(255) NOT NULL,
  `category` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `category`
--

INSERT INTO `category` (`id`, `category`) VALUES
(1, 'Rice'),
(2, 'Pizza'),
(4, 'Pasta');

-- --------------------------------------------------------

--
-- Table structure for table `comment`
--

CREATE TABLE `comment` (
  `id` int(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `food_id` varchar(255) NOT NULL,
  `comment` varchar(1000) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `comment`
--

INSERT INTO `comment` (`id`, `email`, `food_id`, `comment`) VALUES
(6, 'mpmsulakshana@gmail.com', '2', 'no beef'),

-- --------------------------------------------------------

--
-- Table structure for table `foods`
--

CREATE TABLE `foods` (
  `id` int(255) NOT NULL,
  `title` varchar(255) NOT NULL,
  `price` varchar(255) NOT NULL,
  `category` varchar(255) NOT NULL,
  `ingredients` varchar(255) NOT NULL,
  `image` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `foods`
--

INSERT INTO `foods` (`id`, `title`, `price`, `category`, `ingredients`, `image`, `status`) VALUES
(2, ' Basmati Fried Rice ', '350', 'Rice', 'Basmati rice, Chicken or Fish, Chop suey or Mushroom, Batu moju, Chili past', 'https://firebasestorage.googleapis.com/v0/b/new-gen-foods.appspot.com/o/Foods_Pictures%2F2?alt=media&token=6f5b4275-6d66-49a6-a456-360ac1af1036', 'Available'),
(3, 'Mongolian Rice', '450.00', 'Rice', 'Basmati rice, Chicken or Fish, Egg, Batu moju, Chili past', 'https://images.aws.nestle.recipes/resized/76628592b357751f0e8ac6ad6b467f2b_mongolian-rice_944_531.jpg', 'Available'),
(4, 'Chinese Fried Rice ', '400', 'Rice', 'Rice, Chicken devil, Mashroom', 'https://firebasestorage.googleapis.com/v0/b/new-gen-foods.appspot.com/o/Foods_Pictures%2F9308801?alt=media&token=e4e06d63-a443-43d9-8941-42207f63887a', 'Available');

-- --------------------------------------------------------

--
-- Table structure for table `offers`
--

CREATE TABLE `offers` (
  `id` int(255) NOT NULL,
  `order_id` int(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `date` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `order_id` int(255) NOT NULL,
  `id` int(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `date` varchar(255) NOT NULL,
  `total` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL,
  `discount` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`order_id`, `id`, `email`, `date`, `total`, `status`, `discount`) VALUES
(31, 2, 'mpmsulakshana@gmail.com', '2022-05-03', '1350.0', 'Pending', 'no'),
(32, 3, 'mpmsulakshana@gmail.com', '2022-05-03', '350.0', 'Pending', 'no'),
(33, 4, 'mpmsulakshana@gmail.com', '2022-05-03', '350.0', 'Pending', 'no'),

-- --------------------------------------------------------

--
-- Table structure for table `order_details`
--

CREATE TABLE `order_details` (
  `details_id` int(255) NOT NULL,
  `id` varchar(255) NOT NULL,
  `food_id` varchar(255) NOT NULL,
  `qty` varchar(255) NOT NULL,
  `addon` varchar(255) NOT NULL,
  `notice` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `order_details`
--

INSERT INTO `order_details` (`details_id`, `id`, `food_id`, `qty`, `addon`, `notice`) VALUES
(45, '2', '2', '3', 'Cheese', ''),
(46, '3', '2', '1', '', ''),
(47, '4', '2', '1', '', ''),

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `dob` varchar(255) NOT NULL,
  `psw` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`name`, `email`, `dob`, `psw`) VALUES
('Maleeesha', 'mpmsulakshana@gmail.com', '1998/10/02', '12345678'),

--
-- Indexes for dumped tables
--

--
-- Indexes for table `addon`
--
ALTER TABLE `addon`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`email`);

--
-- Indexes for table `category`
--
ALTER TABLE `category`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `comment`
--
ALTER TABLE `comment`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `foods`
--
ALTER TABLE `foods`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `offers`
--
ALTER TABLE `offers`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`order_id`);

--
-- Indexes for table `order_details`
--
ALTER TABLE `order_details`
  ADD PRIMARY KEY (`details_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `addon`
--
ALTER TABLE `addon`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `category`
--
ALTER TABLE `category`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `comment`
--
ALTER TABLE `comment`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `foods`
--
ALTER TABLE `foods`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `offers`
--
ALTER TABLE `offers`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
  MODIFY `order_id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=37;

--
-- AUTO_INCREMENT for table `order_details`
--
ALTER TABLE `order_details`
  MODIFY `details_id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=52;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
