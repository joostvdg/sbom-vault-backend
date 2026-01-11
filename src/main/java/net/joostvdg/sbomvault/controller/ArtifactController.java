/* (C)2025 */
package net.joostvdg.sbomvault.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.joostvdg.sbomvault.model.Artifact;
import net.joostvdg.sbomvault.model.ArtifactAudit;
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
      @RequestBody ArtifactCreateRequest req, HttpServletRequest request) {

    String changedBy = request.getHeader("X-Changed-By");
    String signature = request.getHeader("X-Signature");
    String publicKey = request.getHeader("X-Public-Key");
    String publicKeyFingerprint = request.getHeader("X-Public-Key-Fingerprint");
    String signingKeyType = request.getHeader("X-Signing-Key-Type");
    String clientIp = request.getRemoteAddr();
    String userAgent = request.getHeader("User-Agent");

    var saved =
        artifactService.createArtifactWithAudit(
            req,
            changedBy,
            signature,
            publicKey,
            publicKeyFingerprint,
            signingKeyType,
            clientIp,
            userAgent);

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

  @PatchMapping("/{id}")
  public ResponseEntity<Artifact> updateArtifact(
      @PathVariable UUID id, @RequestBody ArtifactUpdateRequest req, HttpServletRequest request) {
    String clientIp = request.getRemoteAddr();
    String userAgent = request.getHeader("User-Agent");
    Artifact updated = artifactService.updateArtifact(id, req, clientIp, userAgent);
    return ResponseEntity.ok(updated);
  }

  @GetMapping("/{id}/audit")
  public ResponseEntity<List<ArtifactAudit>> getAuditHistory(@PathVariable UUID id) {
    return ResponseEntity.ok(artifactService.getArtifactAuditHistory(id));
  }
}
