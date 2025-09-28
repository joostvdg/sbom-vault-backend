/* (C)2025 */
package net.joostvdg.sbomvault.repo;

import java.util.Optional;
import java.util.UUID;
import net.joostvdg.sbomvault.model.Artifact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArtifactRepo extends JpaRepository<Artifact, UUID> {
  Optional<Artifact> findByDigest(String digest);

  Page<Artifact> findByNameContainingIgnoreCase(String name, Pageable p);

  /**
   * Find an artifact by registry and repository path
   *
   * @param registry the registry name
   * @param repository the repository path
   * @return the artifact if found
   */
  @Query("SELECT a FROM Artifact a WHERE a.registry = :registry AND a.repository = :repository")
  Optional<Artifact> findByRegistryAndRepository(
      @Param("registry") String registry, @Param("repository") String repository);
}
