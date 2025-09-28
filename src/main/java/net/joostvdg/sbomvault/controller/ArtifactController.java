/* (C)2025 */
package net.joostvdg.sbomvault.controller;

import net.joostvdg.sbomvault.service.ArtifactService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/artifacts")
public class ArtifactController {

  private final ArtifactService artifactService;

  public ArtifactController(ArtifactService artifactService) {
    this.artifactService = artifactService;
  }

  @GetMapping("/{registry}/{path:.+}")
  public ResponseEntity<?> getArtifact(@PathVariable String registry, @PathVariable String path) {
    String artifactId = registry + "/" + path;
    return artifactService
        .getArtifactDetails(artifactId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
}
