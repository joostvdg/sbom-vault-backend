set -eu pipefail

REL="release"
mkdir -p "$REL"

echo "============================================================"
echo "=== Signing image and generating SBOM for $IMAGE_REF"

IMAGE_DIGEST="$(docker buildx imagetools inspect "$IMAGE_REF" --format '{{json .Manifest.Digest}}' | tr -d '"')"
IMAGE_DIGEST_REF="${IMAGE_REPO}@${IMAGE_DIGEST}"
echo "=== Image digest: $IMAGE_DIGEST_REF"

echo "=== Signing image digest with cosign"
cosign sign --key cosign.key "$IMAGE_DIGEST_REF"

echo "=== Generating SBOM for image digest"
IMAGE_SBOM="$REL/${APP_NAME}-${VERSION}.image.cdx.json"
syft "$IMAGE_DIGEST_REF" -o cyclonedx-json="$IMAGE_SBOM"

echo "=== Calculating SBOM checksum"
sha256sum "$IMAGE_SBOM" | tee "$IMAGE_SBOM.sha256"

echo "=== Signing SBOM with cosign"
cosign sign-blob --key cosign.key --bundle "${IMAGE_SBOM}.sigstore.json" "$IMAGE_SBOM"

echo "=== Generating provenance for image digest"
IMAGE_PROV="$REL/${APP_NAME}-${VERSION}.image.provenance.json"
docker buildx imagetools inspect "$IMAGE_REF" \
  --format '{{ json .Provenance.SLSA }}' \
  > "$IMAGE_PROV"

echo "=== Calculating provenance checksum"
sha256sum "$IMAGE_PROV" | tee "$IMAGE_PROV.sha256"

echo "=== Signing provenance with cosign"
cosign sign-blob --key cosign.key --bundle "${IMAGE_PROV}.sigstore.json" "$IMAGE_PROV"

echo "=== Finished signing image and generating SBOM for $IMAGE_REF"
echo "============================================================"