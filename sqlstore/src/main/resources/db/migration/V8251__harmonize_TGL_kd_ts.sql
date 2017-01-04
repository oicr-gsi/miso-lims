--StartNoTest
CALL addTargetedSequencing('Agilent SureSelectXT MethylSeq', 'TGL', 'Agilent SureSelectXT MethylSeq', 0);
CALL addTargetedSequencing('IDT xGEN Exome Research Panel v1', 'TGL', 'IDT xGEN Exome Research Panel v1', 0);
CALL addTargetedSequencing('TruSeq RNA Access', 'TGL', 'TruSeq RNA Access', 0);
CALL addTargetedSequencing('Roche SeqCap Epi CpGiant', 'TGL', 'Roche SeqCap Epi CpGiant', 0);
CALL addTargetedSequencing('TruSeq Methylation Epic Exome', 'TGL', 'TruSeq Methylation Epic Exome', 0);

CALL addKitDescriptor('Agilent SureSelect XT Human All Exon V5 + UTRs', 1, 'Agilent', 1, 'Library', 'Illumina', 'TGL');

UPDATE KitDescriptor SET name = 'Agilent SureSelect XT Human All Exon V6 Cosmic' WHERE name = 'Agilent SureSelect Human All Exon V6 Cosmic';

UPDATE TargetedSequencing SET alias = 'Agilent SureSelect XT Human All Exon V5 + UTRs' WHERE alias = 'Agilent SureSelect Human All Exon V5 + UTRs';

UPDATE TargetedSequencing SET kitDescriptorId = (SELECT kitDescriptorId FROM KitDescriptor WHERE name = 'Agilent SureSelect XT Human All Exon V6 Cosmic') WHERE alias = 'Agilent SureSelect XT Human All Exon V6 Cosmic';
UPDATE TargetedSequencing SET kitDescriptorId = (SELECT kitDescriptorId FROM KitDescriptor WHERE name = 'Agilent SureSelect XT Human All Exon V5 + UTRs') WHERE alias = 'Agilent SureSelect XT Human All Exon V5 + UTRs';

-- if any libraries use a kit that is inappropriate for the newly-changed targeted sequencing values, update the kits.
DELIMITER //
DROP PROCEDURE IF EXISTS updateLibraryKitsIfTarSeqUpdated//
CREATE PROCEDURE updateLibraryKitsIfTarSeqUpdated()
BEGIN
  IF EXISTS (SELECT * FROM LibraryDilution WHERE targetedSequencingId IN (SELECT targetedSequencingId FROM TargetedSequencing 
    WHERE alias = 'Agilent SureSelect XT Human All Exon V6 Cosmic'))
  THEN
    UPDATE LibraryAdditionalInfo SET kitDescriptorId = (
      SELECT kitDescriptorId FROM TargetedSequencing WHERE alias = 'Agilent SureSelect XT Human All Exon V6 Cosmic')
    WHERE libraryId = (SELECT library_libraryId FROM LibraryDilution WHERE targetedSequencingId IN (SELECT targetedSequencingId FROM TargetedSequencing 
    WHERE alias = 'Agilent SureSelect XT Human All Exon V6 Cosmic'));
  END IF;

  IF EXISTS (SELECT * FROM LibraryDilution WHERE targetedSequencingId IN (SELECT targetedSequencingId FROM TargetedSequencing 
    WHERE alias = 'Agilent SureSelect XT Human All Exon V5 + UTRs'))
  THEN
    UPDATE LibraryAdditionalInfo SET kitDescriptorId = (
      SELECT kitDescriptorId FROM TargetedSequencing WHERE alias = 'Agilent SureSelect XT Human All Exon V5 + UTRs')
    WHERE libraryId = (SELECT library_libraryId FROM LibraryDilution WHERE targetedSequencingId IN (SELECT targetedSequencingId FROM TargetedSequencing 
    WHERE alias = 'Agilent SureSelect XT Human All Exon V5 + UTRs'));
  END IF;
END //
CALL updateLibraryKitsIfTarSeqUpdated()//
DROP PROCEDURE IF EXISTS updateLibraryKitsIfTarSeqUpdated//
DELIMITER ;
--EndNoTest