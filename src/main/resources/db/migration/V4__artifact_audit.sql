-- V4__artifact_audit.sql

CREATE TABLE artifact_audit (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    artifact_id UUID NOT NULL REFERENCES artifact(id) ON DELETE CASCADE,
    version BIGINT NOT NULL,
    operation TEXT NOT NULL,
    changed_by TEXT NOT NULL,
    changed_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    signature TEXT NOT NULL,
    public_key_fingerprint TEXT NOT NULL,
    public_key TEXT NOT NULL,
    signing_key_type TEXT NOT NULL,

    field_changes JSONB NOT NULL,
    artifact_snapshot JSONB NOT NULL,

    client_ip TEXT,
    user_agent TEXT,
    reason TEXT,

    UNIQUE (artifact_id, version)
);

CREATE INDEX artifact_audit_artifact_idx ON artifact_audit (artifact_id, version DESC);
CREATE INDEX artifact_audit_changed_by_idx ON artifact_audit (changed_by);
CREATE INDEX artifact_audit_fingerprint_idx ON artifact_audit (public_key_fingerprint);
CREATE INDEX artifact_audit_field_changes_gin ON artifact_audit USING GIN (field_changes);

