package com.example.sbomvault.service;

import com.example.sbomvault.model.Artifact;
import com.example.sbomvault.repo.ArtifactRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IngestService {
  private final ArtifactRepo repo;
  public IngestService(ArtifactRepo repo){ this.repo = repo; }

  @Transactional
  public Artifact upsertArtifact(String entityRef, Artifact dto){
    // naive upsert by digest if present, else create
    if(dto.getDigest()!=null){
      return repo.findByDigest(dto.getDigest()).map(existing -> {
        existing.setEntityRef(entityRef);
        existing.setKind(dto.getKind());
        existing.setName(dto.getName());
        existing.setVersion(dto.getVersion());
        existing.setRegistry(dto.getRegistry());
        existing.setRepository(dto.getRepository());
        existing.setUri(dto.getUri());
        return repo.save(existing);
      }).orElseGet(() -> {
        dto.setEntityRef(entityRef);
        return repo.save(dto);
      });
    } else {
      dto.setEntityRef(entityRef);
      return repo.save(dto);
    }
  }
}
