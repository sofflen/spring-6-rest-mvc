ALTER TABLE beer
    ALTER COLUMN id TYPE UUID USING id::uuid;
ALTER TABLE customer
    ALTER COLUMN id TYPE UUID USING id::uuid;
