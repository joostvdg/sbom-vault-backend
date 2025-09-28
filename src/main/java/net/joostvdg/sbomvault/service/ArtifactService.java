/* (C)2025 */
package net.joostvdg.sbomvault.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.joostvdg.sbomvault.repo.*;
import org.springframework.stereotype.Service;

@Service
public class ArtifactService {

  private final ArtifactRepo artifactRepo;
  private final SbomRepository sbomRepo;
  private final SignatureRepository signatureRepo;
  private final ChecksumRepository checksumRepo;
  private final AttestationRepository attestationRepo;
  private final ArtifactVerificationRepository verificationRepo;

  public ArtifactService(
      ArtifactRepo artifactRepo,
      SbomRepository sbomRepo,
      SignatureRepository signatureRepo,
      ChecksumRepository checksumRepo,
      AttestationRepository attestationRepo,
      ArtifactVerificationRepository verificationRepo) {
    this.artifactRepo = artifactRepo;
    this.sbomRepo = sbomRepo;
    this.signatureRepo = signatureRepo;
    this.checksumRepo = checksumRepo;
    this.attestationRepo = attestationRepo;
    this.verificationRepo = verificationRepo;
  }

  public Optional<Map<String, Object>> getArtifactDetails(String artifactUri) {
    // Parse registry and repository from URI
    // Find the artifact and return its details with related entities
    // Implementation depends on your data model

    // Example implementation
    String[] parts = artifactUri.split("/", 2);
    String registry = parts[0];
    String path = parts.length > 1 ? parts[1] : "";

    return artifactRepo
        .findByRegistryAndRepository(registry, path)
        .map(
            artifact -> {
              Map<String, Object> details = new HashMap<>();
              details.put("artifact", artifact);
              details.put("sboms", sbomRepo.findByArtifactId(artifact.getId()));
              details.put("signatures", signatureRepo.findByArtifactId(artifact.getId()));
              details.put("checksums", checksumRepo.findByArtifactId(artifact.getId()));
              details.put("attestations", attestationRepo.findByArtifactId(artifact.getId()));
              details.put("verifications", verificationRepo.findByArtifactId(artifact.getId()));
              return details;
            });
  }
}
