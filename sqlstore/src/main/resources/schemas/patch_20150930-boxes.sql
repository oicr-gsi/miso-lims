
-- This is meant to emulate sequence functionality found in other RDBMs. http://www.microshell.com/database/mysql/emulating-nextval-function-to-get-sequence-in-mysql/
CREATE DATABASE `sequence`;
CREATE TABLE `sequence`.`sequence_data` (
    `sequence_name` varchar(100) NOT NULL,
    `sequence_cur_value` bigint(20) unsigned DEFAULT 1
    PRIMARY KEY (`sequence_name`)
) ENGINE=MyISAM;

CREATE FUNCTION `nextval` (`seq_name` varchar(100))
RETURNS bigint(20) NOT DETERMINISTIC
BEGIN
    DECLARE cur_val bigint(20);
    SELECT sequence_cur_value INTO cur_val FROM sequence.sequence_data WHERE sequence_name = seq_name;
 
    IF cur_val IS NOT NULL THEN
        UPDATE sequence.sequence_data SET sequence_cur_value = sequence_cur_value + 1 WHERE sequence_name = seq_name;
    END IF;
    RETURN cur_val;
END


CREATE TABLE `BoxSize` (
  `boxSizeId` bigint(20) NOT NULL AUTO_INCREMENT,
  `rows` bigint(20) NOT NULL,
  `columns` bigint(20) NOT NULL,
  PRIMARY KEY (`boxTypeId`),
  UNIQUE (`rows`, columns`)
) ENGINE=MyISAM AUTO_INCREMENT=99 DEFAULT CHARSET=utf8;

CREATE TABLE `BoxUse` (
  `boxUseId` bigint(20) NOT NULL AUTO_INCREMENT,
  `alias` varchar(255) NOT NULL,
  PRIMARY KEY (`boxUseId`),
  UNIQUE (`alias`)
) ENGINE=MyISAM AUTO_INCREMENT=99 DEFAULT CHARSET=utf8;

CREATE TABLE `Box` (
  `boxId` bigint(20) NOT NULL AUTO_INCREMENT,
  `boxSizeId` bigint(20) NOT NULL REFERENCES BoxSize(boxSizeId),
  `boxUseId` bigint(20) NOT NULL REFERENCES BoxUse(boxUseId),
  `name` varchar(255) NOT NULL,
  `alias` varchar(255) NOT NULL,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  `locationBarcode` varchar(255) DEFAULT NULL,
  `securityProfile_profileId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`boxId`),
  UNIQUE (`name`),
  UNIQUE (`alias`),
) ENGINE=MyISAM AUTO_INCREMENT=99 DEFAULT CHARSET=utf8;

CREATE TABLE `BoxPosition` (
	`boxPositionId` bigint(20) NOT NULL,
	`boxId` bigint(20) REFERENCES Box(boxId) NOT NULL ON CASCADE DELETE,
	`column` bigint(20) NOT NULL,
	`row` bigint(20) NOT NULL,
	PRIMARY KEY (`boxPositionId`),
	UNIQUE KEY (`boxId`, `column`, `row`)
) ENGINE=MyISAM AUTO_INCREMENT=99 DEFAULT CHARSET=utf8;

INSERT INTO sequence(sequence_name) VALUES('box_position_seq');

ALTER TABLE `Sample` ADD COLUMN (
	`boxPositionId` bigint(20) REFERENCES BoxPosition(boxPositionId) DEFAULT nextval('box_position_seq'),
	`emptied` bit(1) NOT NULL DEFAULT 0,
	`volume` double DEFAULT NULL);

ALTER TABLE `Library` ADD COLUMN (
	`boxPositionId` bigint(20) REFERENCES BoxPosition(boxPositionId) DEFAULT nextval('box_position_seq'),
	`emptied` bit(1) NOT NULL DEFAULT 0,
	`volume` double DEFAULT NULL);

-- There is no check that boxPositionId isn't reused between Sample and Library, but there should be.
