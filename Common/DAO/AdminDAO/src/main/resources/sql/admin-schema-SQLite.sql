CREATE TABLE IF NOT EXISTS `User` (
  `name` varchar(255) NOT NULL,
  `scope` varchar(255) NOT NULL,
  `password` varchar(16) DEFAULT NULL,
  `role` enum('admin','GBP','GSD') NOT NULL,
  PRIMARY KEY (`name`,`scope`)
);


CREATE TABLE IF NOT EXISTS `Exception` (
  `id` varchar(36) NOT NULL,
  `description` varchar(800) NOT NULL,
  `className` varchar(512) NOT NULL,
  `detail` text,
  `parameters` text,
  `date` datetime NOT NULL,
  `user` varchar(255) DEFAULT NULL,
  `scope` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);


CREATE TABLE IF NOT EXISTS `LogMethodExecutionTime` 
	(id INTEGER PRIMARY KEY /*autoincrement*/, 
		methodName VARCHAR(255) NOT NULL, 
	className VARCHAR(512) NOT NULL, 
	parameters TEXT, 
	elapsedTime INT NOT NULL, 
	date DATETIME NOT NULL, 
	user VARCHAR(255) DEFAULT NULL,
	scope varchar(255) DEFAULT NULL);
	
	
CREATE TABLE IF NOT EXISTS LogLevel (
  minTimeToLog INT NOT NULL COMMENT 'In miliseconds\nNegative value implies Log OFF\nPossitive value implies only log method with execution time bigger than it'
);


CREATE TABLE IF NOT EXISTS `Task` (
  `id` varchar(36) NOT NULL,
  `name` varchar(512) DEFAULT NULL,
  `type` varchar(16) NOT NULL,
  `status` varchar(48) NOT NULL,
  `details` varchar(800) DEFAULT NULL,
  `percentage` float DEFAULT 0,
  `startDate` timestamp NULL DEFAULT NULL,
  `finishDate` timestamp NULL DEFAULT NULL,
  `taskResponse` blob,
  `user` varchar(255) NOT NULL,
  `scope` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
)