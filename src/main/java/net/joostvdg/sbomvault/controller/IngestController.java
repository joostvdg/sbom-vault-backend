/* (C)2025 */
package net.joostvdg.sbomvault.controller;

import net.joostvdg.sbomvault.model.Artifact;
import net.joostvdg.sbomvault.service.IngestService;
import org.slf4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ingest")
public class IngestController {
  private final IngestService ingest;
  private static final Logger log = org.slf4j.LoggerFactory.getLogger(IngestController.class);

  public IngestController(IngestService svc) {
    this.ingest = svc;
  }

  @PostMapping(value = "/artifacts", consumes = MediaType.APPLICATION_JSON_VALUE)
  public Artifact upsertArtifact(
      @RequestParam("entityRef") String entityRef, @RequestBody Artifact artifact) {
    log.info("Upserting artifact: {} (ref: {})", artifact, entityRef);
    return ingest.upsertArtifact(entityRef, artifact);
  }
}
