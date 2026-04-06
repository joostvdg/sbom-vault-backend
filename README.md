# SBOM Vault – Vaadin + Spring Boot + PostgreSQL

This starter aligns artifacts to Backstage **entityRefs** and includes a **CDEvents** receiver that accepts CloudEvents (structured & binary).

## Goal

* Be database for tracking the life of applications
    * System topology should be managed via Backstage
    * Alternative topology in CMDB (like Service Now)
    * Artifacts by their respective Artifact Repository system (e.g., Nexus/Artifactory/Harbor/GHCR/ECR)
* Collect references to Backstage System Topology
    * be configured to connect to one or more Backstage instances
* Collect references to Artifacts, related to an external reference

## TODO

## How to run
1. 
2. Start PostgreSQL (example docker-compose below) and set `spring.datasource.*` in `application.yml` if needed.
2. `./mvnw spring-boot:run` (or `mvn spring-boot:run` if you have Maven installed).
3. Visit `http://localhost:8080/artifacts` for the Vaadin view; `http://localhost:8080/swagger-ui.html` for OpenAPI.

## CDEvents

POST to `/api/v1/cdevents` with header `Content-Type: application/cloudevents+json` for structured mode
or send `ce-*` headers in binary mode.

Example (structured):
```json
{
  "specversion":"1.0",
  "type":"dev.cdevents.artifact.packaged.v1",
  "id":"123",
  "source":"https://ci.example/pipelines/99",
  "subject": "artifact:oci",
  "time":"2025-09-20T12:00:00Z",
  "datacontenttype":"application/json",
  "data":{
    "artifact":{"digest":"sha256:abc...","name":"example/app","kind":"oci","uri":"ghcr.io/org/app@sha256:abc..."},
    "producer":{"component":"component:default/my-service"}
  }
}
```

## Docker Compose (Postgres)
```yaml
version: "3.9"
services:
  db:
    image: postgres:16
    environment:
      POSTGRES_DB: sbomvault
      POSTGRES_USER: sbom
      POSTGRES_PASSWORD: sbom
    ports: ["5432:5432"]
    volumes: [ "pgdata:/var/lib/postgresql/data" ]
volumes:
  pgdata: {}
```

## API Examples

### Ingest SBOM with CD Event

```bash
http POST localhost:8080/api/cdevents/ingest \
  Content-Type:application/json \
  < examples/sbom-event.json
```

### Submit Verification Event

```bash
http POST localhost:8080/api/cdevents/verify \
    Content-Type:application/json \
    < examples/verification-event.json
```

### Get Artifact Information

```bash
http GET localhost:8080/api/artifacts/registry.example.com/app:v1.0.0
```



### Generate SSH Signing Key

Create an Ed25519 key pair specifically for signing artifacts:

```bash
# Generate Ed25519 key pair
ssh-keygen -t ed25519 -C "your.email@example.com" -f ~/.ssh/artifact_signing_key
```

```bash
# Don't set a passphrase if using in CI/CD, or set one for interactive use
# View public key
cat ~/.ssh/artifact_signing_key.pub

# Get key fingerprint
ssh-keygen -lf ~/.ssh/artifact_signing_key.pub
````

### Create Artifact

**Using httpie:**

```shell
# Prepare artifact data
cat > artifact.json << 'EOF'
{
  "catalogReference": "example-catalog",
  "kind": "container",
  "name": "example-artifact",
  "artifactVersion": "1.0.0",
  "digest": "sha256:0123456789abcdef",
  "registry": "ghcr.io",
  "repository": "joostvdg/example-artifact",
  "uri": "ghcr.io/joostvdg/example-artifact:1.0.0",
  "labels": {"env": "dev", "team": "platform"}
}
EOF

# Create signature
SIGNATURE=$(echo -n "$(cat artifact.json)" | ssh-keygen -Y sign -n artifact -f ~/.ssh/artifact_signing_key | grep -A 100 "BEGIN SSH SIGNATURE" | base64 -w 0)

# Get public key and fingerprint
PUBLIC_KEY=$(cat ~/.ssh/artifact_signing_key.pub)
FINGERPRINT=$(ssh-keygen -lf ~/.ssh/artifact_signing_key.pub | awk '{print $2}')

# Create artifact with audit
http POST http://localhost:8080/api/artifacts \
  catalogReference="example-catalog" \
  kind="container" \
  name="example-artifact" \
  artifactVersion="1.0.0" \
  digest="sha256:0123456789abcdef" \
  registry="ghcr.io" \
  repository="joostvdg/example-artifact" \
  uri="ghcr.io/joostvdg/example-artifact:1.0.0" \
  labels:='{"env":"dev","team":"platform"}' \
  X-Changed-By:"user@example.com" \
  X-Signature:"${SIGNATURE}" \
  X-Public-Key:"${PUBLIC_KEY}" \
  X-Public-Key-Fingerprint:"${FINGERPRINT}" \
  X-Signing-Key-Type:"ssh-ed25519"
