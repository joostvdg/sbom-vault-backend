.PHONY: test-ingest test-verify test-query setup-examples

PORT ?= 8080
BASE_URL = http://localhost:$(PORT)

setup-examples:
	@mkdir -p examples
	@echo "Creating example JSON files..."

test-ingest:
	@echo "Testing SBOM ingestion..."
	http POST "$(BASE_URL)/api/ingest/artifacts?entityRef=example-entity" Content-Type:application/json < examples/artifact.json

test-verify:
	@echo "Testing verification event..."
	http POST "$(BASE_URL)/api/cdevents" Content-Type:application/cloudevents+json < examples/verification-event.json

test-query:
	@echo "Querying artifact information..."
	http GET "$(BASE_URL)/api/artifacts/registry.example.com/app:v1.0.0"


test-all: test-ingest test-verify test-query
	@echo "All API tests completed"


compose-clean:
	@echo "Cleaning up Docker Compose resources"
	docker compose down
	docker volume rm sbom-vault-backend_db_data

compose-up:
	@echo "Starting Database via Docker Compose"
	docker compose up -d

compose-re: compose-clean compose-up
	@echo "Finished cycling docker compose setup"


run:
	mvn spring-boot:run