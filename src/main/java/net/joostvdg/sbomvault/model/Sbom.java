/* (C)2025 */
package net.joostvdg.sbomvault.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "sbom")
public class Sbom {

  @Id @GeneratedValue private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "artifact_id", nullable = false)
  private Artifact artifact;

  @Column(name = "format", nullable = false)
  private String format;

  @Column(name = "source")
  private String source;

  @Column(name = "doc_name")
  private String docName;

  @Column(name = "doc_version")
  private String docVersion;

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

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getDocName() {
    return docName;
  }

  public void setDocName(String docName) {
    this.docName = docName;
  }

  public String getDocVersion() {
    return docVersion;
  }

  public void setDocVersion(String docVersion) {
    this.docVersion = docVersion;
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
