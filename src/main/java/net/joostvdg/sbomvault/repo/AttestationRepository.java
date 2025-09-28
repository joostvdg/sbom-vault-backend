/* (C)2025 */
package net.joostvdg.sbomvault.repo;

import java.util.List;
import java.util.UUID;
import net.joostvdg.sbomvault.model.Attestation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttestationRepository extends JpaRepository<Attestation, UUID> {
  List<Attestation> findByArtifactId(UUID artifactId);
}
