-- V2__topology_and_labels.sql

-- 1) Topology reference table (e.g., Backstage, ServiceNow)
CREATE TABLE topology_reference (
    id UUID PRIMARY KEY,
    system TEXT NOT NULL,         -- e.g. BACKSTAGE, SERVICENOW
    external_id TEXT NOT NULL,    -- id in the external system
    reference TEXT,               -- optional URL or canonical ref
    raw_jsonb JSONB,              -- optional raw payload
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (system, external_id)
);
CREATE INDEX topology_reference_raw_gin ON topology_reference USING GIN (raw_jsonb);

-- 2) Many-to-many join between artifact and topology_reference
CREATE TABLE artifact_topology (
    artifact_id UUID NOT NULL REFERENCES artifact(id) ON DELETE CASCADE,
    topology_id UUID NOT NULL REFERENCES topology_reference(id) ON DELETE CASCADE,
    role TEXT,                    -- optional: 'primary', 'cmdb', 'backstage', ...
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (artifact_id, topology_id)
);
CREATE INDEX artifact_topology_topology_idx ON artifact_topology (topology_id);

-- 3) Labels as JSON on artifact
ALTER TABLE artifact
    ADD COLUMN IF NOT EXISTS labels_jsonb JSONB NOT NULL DEFAULT '{}'::jsonb;

CREATE INDEX IF NOT EXISTS artifact_labels_gin ON artifact USING GIN (labels_jsonb);
