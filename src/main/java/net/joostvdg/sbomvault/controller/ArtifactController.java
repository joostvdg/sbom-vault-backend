/* (C)2025 */
package net.joostvdg.sbomvault.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.joostvdg.sbomvault.model.Artifact;
import net.joostvdg.sbomvault.service.ArtifactService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/artifacts")
public class ArtifactController {

  private final ArtifactService artifactService;

  public ArtifactController(ArtifactService artifactService) {
    this.artifactService = artifactService;
  }

  @GetMapping
  public ResponseEntity<List<Artifact>> getAllArtifacts() {
    List<Artifact> artifacts = artifactService.getAllArtifacts();
    return ResponseEntity.ok(artifacts);
  }

  @GetMapping("/{registry}/{path:.+}")
  public ResponseEntity<?> getArtifact(@PathVariable String registry, @PathVariable String path) {
    String artifactId = registry + "/" + path;
    return artifactService
        .getArtifactDetails(artifactId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<Map<String, String>> createArtifact(
      @RequestBody ArtifactCreateRequest req) {
    var saved = artifactService.createArtifact(req);
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(saved.getId())
            .toUri();
    Map<String, String> body = Map.of("id", saved.getId().toString(), "url", location.toString());
    return ResponseEntity.created(location).body(body);
  }

  @PostMapping(value = "/{id}/sboms", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, String>> uploadSbom(
      @PathVariable("id") UUID id, @RequestBody SbomUploadRequest req) {
    var saved = artifactService.addSbomToArtifact(id, req);
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{sbomId}")
            .buildAndExpand(saved.getId())
            .toUri();
    Map<String, String> body = Map.of("id", saved.getId().toString(), "url", location.toString());
    return ResponseEntity.created(location).body(body);
  }
}
