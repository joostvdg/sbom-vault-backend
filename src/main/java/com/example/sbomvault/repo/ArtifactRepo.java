package com.example.sbomvault.repo;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sbomvault.model.Artifact;

public interface ArtifactRepo extends JpaRepository<Artifact, UUID> {
  Optional<Artifact> findByDigest(String digest);
  Page<Artifact> findByNameContainingIgnoreCase(String name, Pageable p);
}
