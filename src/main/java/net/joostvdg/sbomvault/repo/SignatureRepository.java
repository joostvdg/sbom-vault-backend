/* (C)2025 */
package net.joostvdg.sbomvault.repo;

import java.util.List;
import java.util.UUID;
import net.joostvdg.sbomvault.model.Signature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SignatureRepository extends JpaRepository<Signature, UUID> {
  List<Signature> findByArtifactId(UUID artifactId);
}
