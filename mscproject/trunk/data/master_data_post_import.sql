CREATE INDEX ix_saccession_pa ON sequence_accession (primary_accession)
;

ALTER TABLE architecture ADD CONSTRAINT PRIMARY KEY(id);
ALTER TABLE `domain` ADD CONSTRAINT PRIMARY KEY(accession);
ALTER TABLE `domain_clan` ADD CONSTRAINT FOREIGN KEY(domain_accession) REFERENCES `domain`(`accession`);
ALTER TABLE `domain_clan` ADD CONSTRAINT FOREIGN KEY(clan_accession) REFERENCES `clan`(`accession`);

-- NORMALISE SEQUENCE_ACCESSION FOR SEQUENCE_DOMAIN TABLE
CREATE INDEX ix_sd_sa ON sequence_domain (sequence_accession)
;

/*
SELECT sd.sequence_accession, sa.primary_accession
FROM sequence_domain AS sd 
    INNER JOIN sequence_accession AS sa ON sequence_accession = accession
WHERE sd.sequence_accession <> sa.primary_accession
;
*/

UPDATE sequence_domain AS sd 
    INNER JOIN sequence_accession AS sa ON sequence_accession = accession
SET sd.sequence_accession = sa.primary_accession
WHERE sd.sequence_accession <> sa.primary_accession
;
-- 51824 record(s) affected



-- NORMALISE SEQUENCE_ACCESSION FOR SEQUENCE_ARCHITECTURE TABLE
CREATE INDEX ix_sr_sa ON sequence_architecture (sequence_accession)
;
/*
SELECT sr.sequence_accession, sa.primary_accession
FROM sequence_architecture AS sr 
    INNER JOIN sequence_accession AS sa ON sr.sequence_accession = sa.accession
WHERE sr.sequence_accession <> sa.primary_accession
;
*/
UPDATE sequence_architecture AS sr
    INNER JOIN sequence_accession AS sa ON sr.sequence_accession = sa.accession
SET sr.sequence_accession = sa.primary_accession
WHERE sr.sequence_accession <> sa.primary_accession
;
-- 23600 record(s) affected


-- NORMALISE SEQUENCE_ACCESSION FOR SEQUENCE_PDB TABLE
CREATE INDEX ix_sp_sa ON sequence_pdb (sequence_accession)
;
/*
SELECT sp.sequence_accession, sa.primary_accession
FROM sequence_pdb AS sp 
    INNER JOIN sequence_accession AS sa ON sp.sequence_accession = sa.accession
WHERE sp.sequence_accession <> sa.primary_accession
;
*/
UPDATE sequence_pdb AS sp
    INNER JOIN sequence_accession AS sa ON sp.sequence_accession = sa.accession
SET sp.sequence_accession = sa.primary_accession
WHERE sp.sequence_accession <> sa.primary_accession
;
-- 64 record(s) affected


-- NORMALISE SEQUENCE_ACCESSION FOR DRUG_TARGET TABLE
CREATE INDEX ix_dt_sa ON drug_target (sequence_accession)
;
/*
SELECT dt.sequence_accession, sa.primary_accession
FROM drug_target AS dt 
    INNER JOIN sequence_accession AS sa ON dt.sequence_accession = sa.accession
WHERE dt.sequence_accession <> sa.primary_accession
;
*/
UPDATE drug_target AS dt
    INNER JOIN sequence_accession AS sa ON dt.sequence_accession = sa.accession
SET dt.sequence_accession = sa.primary_accession
WHERE dt.sequence_accession <> sa.primary_accession
;
-- 69 record(s) affected

-- ADD REMAINING INDEXES AND FOREIGN KEY CONSTRAINTS
ALTER TABLE sequence ADD CONSTRAINT PRIMARY KEY sequence(accession);

ALTER TABLE domain_clan ADD CONSTRAINT FOREIGN KEY (clan_accession) REFERENCES clan(accession);
ALTER TABLE domain_clan ADD CONSTRAINT FOREIGN KEY (domain_accession) REFERENCES domain(accession);

ALTER TABLE drug ADD CONSTRAINT PRIMARY KEY drug(id)
;
ALTER TABLE drug_target ADD CONSTRAINT FOREIGN KEY (drug_id) REFERENCES drug(id);
-- delete trembl drug targets

DELETE dt FROM drug_target as dt
    LEFT JOIN `sequence` as s on dt.sequence_accession = s.accession
WHERE s.accession IS NULL;
--  902 record(s) affected 
ALTER TABLE drug_target ADD CONSTRAINT FOREIGN KEY (sequence_accession) REFERENCES sequence(accession);

ALTER TABLE sequence_accession ADD CONSTRAINT FOREIGN KEY (primary_accession) REFERENCES sequence(accession);
-- ? DROP INDEX ix_saccession_pa ON sequence_accession;
-- ? ALTER TABLE sequence_accession ADD CONSTRAINT PRIMARY KEY (accession)

create index ix_sequence_species on sequence(species);
create index ix_domain_accession on sequence_domain(domain_accession);

ALTER TABLE sequence_architecture ADD CONSTRAINT FOREIGN KEY(architecture_id) REFERENCES architecture(id);
create index ix_sa_aid on sequence_architecture(architecture_id);

ALTER TABLE sequence_architecture ADD CONSTRAINT PRIMARY KEY(sequence_accession);

select count(*) from species
-- create index ix_species_name on species(name)

-- update tmp set organism = null where organism <> 'Homo sapiens (Human)'
-- 6013679 record(s) affected 
-- update tmp set organism = 32 where organism = 'Homo sapiens (Human)'
-- 54861 record(s) affected

-- insert into sequence (id, accession, species) 
select name, accession, organism
from tmp
where organism = 32
--  54861 record(s) affected 





