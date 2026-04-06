set -euo pipefail


APP_NAME="sbom-vault-backend"
JAR="target/${APP_NAME}.jar"

IMAGE_REPO="ghcr.io/joostvdg/${APP_NAME}"
IMAGE_TAG="${VERSION}"
IMAGE_REF="${IMAGE_REPO}:${IMAGE_TAG}"

REL="release"
mkdir -p "$REL"

GIT_COMMIT="$(git rev-parse HEAD)"
BUILD_STARTED_ON="$(date -u +%Y-%m-%dT%H:%M:%SZ)"

sha256sum "$REL/$(basename "$JAR")" | tee "$REL/$(basename "$JAR").sha256"
JAR_BASENAME="$(basename "$JAR")"
JAR_SBOM="$REL/${JAR_BASENAME}.cdx.json"

syft "$REL/$JAR_BASENAME" -o cyclonedx-json="$JAR_SBOM"
sha256sum "$JAR_SBOM" | tee "$JAR_SBOM.sha256"

cosign sign-blob --key cosign.key --bundle "$REL/${JAR_BASENAME}.sigstore.json" "$REL/$JAR_BASENAME"
cosign sign-blob --key cosign.key --bundle "${JAR_SBOM}.sigstore.json" "$JAR_SBOM"


IMAGE_DIGEST="$(docker buildx imagetools inspect "$IMAGE_REF" --format '{{json .Manifest.Digest}}' | tr -d '"')"
IMAGE_DIGEST_REF="${IMAGE_REPO}@${IMAGE_DIGEST}"

cosign sign --key cosign.key "$IMAGE_DIGEST_REF"

IMAGE_SBOM="$REL/${APP_NAME}-${VERSION}.image.cdx.json"
syft "$IMAGE_DIGEST_REF" -o cyclonedx-json="$IMAGE_SBOM"
sha256sum "$IMAGE_SBOM" | tee "$IMAGE_SBOM.sha256"
cosign sign-blob --key cosign.key --bundle "${IMAGE_SBOM}.sigstore.json" "$IMAGE_SBOM"

IMAGE_PROV="$REL/${APP_NAME}-${VERSION}.image.provenance.json"