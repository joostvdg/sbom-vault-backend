set -euo pipefail

APP_NAME="sbom-vault-backend"
JAR="target/${APP_NAME}.jar"

echo "============================================================"
echo "=== Signing JAR and generating SBOM for $JAR"

IMAGE_REPO="ghcr.io/joostvdg/${APP_NAME}"
IMAGE_TAG="${VERSION}"
IMAGE_REF="${IMAGE_REPO}:${IMAGE_TAG}"
echo "=== Image reference: $IMAGE_REF"

REL="release"
mkdir -p "$REL"

GIT_COMMIT="$(git rev-parse HEAD)"
echo "=== Git commit: $GIT_COMMIT"

BUILD_STARTED_ON="$(date -u +%Y-%m-%dT%H:%M:%SZ)"
echo "=== Build started on: $BUILD_STARTED_ON"

echo "=== Calculating JAR checksum"
sha256sum "$REL/$(basename "$JAR")" | tee "$REL/$(basename "$JAR").sha256"

JAR_BASENAME="$(basename "$JAR")"
JAR_SBOM="$REL/${JAR_BASENAME}.cdx.json"

echo "=== Generating SBOM for JAR"
syft "$REL/$JAR_BASENAME" -o cyclonedx-json="$JAR_SBOM"

echo "=== Calculating SBOM checksum"
sha256sum "$JAR_SBOM" | tee "$JAR_SBOM.sha256"

echo "=== Signing JAR with cosign"
cosign sign-blob --key cosign.key --bundle "$REL/${JAR_BASENAME}.sigstore.json" "$REL/$JAR_BASENAME"

echo "=== Signing SBOM with cosign"
cosign sign-blob --key cosign.key --bundle "${JAR_SBOM}.sigstore.json" "$JAR_SBOM"

echo "=== Finished signing JAR and generating SBOM for $JAR"