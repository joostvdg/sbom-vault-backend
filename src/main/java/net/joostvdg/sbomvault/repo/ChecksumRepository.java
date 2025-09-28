/* (C)2025 */
package net.joostvdg.sbomvault.repo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.joostvdg.sbomvault.model.Checksum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChecksumRepository extends JpaRepository<Checksum, UUID> {
  List<Checksum> findByArtifactId(UUID artifactId);

  Optional<Checksum> findByArtifactIdAndAlgo(UUID artifactId, String algo);
}
