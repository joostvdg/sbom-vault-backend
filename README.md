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


### Create Artifact

```shell
http POST http://localhost:8080/api/artifacts \
  catalogReference="example-catalog" \
  kind="container" \
  name="example-artifact" \
  artifactVersion="1.0.0" \
  digest="sha256:0123456789abcdef" \
  registry="ghcr.io" \
  repository="joostvdg/example-artifact" \
  uri="ghcr.io/joostvdg/example-artifact:1.0.0" \
  labels:='{"env":"dev","team":"platform"}'
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
