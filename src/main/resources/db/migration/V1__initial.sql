-- V1__initial.sql
CREATE TABLE catalog_entity (
  entity_ref TEXT PRIMARY KEY,
  kind TEXT NOT NULL,
  namespace TEXT DEFAULT 'default',
  name TEXT NOT NULL,
  title TEXT,
  owner_ref TEXT,
  system_ref TEXT,
  relations_jsonb JSONB,
  raw_entity_jsonb JSONB,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE artifact (
  id UUID PRIMARY KEY,
  entity_ref TEXT REFERENCES catalog_entity(entity_ref) ON DELETE SET NULL,
  kind TEXT NOT NULL,
  name TEXT NOT NULL,
  version TEXT,
  digest TEXT UNIQUE,
  registry TEXT,
  repository TEXT,
  uri TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE sbom (
  id UUID PRIMARY KEY,
  artifact_id UUID NOT NULL REFERENCES artifact(id) ON DELETE CASCADE,
  format TEXT NOT NULL,
  source TEXT,
  doc_name TEXT,
  doc_version TEXT,
  jsonb JSONB NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX sbom_jsonb_gin ON sbom USING GIN (jsonb);

CREATE TABLE signature (
  id UUID PRIMARY KEY,
  artifact_id UUID NOT NULL REFERENCES artifact(id) ON DELETE CASCADE,
  signer TEXT,
  key_id TEXT,
  certificate_pem TEXT,
  transparency_log_id TEXT,
  raw_sig_bytes BYTEA,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE checksum (
  id UUID PRIMARY KEY,
  artifact_id UUID NOT NULL REFERENCES artifact(id) ON DELETE CASCADE,
  algo TEXT NOT NULL,
  value TEXT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (artifact_id, algo)
);

CREATE TABLE attestation (
  id UUID PRIMARY KEY,
  artifact_id UUID NOT NULL REFERENCES artifact(id) ON DELETE CASCADE,
  type TEXT NOT NULL,
  predicate_type TEXT,
  issuer TEXT,
  jsonb JSONB NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX attestation_jsonb_gin ON attestation USING GIN (jsonb);

CREATE TABLE verification_run (
  id UUID PRIMARY KEY,
  artifact_id UUID NOT NULL REFERENCES artifact(id) ON DELETE CASCADE,
  tool TEXT NOT NULL,
  policy TEXT,
  result TEXT NOT NULL,
  details_jsonb JSONB,
  started_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  finished_at TIMESTAMPTZ
);
CREATE INDEX verification_details_gin ON verification_run USING GIN (details_jsonb);
CREATE INDEX artifact_entity_ref_idx ON artifact(entity_ref);
