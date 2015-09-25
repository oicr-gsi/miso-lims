ALTER TABLE Experiment ADD COLUMN lastModifier bigint(20) NOT NULL DEFAULT 1 REFERENCES User(userId);
ALTER TABLE Library ADD COLUMN lastModifier bigint(20) NOT NULL DEFAULT 1 REFERENCES User(userId);
ALTER TABLE Plate ADD COLUMN lastModifier bigint(20) NOT NULL DEFAULT 1 REFERENCES User(userId);
ALTER TABLE Pool ADD COLUMN lastModifier bigint(20) NOT NULL DEFAULT 1 REFERENCES User(userId);
ALTER TABLE Run ADD COLUMN lastModifier bigint(20) NOT NULL DEFAULT 1 REFERENCES User(userId);
ALTER TABLE Sample ADD COLUMN lastModifier bigint(20) NOT NULL DEFAULT 1 REFERENCES User(userId);
ALTER TABLE SequencerPartitionContainer ADD COLUMN lastModifier bigint(20) NOT NULL DEFAULT 1 REFERENCES User(userId);
ALTER TABLE Study ADD COLUMN lastModifier bigint(20) NOT NULL DEFAULT 1 REFERENCES User(userId);

CREATE TABLE SampleChangeLog (
  sampleId bigint(20) NOT NULL REFERENCES Sample(sampleId),
  columnsChanged text NOT NULL,
  userId bigint(20) NOT NULL,
  message text NOT NULL,
  changeTime timestamp DEFAULT CURRENT_TIMESTAMP) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TRIGGER SampleChange BEFORE UPDATE ON Sample
FOR EACH ROW
  INSERT INTO SampleChangeLog(sampleId, columnsChanged, userId, message) VALUES (
    NEW.sampleId,
    CONCAT_WS(',',
      CASE WHEN NEW.accession IS NULL <> OLD.accession IS NULL OR NEW.accession <> OLD.accession THEN 'accession' END,
      CASE WHEN NEW.alias IS NULL <> OLD.alias IS NULL OR NEW.alias <> OLD.alias THEN 'alias' END,
      CASE WHEN NEW.description <> OLD.description THEN 'description' END,
      CASE WHEN NEW.identificationBarcode IS NULL <> OLD.identificationBarcode IS NULL OR NEW.identificationBarcode <> OLD.identificationBarcode THEN 'identificationBarcode' END,
      CASE WHEN NEW.locationBarcode <> OLD.locationBarcode THEN 'locationBarcode' END,
      CASE WHEN NEW.name <> OLD.name THEN 'name' END,
      CASE WHEN NEW.project_projectId <> OLD.project_projectId THEN 'project_projectId' END,
      CASE WHEN NEW.qcPassed IS NULL <> OLD.qcPassed IS NULL OR NEW.qcPassed <> OLD.qcPassed THEN 'qcPassed' END,
      CASE WHEN NEW.receivedDate IS NULL <> OLD.receivedDate IS NULL OR NEW.receivedDate <> OLD.receivedDate THEN 'receivedDate' END,
      CASE WHEN NEW.sampleType <> OLD.sampleType THEN 'sampleType' END,
      CASE WHEN NEW.scientificName <> OLD.scientificName THEN 'scientificName' END,
      CASE WHEN NEW.taxonIdentifier IS NULL <> OLD.taxonIdentifier IS NULL OR NEW.taxonIdentifier <> OLD.taxonIdentifier THEN 'taxonIdentifier' END),
    NEW.lastModifier,
    CONCAT(
      (SELECT fullname FROM User WHERE userId = NEW.lastModifier),
      ' has changed: ',
      CONCAT_WS(', ',
        CASE WHEN NEW.accession IS NULL <> OLD.accession IS NULL OR NEW.accession <> OLD.accession THEN CONCAT('accession: ', COALESCE(OLD.accession, 'n/a'), ' → ', COALESCE(NEW.accession, 'n/a')) END,
        CASE WHEN NEW.alias IS NULL <> OLD.alias IS NULL OR NEW.alias <> OLD.alias THEN CONCAT('alias: ', COALESCE(OLD.alias, 'n/a'), ' → ', COALESCE(NEW.alias, 'n/a')) END,
        CASE WHEN NEW.description <> OLD.description THEN CONCAT('description: ', OLD.description, ' → ', NEW.description) END,
        CASE WHEN NEW.identificationBarcode IS NULL <> OLD.identificationBarcode IS NULL OR NEW.identificationBarcode <> OLD.identificationBarcode THEN CONCAT('identification: ', COALESCE(OLD.identificationBarcode, 'n/a'), ' → ', COALESCE(NEW.identificationBarcode, 'n/a')) END,
        CASE WHEN NEW.locationBarcode IS NULL <> OLD.locationBarcode IS NULL OR NEW.locationBarcode <> OLD.locationBarcode THEN CONCAT('location: ', COALESCE(OLD.locationBarcode, 'n/a'), ' → ', COALESCE(NEW.locationBarcode, 'n/a')) END,
        CASE WHEN NEW.name <> OLD.name THEN CONCAT('name: ', OLD.name, ' → ', NEW.name) END,
        CASE WHEN NEW.project_projectId <> OLD.project_projectId THEN CONCAT('project: ', (SELECT name FROM Project WHERE projectId = OLD.project_projectId), ' → ', (SELECT name FROM Project WHERE projectId = NEW.project_projectId)) END,
        CASE WHEN NEW.qcPassed IS NULL <> OLD.qcPassed IS NULL OR NEW.qcPassed <> OLD.qcPassed THEN CONCAT('qcPassed: ', COALESCE(OLD.qcPassed, 'n/a'), ' → ', COALESCE(NEW.qcPassed, 'n/a')) END,
        CASE WHEN NEW.receivedDate IS NULL <> OLD.receivedDate IS NULL OR NEW.receivedDate <> OLD.receivedDate THEN CONCAT('received: ', COALESCE(OLD.receivedDate, 'n/a'), ' → ', COALESCE(NEW.receivedDate, 'n/a')) END,
        CASE WHEN NEW.sampleType <> OLD.sampleType THEN CONCAT('type: ', OLD.sampleType, ' → ', NEW.sampleType) END,
        CASE WHEN NEW.scientificName <> OLD.scientificName THEN CONCAT('scientific name: ', OLD.scientificName, ' → ', NEW.scientificName) END,
        CASE WHEN NEW.taxonIdentifier IS NULL <> OLD.taxonIdentifier IS NULL OR NEW.taxonIdentifier <> OLD.taxonIdentifier THEN CONCAT('taxon: ', COALESCE(OLD.taxonIdentifier, 'n/a'), ' → ', COALESCE(NEW.taxonIdentifier, 'n/a')) END)));

CREATE TRIGGER SampleInsert AFTER INSERT ON Sample
FOR EACH ROW
  INSERT INTO SampleChangeLog(sampleId, columnsChanged, userId, message) VALUES (
    NEW.sampleId,
    NULL,
    NEW.lastModifier,
    CONCAT(
      (SELECT fullname FROM User WHERE userId = NEW.lastModifier),
      ' created sample.'));