```

**Using curl:**

```shell
# Using the same variables from above
curl -X POST http://localhost:8080/api/artifacts \
  -H "Content-Type: application/json" \
  -H "X-Changed-By: user@example.com" \
  -H "X-Signature: ${SIGNATURE}" \
  -H "X-Public-Key: ${PUBLIC_KEY}" \
  -H "X-Public-Key-Fingerprint: ${FINGERPRINT}" \
  -H "X-Signing-Key-Type: ssh-ed25519" \
  -d '{
    "catalogReference": "example-catalog",
    "kind": "container",
    "name": "example-artifact",
    "artifactVersion": "1.0.0",
    "digest": "sha256:0123456789abcdef",
    "registry": "ghcr.io",
    "repository": "joostvdg/example-artifact",
    "uri": "ghcr.io/joostvdg/example-artifact:1.0.0",
    "labels": {"env": "dev", "team": "platform"}
  }'
```


### Get All Artifacts

```shell
http http://localhost:8080/api/artifacts
```

### Attach SBOM

```shell
UUID=9b64c4da-0bbb-42c9-bdbd-cb3422c8be04
```

```shell
# replace {UUID} with the artifact id or use the full Location URL path
http POST http://localhost:8080/api/artifacts/${UUID}/sboms \
  format="cyclonedx1.6+json" \
  source="snyk" \
  docName="my-sbom" \
  docVersion="1.0" \
  jsonb:=@mySBOM.json

```

## Artifact Audit System

All artifact changes are tracked with SSH signature verification, similar to GitHub's commit signing.

### How It Works

1. **Generate SSH Key**: Create an Ed25519 key pair for signing
2. **Sign Changes**: Sign artifact data with your private key
3. **Submit Request**: Include signature, public key, and fingerprint
4. **Verification**: System verifies signature and stores audit record

### Setup SSH Signing Key

```bash
# Generate Ed25519 key
ssh-keygen -t ed25519 -C "your.email@example.com" -f ~/.ssh/artifact_signing_key
```

```sh
# Get public key
cat ~/.ssh/artifact_signing_key.pub
```

```sh
# Get fingerprint
ssh-keygen -lf ~/.ssh/artifact_signing_key.pub
```


### Create and Update Artifact with Audit
```sh
# Prepare artifact data
cat > artifact.json << 'EOF'
{
  "catalogReference": "example-catalog",
  "kind": "container",
  "name": "example-artifact",
  "artifactVersion": "1.0.0",
  "digest": "sha256:0123456789abcdef",
  "registry": "ghcr.io",
  "repository": "joostvdg/example-artifact",
  "uri": "ghcr.io/joostvdg/example-artifact:1.0.0",
  "labels": {"env": "dev", "team": "platform"}
}
EOF
```

```sh
# Create signature
SIGNATURE=$(echo -n "$(cat artifact.json)" | ssh-keygen -Y sign -n artifact -f ~/.ssh/artifact_signing_key | grep -A 100 "BEGIN SSH SIGNATURE" | base64 -w 0)
```

```sh
# Get public key and fingerprint
PUBLIC_KEY=$(cat ~/.ssh/artifact_signing_key.pub)
FINGERPRINT=$(ssh-keygen -lf ~/.ssh/artifact_signing_key.pub | awk '{print $2}')
```

```sh
# Create artifact with audit
http POST http://localhost:8080/api/artifacts \
  catalogReference="example-catalog" \
  kind="container" \
  name="example-artifact" \
  artifactVersion="1.0.0" \
  digest="sha256:0123456789abcdef" \
  registry="ghcr.io" \
  repository="joostvdg/example-artifact" \
  uri="ghcr.io/joostvdg/example-artifact:1.0.0" \
  labels:='{"env":"dev","team":"platform"}' \
  changedBy="user@example.com" \
  signature="${SIGNATURE}" \
  publicKey="${PUBLIC_KEY}" \
  publicKeyFingerprint="${FINGERPRINT}" \
  signingKeyType="ssh-ed25519"
```

### Update Artifact with Audit

```sh
UUID="9b64c4da-0bbb-42c9-bdbd-cb3422c8be04"
```

Prepare update data

```sh
cat > update.json << 'EOF'
{
  "tags": {"version": "1.0.1", "stage": "production"},
  "reason": "Production deployment"
}
EOF
```

Sign update

```sh
UPDATE_SIG=$(echo -n "$(cat update.json)" | ssh-keygen -Y sign -n artifact -f ~/.ssh/artifact_signing_key | grep -A 100 "BEGIN SSH SIGNATURE" | base64 -w 0)
```

Apply update
```sh
http PATCH http://localhost:8080/api/artifacts/${UUID} \
  tags:='{"version":"1.0.1","stage":"production"}' \
  reason="Production deployment" \
  changedBy="user@example.com" \
  signature="${UPDATE_SIG}" \
  publicKey="${PUBLIC_KEY}" \
  publicKeyFingerprint="${FINGERPRINT}" \
  signingKeyType="ssh-ed25519"
```

### View Audit Records

```sh
http GET http://localhost:8080/api/artifacts/${UUID}/audit
```

## Create Artifacts

```shell
mvn clean verify
```

```shell

```

## Local Tool Scanning & Signing

* Cosign: https://github.com/sigstore/cosign
    * https://edu.chainguard.dev/open-source/sigstore/cosign/how-to-install-cosign/   
* Syft: https://oss.anchore.com/docs/installation/syft/
* Trivy
* Snyk

### Cosign

```sh
brew install cosign
```

### Syft

```shell
curl -sSfL https://get.anchore.io/syft | sudo sh -s -- -b /usr/local/bin
```