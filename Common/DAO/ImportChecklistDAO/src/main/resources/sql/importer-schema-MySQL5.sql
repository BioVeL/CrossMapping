CREATE TABLE IF NOT EXISTS `Checklist` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `checklistName` varchar(255) NOT NULL,
  `fileName` varchar(255) NOT NULL,
  `user` varchar(255) NOT NULL,
  `scope` varchar(255) NOT NULL,
  `prefixTableDB` varchar(255) DEFAULT NULL,
  `taskId` varchar(36) NOT NULL,
  `ts` TIMESTAMP default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `checklist_UNIQUE` (`checklistName`,`user`,`scope`),
  FOREIGN KEY (user) REFERENCES User(name),
  FOREIGN KEY (taskId) REFERENCES Task(id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_unicode_ci;
