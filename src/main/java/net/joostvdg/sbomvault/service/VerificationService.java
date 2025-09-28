/* (C)2025 */
package net.joostvdg.sbomvault.service;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import net.joostvdg.sbomvault.model.Artifact;
import net.joostvdg.sbomvault.model.ArtifactVerification;
import net.joostvdg.sbomvault.repo.ArtifactRepo;
import net.joostvdg.sbomvault.repo.ArtifactVerificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VerificationService {
  private final ArtifactRepo artifactRepo;
  private final ArtifactVerificationRepository verificationRepo;

  private static final ZoneId ZONE_ID = ZoneId.of("UTC");

  private final Logger log = LoggerFactory.getLogger(VerificationService.class);

  public VerificationService(
      ArtifactRepo artifactRepo, ArtifactVerificationRepository verificationRepo) {
    this.artifactRepo = artifactRepo;
    this.verificationRepo = verificationRepo;
  }

  @Transactional
  public void verifyWithCosign(UUID artifactId) throws Exception {
    log.info("Verifying artifact {} with cosign", artifactId);
    Artifact artifact = artifactRepo.findById(artifactId).orElseThrow();

    // Execute cosign verification
    Process p =
        new ProcessBuilder(List.of("bash", "-lc", "cosign verify " + artifact.getUri())).start();
    String out = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    int exitCode = p.waitFor();

    // Find or create verification record
    ArtifactVerification verification =
        verificationRepo
            .findByArtifact(artifact)
            .orElseGet(
                () -> {
                  ArtifactVerification newVerification = new ArtifactVerification();
                  newVerification.setId(UUID.randomUUID());
                  newVerification.setArtifact(artifact);
                  newVerification.setVerificationTool("cosign");
                  newVerification.setVerificationMethod("signature");
                  return newVerification;
                });

    // Process the results
    if (exitCode == 0) {
      log.info("Cosign verification successful for artifact {}", artifactId);
      verification.setVerified(true);
    } else {
      log.warn("Cosign verification failed for artifact {}: {}", artifactId, out);
      verification.setVerified(false);
    }

    verification.setVerificationOutput(out);
    verification.setLastVerified(java.time.OffsetDateTime.now(ZONE_ID));
    verificationRepo.save(verification);
  }
}
