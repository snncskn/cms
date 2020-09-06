ALTER TABLE vehicle_attribute_value ALTER COLUMN value TYPE varchar(100) USING value::varchar;
