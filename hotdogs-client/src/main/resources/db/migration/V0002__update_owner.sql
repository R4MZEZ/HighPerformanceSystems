ALTER TABLE owners
    drop column coordinates;

ALTER TABLE owners
    add location geography;
