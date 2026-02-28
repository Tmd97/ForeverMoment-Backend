 -- Drop stale FK (was pointing to old singular table)
ALTER TABLE experience_time_slot_mappers
    DROP CONSTRAINT IF EXISTS fkjeoj46i4galvnhu7wtdym8jpo;

-- Recreate pointing to correct plural table
ALTER TABLE experience_time_slot_mappers
    ADD CONSTRAINT fk_etsm_exp_location
        FOREIGN KEY (exp_location_id)
            REFERENCES experience_location_mappers(id);


ALTER TABLE experience_time_slot_mapper
  DROP CONSTRAINT IF EXISTS fkjeoj46i4galvnhu7wtdym8jpo;

ALTER TABLE experience_time_slot_mapper
  ADD CONSTRAINT fk_etsm_exp_location
  FOREIGN KEY (exp_location_id)
  REFERENCES experience_location_mappers(id);