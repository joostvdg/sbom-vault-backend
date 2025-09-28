/* (C)2025 */
package net.joostvdg.sbomvault.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "attestation")
public class Attestation {

  @Id @GeneratedValue private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "artifact_id", nullable = false)
  private Artifact artifact;

  @Column(name = "type", nullable = false)
  private String type;

  @Column(name = "predicate_type")
  private String predicateType;

  @Column(name = "issuer")
  private String issuer;

  @Column(name = "jsonb", nullable = false)
  @JdbcTypeCode(SqlTypes.JSON)
  private Object jsonb;

  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Artifact getArtifact() {
    return artifact;
  }

  public void setArtifact(Artifact artifact) {
    this.artifact = artifact;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getPredicateType() {
    return predicateType;
  }

  public void setPredicateType(String predicateType) {
    this.predicateType = predicateType;
  }

  public String getIssuer() {
    return issuer;
  }

  public void setIssuer(String issuer) {
    this.issuer = issuer;
  }

  public Object getJsonb() {
    return jsonb;
  }

  public void setJsonb(Object jsonb) {
    this.jsonb = jsonb;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
