/* (C)2025 */
package net.joostvdg.sbomvault.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.joostvdg.sbomvault.controller.SbomUploadRequest;
import net.joostvdg.sbomvault.model.Artifact;
import net.joostvdg.sbomvault.model.Sbom;
import net.joostvdg.sbomvault.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

  public java.util.List<net.joostvdg.sbomvault.model.Artifact> getAllArtifacts() {
    return artifactRepo.findAll();
  }

  public net.joostvdg.sbomvault.model.Artifact createArtifact(
      net.joostvdg.sbomvault.controller.ArtifactCreateRequest req) {
    net.joostvdg.sbomvault.model.Artifact a = new net.joostvdg.sbomvault.model.Artifact();
    a.setCatalogReference(req.getCatalogReference());
    a.setKind(req.getKind());
    a.setName(req.getName());
    a.setArtifactVersion(req.getArtifactVersion());
    a.setDigest(req.getDigest());
    a.setRegistry(req.getRegistry());
    a.setRepository(req.getRepository());
    // prefer explicit uri if provided, otherwise derive from registry/repository
    if (req.getUri() != null && !req.getUri().isBlank()) {
      a.setUri(req.getUri());
    } else if (req.getRegistry() != null && req.getRepository() != null) {
      a.setUri(req.getRegistry() + "/" + req.getRepository());
    }
    a.setLabels(req.getLabels()); // null is fine; @PrePersist will set empty map
    return artifactRepo.save(a);
  }

  @Transactional
  public Sbom addSbomToArtifact(UUID artifactId, SbomUploadRequest req) {
    Artifact artifact =
        artifactRepo
            .findById(artifactId)
            .orElseThrow(() -> new IllegalArgumentException("Artifact not found: " + artifactId));
    Sbom sbom = new Sbom();
    sbom.setArtifact(artifact);
    sbom.setFormat(req.getFormat());
    sbom.setSource(req.getSource());
    sbom.setDocName(req.getDocName());
    sbom.setDocVersion(req.getDocVersion());

    ObjectMapper om = new ObjectMapper();
    String jsonString;
    try {
      jsonString = om.writeValueAsString(req.getJsonb());
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Invalid JSON for sbom", e);
    }
    sbom.setJsonb(jsonString);
    return sbomRepo.save(sbom);
  }
}
