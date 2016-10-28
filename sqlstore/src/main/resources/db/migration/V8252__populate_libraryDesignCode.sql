--StartNoTest
INSERT INTO LibraryDesignCode (code, description) VALUES
('WG', 'Whole Genome'), ('EX', 'Exome'), ('TS', 'Targeted Sequencing'), ('MR', 'mRNA'), ('SM', 'smRNA'), ('WT', 'Whole Transcriptome'), ('CH', 'Chromatin'), ('AT', 'ATAC-Seq');

UPDATE LibraryDesign SET suffix = '_MR' WHERE name = 'MR';
--EndNoTest