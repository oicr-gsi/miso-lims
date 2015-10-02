CREATE TABLE `BoxType` (
  `boxTypeId` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `alias` varchar(255) NOT NULL,
  `defaultRows` bigint(20) NOT NULL,
  `defaultColumns` bigint(20) NOT NULL,
  PRIMARY KEY (`boxTypeId`)
) ENGINE=MyISAM AUTO_INCREMENT=99 DEFAULT CHARSET=utf8;

CREATE TABLE `Box` (
  `boxId` bigint(20) NOT NULL AUTO_INCREMENT,
  `boxTypeId` bigint(20) NOT NULL REFERENCES BoxType(boxTypeId),
  `name` varchar(255) NOT NULL,
  `alias` varchar(255) NOT NULL,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  `locationBarcode` varchar(255) DEFAULT NULL,
  `securityProfile_profileId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`boxId`)
) ENGINE=MyISAM AUTO_INCREMENT=99 DEFAULT CHARSET=utf8;

