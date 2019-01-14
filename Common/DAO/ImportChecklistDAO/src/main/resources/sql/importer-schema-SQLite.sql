CREATE TABLE IF NOT EXISTS `Checklist` (
  `id` int(10) unsigned NOT NULL,
  `checklistName` varchar(255) NOT NULL,
  `fileName` varchar(255) NOT NULL,
  `user` varchar(255) NOT NULL,
  `scope` varchar(255) NOT NULL,
  `prefixTableDB` varchar(255) DEFAULT NULL,
  `taskId` varchar(36) NOT NULL,
  PRIMARY KEY (`id`)
);



