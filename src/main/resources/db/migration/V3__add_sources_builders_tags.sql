-- V3__add_sources_builders_tags.sql

ALTER TABLE artifact
    ADD COLUMN IF NOT EXISTS sources_jsonb JSONB NOT NULL DEFAULT '[]'::jsonb,
    ADD COLUMN IF NOT EXISTS builders_jsonb JSONB NOT NULL DEFAULT '[]'::jsonb,
    ADD COLUMN IF NOT EXISTS tags_jsonb JSONB NOT NULL DEFAULT '{}'::jsonb;

CREATE INDEX IF NOT EXISTS artifact_sources_gin ON artifact USING GIN (sources_jsonb);
CREATE INDEX IF NOT EXISTS artifact_builders_gin ON artifact USING GIN (builders_jsonb);
CREATE INDEX IF NOT EXISTS artifact_tags_gin ON artifact USING GIN (tags_jsonb);