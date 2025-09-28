/* (C)2025 */
package net.joostvdg.sbomvault.repo;

import java.util.List;
import java.util.UUID;
import net.joostvdg.sbomvault.model.VerificationRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationRunRepository extends JpaRepository<VerificationRun, UUID> {
  List<VerificationRun> findByArtifactId(UUID artifactId);
}
