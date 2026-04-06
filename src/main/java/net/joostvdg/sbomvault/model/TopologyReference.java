/* (C)2025 */
package net.joostvdg.sbomvault.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

@Entity
@Table(
    name = "topology_reference",
    uniqueConstraints = @UniqueConstraint(columnNames = {"system", "external_id"}))
public class TopologyReference {

  @Id
  @GeneratedValue
  @UuidGenerator
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "system", nullable = false)
  private String system;

  @Column(name = "external_id", nullable = false)
  private String externalId;

  @Column(name = "reference")
  private String reference;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "raw_jsonb")
  private Map<String, Object> raw;

  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  @ManyToMany(mappedBy = "topologyReferences")
  private Set<Artifact> artifacts;

  @PrePersist
  public void onCreate() {
    if (createdAt == null) {
      createdAt = OffsetDateTime.now(Defaults.ZONE_ID);
    }
  }

  // getters and setters
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getSystem() {
    return system;
  }

  public void setSystem(String system) {
    this.system = system;
  }

  public String getExternalId() {
    return externalId;
  }

  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }

  public Map<String, Object> getRaw() {
    return raw;
  }

  public void setRaw(Map<String, Object> raw) {
    this.raw = raw;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public Set<Artifact> getArtifacts() {
    return artifacts;
  }

  public void setArtifacts(Set<Artifact> artifacts) {
    this.artifacts = artifacts;
  }

  @Override
  public String toString() {
    return "TopologyReference{"
        + "id="
        + id
        + ", system='"
        + system
        + '\''
        + ", externalId='"
        + externalId
        + '\''
        + ", reference='"
        + reference
        + '\''
        + ", raw="
        + raw
        + ", createdAt="
        + createdAt
        + ", artifacts="
        + artifacts
        + '}';
  }
}
