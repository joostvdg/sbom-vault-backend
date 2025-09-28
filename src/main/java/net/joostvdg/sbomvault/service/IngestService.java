/* (C)2025 */
package net.joostvdg.sbomvault.service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;
import net.joostvdg.sbomvault.model.Artifact;
import net.joostvdg.sbomvault.model.CatalogEntity;
import net.joostvdg.sbomvault.repo.ArtifactRepo;
import net.joostvdg.sbomvault.repo.CatalogEntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IngestService {
  private final ArtifactRepo repo;
  private final CatalogEntityRepository catalogEntityRepo;
  private static final ZoneId ZONE_ID = ZoneId.of("UTC");

  public IngestService(ArtifactRepo repo, CatalogEntityRepository catalogEntityRepo) {
    this.repo = repo;
    this.catalogEntityRepo = catalogEntityRepo;
  }

  @Transactional
  public Artifact upsertArtifact(String entityRef, Artifact dto) {
    // Find or create the catalog entity
    CatalogEntity catalogEntity =
        catalogEntityRepo
            .findById(entityRef)
            .orElseGet(
                () -> {
                  CatalogEntity newEntity = new CatalogEntity();
                  newEntity.setEntityRef(entityRef);
                  newEntity.setKind("component");
                  newEntity.setName(dto.getName());
                  newEntity.setUpdatedAt(OffsetDateTime.now(ZONE_ID));
                  return catalogEntityRepo.save(newEntity);
                });

    // naive upsert by digest if present, else create
    if (dto.getDigest() != null) {
      return repo.findByDigest(dto.getDigest())
          .map(
              existing -> {
                existing.setCatalogEntity(catalogEntity);
                existing.setKind(dto.getKind());
                existing.setName(dto.getName());
                existing.setVersion(dto.getVersion());
                existing.setRegistry(dto.getRegistry());
                existing.setRepository(dto.getRepository());
                existing.setUri(dto.getUri());
                return repo.save(existing);
              })
          .orElseGet(
              () -> {
                dto.setCatalogEntity(catalogEntity);
                if (dto.getId() == null) {
                  dto.setId(UUID.randomUUID());
                }
                if (dto.getCreatedAt() == null) {
                  dto.setCreatedAt(OffsetDateTime.now(ZONE_ID));
                }
                return repo.save(dto);
              });
    } else {
      dto.setCatalogEntity(catalogEntity);
      if (dto.getId() == null) {
        dto.setId(UUID.randomUUID());
      }
      if (dto.getCreatedAt() == null) {
        dto.setCreatedAt(OffsetDateTime.now(ZONE_ID));
      }
      return repo.save(dto);
    }
  }
}
