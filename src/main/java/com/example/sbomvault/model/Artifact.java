package com.example.sbomvault.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;

@Entity @Table(name = "artifact")
public class Artifact {
  @Id @GeneratedValue
  private UUID id;

  // Backstage entityRef, e.g., component:default/my-service
  private String entityRef;

  private String kind; // oci|file|maven|npm|pypi
  private String name;
  private String version;

  @Column(unique = true)
  private String digest; // sha256:...

  private String registry;
  private String repository;
  private String uri;

  @CreationTimestamp
  private Instant createdAt;

  // getters/setters
  public UUID getId() { return id; }
  public String getEntityRef() { return entityRef; }
  public void setEntityRef(String r) { this.entityRef = r; }
  public String getKind() { return kind; }
  public void setKind(String k) { this.kind = k; }
  public String getName() { return name; }
  public void setName(String n) { this.name = n; }
  public String getVersion() { return version; }
  public void setVersion(String v) { this.version = v; }
  public String getDigest() { return digest; }
  public void setDigest(String d) { this.digest = d; }
  public String getRegistry() { return registry; }
  public void setRegistry(String r) { this.registry = r; }
  public String getRepository() { return repository; }
  public void setRepository(String r) { this.repository = r; }
  public String getUri() { return uri; }
  public void setUri(String u) { this.uri = u; }
  public Instant getCreatedAt() { return createdAt; }
}
