package com.example.sbomvault.controller;

import com.example.sbomvault.model.Artifact;
import com.example.sbomvault.service.IngestService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1")
public class IngestController {
  private final IngestService ingest;
  public IngestController(IngestService svc){ this.ingest = svc; }

  @PostMapping(value="/artifacts", consumes = MediaType.APPLICATION_JSON_VALUE)
  public Artifact upsertArtifact(@RequestParam String entityRef, @RequestBody Artifact dto){
    return ingest.upsertArtifact(entityRef, dto);
  }
}
