/* (C)2025 */
package net.joostvdg.sbomvault.repo;

import java.util.List;
import java.util.UUID;
import net.joostvdg.sbomvault.model.ArtifactAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtifactAuditRepository extends JpaRepository<ArtifactAudit, UUID> {
  List<ArtifactAudit> findByArtifactIdOrderByVersionDesc(UUID artifactId);

  List<ArtifactAudit> findByChangedBy(String changedBy);

  List<ArtifactAudit> findByPublicKeyFingerprint(String fingerprint);
}
