/* (C)2025 */
package net.joostvdg.sbomvault.service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;
import net.joostvdg.sbomvault.model.Artifact;
import net.joostvdg.sbomvault.repo.ArtifactRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IngestService {
  private final ArtifactRepo repo;
  private static final ZoneId ZONE_ID = ZoneId.of("UTC");

  public IngestService(ArtifactRepo repo) {
    this.repo = repo;
  }

  @Transactional
  public Artifact upsertArtifact(String entityRef, Artifact dto) {

    // naive upsert by digest if present, else create
    if (dto.getDigest() != null) {
      return repo.findByDigest(dto.getDigest())
          .map(
              existing -> {
                existing.setCatalogReference(dto.getCatalogReference());
                existing.setKind(dto.getKind());
                existing.setName(dto.getName());
                existing.setArtifactVersion(dto.getArtifactVersion());
                existing.setRegistry(dto.getRegistry());
                existing.setRepository(dto.getRepository());
                existing.setUri(dto.getUri());
                return repo.save(existing);
              })
          .orElseGet(
              () -> {
                if (dto.getId() == null) {
                  dto.setId(UUID.randomUUID());
                }
                if (dto.getCreatedAt() == null) {
                  dto.setCreatedAt(OffsetDateTime.now(ZONE_ID));
                }
                return repo.save(dto);
              });
    } else {
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
