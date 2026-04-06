.PHONY: test-ingest test-verify test-query setup-examples

PORT ?= 8080
BASE_URL = http://localhost:$(PORT)

SHELL := /usr/bin/env zsh
APP_NAME ?= sbom-vault-backend
VERSION  ?= 0.1.0

REGISTRY ?= ghcr.io
NAMESPACE ?= joostvdg

IMAGE    ?= $(REGISTRY)/$(NAMESPACE)/$(APP_NAME):$(VERSION)
IMAGE2   ?= $(REGISTRY)/$(NAMESPACE)/$(APP_NAME):$(VERSION)-runtime

JAR_GLOB ?= target/sbom-vault-backend.jar
JAR_FILE = target/sbom-vault-backend.jar
PREP_DIR ?= .docker-prep

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

.PHONY: docker docker2 prep verify-jar clean

docker:
	mvn -DskipTests clean package
	docker build \
		-f Dockerfile \
		--build-arg JAR_FILE=$(JAR_GLOB) \
		-t $(IMAGE) \
		.


verify-jar:
	test -f $(JAR_FILE)
	java -jar $(JAR_FILE) --help >/dev/null || (echo "Jar is not executable"; exit 1)
	java -Djarmode=tools -jar $(JAR_FILE) --help >/dev/null || (echo "Jar does not support Spring Boot tools mode"; exit 1)

prep:
	rm -rf $(PREP_DIR)
	mkdir -p $(PREP_DIR)
	mvn -DskipTests clean package
# 	java -jar $(JAR_FILE) --help >/dev/null
# 	java -Djarmode=tools -jar $(JAR_FILE) --help >/dev/null
	cp $(JAR_FILE) $(PREP_DIR)/application.jar
	cd $(PREP_DIR) && java -Djarmode=tools -jar application.jar extract --layers --destination extracted
	rm -f $(PREP_DIR)/application.jar
	mkdir -p $(PREP_DIR)/application
	cp -R $(PREP_DIR)/extracted/dependencies/. $(PREP_DIR)/application/
	cp -R $(PREP_DIR)/extracted/spring-boot-loader/. $(PREP_DIR)/application/
	cp -R $(PREP_DIR)/extracted/snapshot-dependencies/. $(PREP_DIR)/application/
	cp -R $(PREP_DIR)/extracted/application/. $(PREP_DIR)/application/
	cd $(PREP_DIR)/application && java -XX:AOTCacheOutput=app.aot -Dspring.context.exit=onRefresh -jar application.jar

docker2: prep
	docker build \
		-f Dockerfile.runtime \
		-t $(IMAGE2) \
		.

push:
	docker push $(IMAGE)

push2:
	docker push $(IMAGE2)

clean:
	rm -rf $(PREP_DIR)