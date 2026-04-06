# Release Bundle

## Setup Cosign

```sh
export COSIGN_PASSWORD=''
cosign generate-key-pair
```

## Image Provenance

```sh
export VERSION=0.1.0
export APP_NAME=sbom-vault-backend
export IMAGE_DIGEST=$(docker build -t $APP_NAME:$VERSION . | tail -n 1 | awk '{print $3}')
export IMAGE_REPO=ghcr.io/joostvdg/$APP_NAME
export IMAGE_REF=ghcr.io/joostvdg/sbom-vault-backend:0.1.0-runtime
```

Optional:

```sh
export COSIGN_PASSWORD=''
```

```sh
./image-provenance.sh
```