CREATE TABLE IF NOT EXISTS  `TaxonomicRankHierarchy` (
  `id` smallint NOT NULL,
  `rank` varchar(80) NOT NULL,
  `parentId` smallint DEFAULT NULL,
  `isHigherRank` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `rank_UNIQUE` (`rank`),
  FOREIGN KEY (`parentId`) REFERENCES `TaxonomicRankHierarchy` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `Checklist` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `checklistName` varchar(255) NOT NULL,
  `fileName` varchar(255) NOT NULL,
  `user` varchar(255) NOT NULL,
  `scope` varchar(255) NOT NULL,
  `prefixTableDB` varchar(255) DEFAULT NULL,
  `taskId` varchar(36) DEFAULT NULL,
  `ts` TIMESTAMP default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `checklist_UNIQUE` (`checklistName`,`user`,`scope`),
  FOREIGN KEY (user) REFERENCES User(name),
  FOREIGN KEY (taskId) REFERENCES Task(id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_unicode_ci;


CREATE TABLE IF NOT EXISTS `Crossmap` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `shortname` varchar(255) NOT NULL,
  `user` varchar(255) NOT NULL,
  `scope` varchar(255) NOT NULL,
  `longname` varchar(512) DEFAULT NULL,
  `leftChecklistId` int(10) unsigned NOT NULL,
  `rightChecklistId` int(10) unsigned NOT NULL,
  `strict` tinyint(1) NOT NULL,
  `identifyExtraTaxa` varchar(128) DEFAULT NULL,
  `compareHigherTaxa` tinyint(1) NOT NULL,
  `highestRankIdToCompare` smallint DEFAULT NULL,
  `refinementLevel` varchar(128) NOT NULL,
  `prefixTableDB` varchar(255) DEFAULT NULL,
  `taskId` varchar(36) DEFAULT NULL,
  `ts` TIMESTAMP default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `xmap_UNIQUE` (`shortname`,`user`,`scope`),
  FOREIGN KEY (user) REFERENCES User(name),
  FOREIGN KEY (leftChecklistId) REFERENCES Checklist(id),
  FOREIGN KEY (rightChecklistId) REFERENCES Checklist(id),
  FOREIGN KEY (highestRankIdToCompare) REFERENCES TaxonomicRankHierarchy(id),
  FOREIGN KEY (taskId) REFERENCES Task(id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `ExportCrossmapResult` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `user` varchar(255) NOT NULL,
  `scope` varchar(255) NOT NULL,
  `xMapId` int(10) unsigned NOT NULL,
  `includeAcceptedNames` tinyint(1) NOT NULL DEFAULT 0,
  `resultFileURL` varchar(800) DEFAULT NULL,
  `taskId` varchar(36) DEFAULT NULL,
  `exportDate` TIMESTAMP default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, 
  PRIMARY KEY (`id`),
  FOREIGN KEY (xMapId) REFERENCES Crossmap(id),
  FOREIGN KEY (user) REFERENCES User(name),
  FOREIGN KEY (taskId) REFERENCES Task(id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_unicode_ci;


CREATE TABLE `UserKnowledge` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name1` varchar(255) NOT NULL,
  `relationship` varchar(30) NOT NULL,
  `name2` varchar(255) NOT NULL,
  `xMapId` int(10) unsigned NOT NULL,
  `user` varchar(255) NOT NULL,
  `scope` varchar(255) NOT NULL,  
  `comment` varchar(255),
  `ts` TIMESTAMP default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `knowledge` (`name1`,`name2`,`user`,`scope`,`xMapId`),
  FOREIGN KEY (user) REFERENCES User(name),
  FOREIGN KEY (xMapId) REFERENCES Crossmap(id)
) ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 DEFAULT COLLATE=utf8_unicode_ci;