package com.example.sbomvault.service;

import com.example.sbomvault.model.Artifact;
import com.example.sbomvault.repo.ArtifactRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class VerificationService {
  private final ArtifactRepo artifactRepo;

  public VerificationService(ArtifactRepo repo){ this.artifactRepo = repo; }

  @Transactional
  public void verifyWithCosign(UUID artifactId){
    Artifact art = artifactRepo.findById(artifactId).orElseThrow();
    // Stub: call cosign here if available
    try {
      Process p = new ProcessBuilder(List.of("bash","-lc","echo cosign verify " + art.getUri())).start();
      String out = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
      p.waitFor();
    } catch(Exception ignored){}
  }
}
