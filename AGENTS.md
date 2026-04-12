# AGENTS.md

## What this repo is
- Single Spring Boot app that serves both REST APIs and a Vaadin UI (`src/main/java/net/joostvdg/sbomvault/SbomVaultApplication.java`, `src/main/java/net/joostvdg/sbomvault/ui/ArtifactsView.java`).
- Domain goal: store supply-chain evidence for external apps (artifacts + SBOM/signature/attestation/verification/audit metadata).
- PostgreSQL is the source of truth; schema evolves via Flyway SQL in `src/main/resources/db/migration`.

## Fast local workflow
- Start DB: `docker compose up -d db` (defaults in `src/main/resources/application.yml` use `sbomvault/sbomvault`).
- Run app + UI: `mvn spring-boot:run` (Vaadin UI at `/artifacts`, APIs under `/api/**`).
- Smoke checks: `mvn test` (currently mostly compile/smoke), then `make test-all` for HTTPie endpoint flow checks.
- Style gates: `mvn checkstyle:check` and `mvn spotless:check` (`spotless:apply` also enforces `/* (C)2025 */` header).
- Package release jar: `mvn -DskipTests package` -> `target/sbom-vault-backend.jar` (+ Vaadin frontend build).

## Code map and service boundaries
- API controllers: `controller/ArtifactController.java`, `controller/IngestController.java`, `controller/CDEventsController.java`.
- Core orchestration: `service/ArtifactService.java`, `service/IngestService.java`, `service/CDEventsService.java`, `service/VerificationService.java`.
- Persistence: JPA entities in `model/*`, repositories in `repo/*`; `Artifact` is the central aggregate (`model/Artifact.java`).
- Vaadin is explicitly kept away from APIs via `vaadin.exclude-urls: /api/**` in `application.yml`.

## Data and schema patterns (project-specific)
- JSONB-first modeling is intentional: `labels_jsonb`, `sources_jsonb`, `builders_jsonb`, `tags_jsonb`, audit snapshots, SBOM/attestation payloads (`V2`, `V3`, `V4` migrations).
- New metadata should usually be added as JSONB + GIN index + entity field (not a new scalar column first).
- UTC timestamps are standard (`model/Defaults.java` and `@PrePersist` in entities like `Artifact`, `ArtifactAudit`).
- Audit is explicit, not JPA auditing: `ArtifactService.createArtifactWithAudit()` writes version `0`; `updateArtifact()` increments `artifact.changeVersion` and persists field deltas + full snapshot.

## Ingestion and API contract gotchas
- Identity in ingest/event flows is digest-first (`IngestService` and `CDEventsService` call `ArtifactRepo.findByDigest`).
- `POST /api/artifacts` reads signing/audit fields from headers (`X-Changed-By`, `X-Signature`, `X-Public-Key`, `X-Public-Key-Fingerprint`, `X-Signing-Key-Type`).
- `PATCH /api/artifacts/{id}` expects signing/audit fields in JSON body (`changedBy`, `signature`, `publicKey`, `publicKeyFingerprint`, `signingKeyType`).
- `GET /api/artifacts/{registry}/{path:.+}` treats everything after first slash as repository path; lookup aligns with `ArtifactService.getArtifactDetails()` + `ArtifactRepo.findByRegistryAndRepository()`.
- Manual creation derives `uri` from `registry + "/" + repository` if omitted (`ArtifactService.createArtifact`).

## External integrations and release surface
- CloudEvents ingress supports structured + binary at `/api/cdevents` (`CDEventsController` -> `CDEventsService`).
- Verification integration shells out to `cosign verify <artifact-uri>` in `VerificationService`; there is no controller endpoint wired yet.
- Provenance/signing scripts (`jar-provenance.sh`, `image-provenance.sh`, `release-bundle.sh`) rely on `cosign`, `syft`, Docker Buildx, and keep release assets in `release/`.
- Treat release bundle shape as compatibility: checksums, CycloneDX SBOMs, Sigstore bundles, and image provenance JSON are all first-class artifacts.

## Agent guardrails for this repo
- Prefer changing `src/main/**` and migrations over generated content in `target/**` or Vaadin generated frontend files.
- When evolving storage/API behavior, update both entity+Flyway and endpoint contract examples (`README.md`, `examples/*.json`, `Makefile` smoke flows) together.
