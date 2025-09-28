/* (C)2025 */
package net.joostvdg.sbomvault.repo;

import java.util.Optional;
import java.util.UUID;
import net.joostvdg.sbomvault.model.Artifact;
import net.joostvdg.sbomvault.model.ArtifactVerification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtifactVerificationRepository extends JpaRepository<ArtifactVerification, UUID> {
  Optional<ArtifactVerification> findByArtifact(Artifact artifact);

  Optional<ArtifactVerification> findByArtifactId(UUID artifactId);
}
