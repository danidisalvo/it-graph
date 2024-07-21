-- Create a new user
-- CREATE USER admin WITH PASSWORD 'admin';

-- Grant privileges to the new user on the database
GRANT ALL PRIVILEGES ON DATABASE itgraph TO admin;

-- Table: public.node

-- DROP TABLE IF EXISTS public.node;

CREATE TABLE IF NOT EXISTS public.node
(
    id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    x integer NOT NULL,
    y integer NOT NULL,
    type character varying(255) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT node_pkey PRIMARY KEY (id)
    )

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.node
    OWNER to admin;

-- Table: public.edge

-- DROP TABLE IF EXISTS public.edge;

CREATE TABLE IF NOT EXISTS public.edge
(
    source character varying COLLATE pg_catalog."default" NOT NULL,
    target character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT edge_pkey PRIMARY KEY (source, target),
    CONSTRAINT source_fk FOREIGN KEY (source)
    REFERENCES public.node (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE CASCADE,
    CONSTRAINT target_fk FOREIGN KEY (target)
    REFERENCES public.node (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE CASCADE
    )

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.edge
    OWNER to admin;
-- Index: fki_source_fk

-- DROP INDEX IF EXISTS public.fki_source_fk;

CREATE INDEX IF NOT EXISTS fki_source_fk
    ON public.edge USING btree
    (source COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;
-- Index: fki_target_fk

-- DROP INDEX IF EXISTS public.fki_target_fk;

CREATE INDEX IF NOT EXISTS fki_target_fk
    ON public.edge USING btree
    (target COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;
