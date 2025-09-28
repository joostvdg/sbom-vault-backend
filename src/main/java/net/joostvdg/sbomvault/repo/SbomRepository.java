/* (C)2025 */
package net.joostvdg.sbomvault.repo;

import java.util.List;
import java.util.UUID;
import net.joostvdg.sbomvault.model.Sbom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SbomRepository extends JpaRepository<Sbom, UUID> {
  List<Sbom> findByArtifactId(UUID artifactId);
}
