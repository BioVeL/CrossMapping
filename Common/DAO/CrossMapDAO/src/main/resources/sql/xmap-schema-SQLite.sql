CREATE TABLE IF NOT EXISTS `Crossmap` (
  `id` int(10) unsigned NOT NULL,
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
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `Checklist` (
  `id` int(10) unsigned NOT NULL,
  `checklistName` varchar(50) NOT NULL,
  `fileName` varchar(255) NOT NULL,
  `user` varchar(255) NOT NULL,
  `scope` varchar(255) NOT NULL,
  `prefixTableDB` varchar(255) DEFAULT NULL,
  `taskId` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `ExportCrossmapResult` (
  `id` int(10) unsigned NOT NULL,
  `name` varchar(50) NOT NULL,
  `user` varchar(50) NOT NULL,
  `scope` varchar(50) NOT NULL,
  `xMapId` int(10) unsigned NOT NULL,
  `includeAcceptedNames` tinyint(1) DEFAULT 0,
  `exportDate` timestamp NOT NULL, 
  `resultFileURL` varchar(600) NOT NULL,
  `taskId` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE  IF NOT EXISTS `TaxonomicRankHierarchy` (
  `id` smallint NOT NULL,
  `rank` varchar(80) NOT NULL,
  `parentId` smallint DEFAULT NULL,
  `isHigherRank` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
);
