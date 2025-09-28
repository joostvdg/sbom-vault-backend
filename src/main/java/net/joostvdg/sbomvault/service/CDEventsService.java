/* (C)2025 */
package net.joostvdg.sbomvault.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.UUID;
import net.joostvdg.sbomvault.model.Artifact;
import net.joostvdg.sbomvault.model.CatalogEntity;
import net.joostvdg.sbomvault.repo.ArtifactRepo;
import net.joostvdg.sbomvault.repo.CatalogEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CDEventsService {
  private final ObjectMapper om = new ObjectMapper();
  private final ArtifactRepo repo;
  private final CatalogEntityRepository catalogEntityRepo;

  private final Logger log = LoggerFactory.getLogger(CDEventsService.class);

  public CDEventsService(ArtifactRepo repo, CatalogEntityRepository catalogEntityRepo) {
    this.repo = repo;
    this.catalogEntityRepo = catalogEntityRepo;
  }

  @Transactional
  public void handleStructured(JsonNode cloudEvent) {
    // Expect CloudEvents v1.0 envelope; extract 'type' and 'data'
    String type = cloudEvent.path("type").asText("");
    JsonNode data = cloudEvent.path("data");
    process(type, data);
  }

  @Transactional
  public void handleBinary(Map<String, String> headers, byte[] body) {
    String type = headers.getOrDefault("ce-type", "");
    JsonNode data = om.nullNode();
    if (body != null && body.length > 0) {
      try {
        data = om.readTree(new String(body, StandardCharsets.UTF_8));
      } catch (Exception e) {
        log.warn("Could not handle binary: {}", e.getMessage());
      }
    }
    process(type, data);
  }

  private void process(String type, JsonNode data) {
    // Minimal demo: upsert artifact on artifact.* events
    var zoneId = ZoneId.of("UTC");

    if (type.startsWith("dev.cdevents.artifact.")) {
      String digest = data.path("artifact").path("digest").asText(null);
      String name = data.path("artifact").path("name").asText("unknown");
      String entityRef =
          data.path("producer").path("component").asText("component:default/unknown");
      String kind = data.path("artifact").path("kind").asText("oci");
      String uri = data.path("artifact").path("uri").asText(null);

      // First, find or create the catalog entity
      CatalogEntity catalogEntity =
          catalogEntityRepo
              .findById(entityRef)
              .orElseGet(
                  () -> {
                    CatalogEntity newEntity = new CatalogEntity();
                    newEntity.setEntityRef(entityRef);
                    // Set minimum required fields for a new CatalogEntity
                    newEntity.setKind("component");
                    newEntity.setName(name);
                    // Default values for other required fields
                    newEntity.setUpdatedAt(OffsetDateTime.now(zoneId));
                    return catalogEntityRepo.save(newEntity);
                  });

      // Now handle the artifact
      Artifact artifact =
          repo.findByDigest(digest)
              .orElseGet(
                  () -> {
                    Artifact newArtifact = new Artifact();
                    newArtifact.setId(UUID.randomUUID()); // Generate UUID for new artifacts
                    newArtifact.setCreatedAt(OffsetDateTime.now(zoneId));
                    return newArtifact;
                  });

      // Update artifact properties
      artifact.setDigest(digest);
      artifact.setName(name);
      artifact.setKind(kind);
      artifact.setCatalogEntity(catalogEntity); // Now we set the proper entity object
      artifact.setUri(uri);

      repo.save(artifact);

      // Handle additional event-specific data based on type
      if (type.equals("dev.cdevents.artifact.published")) {
        // Handle artifact publication specifics
        // e.g., registry, repository info
        artifact.setRegistry(data.path("artifact").path("registry").asText(null));
        artifact.setRepository(data.path("artifact").path("repository").asText(null));
        repo.save(artifact);
      } else if (type.equals("dev.cdevents.artifact.signed")) {
        // Handle signature information - could delegate to a signature service
        // This is just a placeholder for where signature handling would go
      }
    }
    // Extend here for build.finished/pipeline.run.finished, etc.
  }
}
