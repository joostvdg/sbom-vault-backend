.PHONY: test-ingest test-verify test-query setup-examples

PORT ?= 8080
BASE_URL = http://localhost:$(PORT)

setup-examples:
	@mkdir -p examples
	@echo "Creating example JSON files..."

test-ingest: setup-examples
	@echo "Testing SBOM ingestion..."
	@echo '{"specversion":"0.3","type":"dev.cdevents.artifact.published.0.1.0","source":"https://ci.example.com/pipeline/123","id":"sbom-12345","time":"2023-10-15T10:30:00Z","subject":{"id":"artifact://registry.example.com/app:v1.0.0","type":"artifact","content":{"sbom":{"name":"example-app","version":"1.0.0","format":"spdx-json","content":"eyJ0ZXN0IjoiZGF0YSJ9"},"checksums":[{"algorithm":"sha256","value":"a1b2c3d4e5f6"}]}}}' > examples/sbom-event.json
	http POST $(BASE_URL)/api/cdevents/ingest Content-Type:application/json < examples/sbom-event.json

test-verify: setup-examples
	@echo "Testing verification event..."
	@echo '{"specversion":"0.3","type":"dev.cdevents.artifact.signed.0.1.0","source":"https://sigstore.example.com","id":"verification-67890","time":"2023-10-15T10:35:00Z","subject":{"id":"artifact://registry.example.com/app:v1.0.0","type":"artifact","content":{"signature":{"keyId":"cosign-key-123","signature":"MEUCIQDtU5","certificate":"-----BEGIN CERTIFICATE-----"},"attestation":{"predicateType":"https://slsa.dev/provenance/v0.2","statement":"eyJhbGciOiJ"}}}}' > examples/verification-event.json
	http POST $(BASE_URL)/api/cdevents/verify Content-Type:application/json < examples/verification-event.json

test-query:
	@echo "Querying artifact information..."
	http GET $(BASE_URL)/api/artifacts/registry.example.com/app:v1.0.0

test-all: test-ingest test-verify test-query
	@echo "All API tests completed"

clean-examples:
	rm -rf examples/

run:
	mvn spring-boot:run