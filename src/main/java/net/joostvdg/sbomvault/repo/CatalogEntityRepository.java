/* (C)2025 */
package net.joostvdg.sbomvault.repo;

import net.joostvdg.sbomvault.model.CatalogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatalogEntityRepository extends JpaRepository<CatalogEntity, String> {}
