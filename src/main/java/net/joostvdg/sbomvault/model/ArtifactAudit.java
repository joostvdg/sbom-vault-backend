/* (C)2025 */
package net.joostvdg.sbomvault.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "artifact_audit")
public class ArtifactAudit {

  @Id @GeneratedValue @UuidGenerator private UUID id;

  @Column(name = "artifact_id", nullable = false)
  private UUID artifactId;

  @Column(name = "version", nullable = false)
  private Long version;

  @Column(name = "operation", nullable = false)
  private String operation; // CREATE, UPDATE, DELETE

  @Column(name = "changed_by", nullable = false)
  private String changedBy;

  @Column(name = "changed_at", nullable = false)
  private OffsetDateTime changedAt;

  @Column(name = "signature", nullable = false, columnDefinition = "TEXT")
  private String signature;

  @Column(name = "public_key_fingerprint", nullable = false)
  private String publicKeyFingerprint;

  @Column(name = "public_key", nullable = false, columnDefinition = "TEXT")
  private String publicKey;

  @Column(name = "signing_key_type", nullable = false)
  private String signingKeyType;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "field_changes", nullable = false)
  private Map<String, Object> fieldChanges;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "artifact_snapshot", nullable = false)
  private Map<String, Object> artifactSnapshot;

  @Column(name = "client_ip")
  private String clientIp;

  @Column(name = "user_agent")
  private String userAgent;

  @Column(name = "reason")
  private String reason;

  @PrePersist
  public void onCreate() {
    if (changedAt == null) {
      changedAt = OffsetDateTime.now(Defaults.ZONE_ID);
    }
  }

  // Getters and setters
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getArtifactId() {
    return artifactId;
  }

  public void setArtifactId(UUID artifactId) {
    this.artifactId = artifactId;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  public String getOperation() {
    return operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }

  public String getChangedBy() {
    return changedBy;
  }

  public void setChangedBy(String changedBy) {
    this.changedBy = changedBy;
  }

  public OffsetDateTime getChangedAt() {
    return changedAt;
  }

  public void setChangedAt(OffsetDateTime changedAt) {
    this.changedAt = changedAt;
  }

  public String getSignature() {
    return signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }

  public String getPublicKeyFingerprint() {
    return publicKeyFingerprint;
  }

  public void setPublicKeyFingerprint(String publicKeyFingerprint) {
    this.publicKeyFingerprint = publicKeyFingerprint;
  }

  public String getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(String publicKey) {
    this.publicKey = publicKey;
  }

  public String getSigningKeyType() {
    return signingKeyType;
  }

  public void setSigningKeyType(String signingKeyType) {
    this.signingKeyType = signingKeyType;
  }

  public Map<String, Object> getFieldChanges() {
    return fieldChanges;
  }

  public void setFieldChanges(Map<String, Object> fieldChanges) {
    this.fieldChanges = fieldChanges;
  }

  public Map<String, Object> getArtifactSnapshot() {
    return artifactSnapshot;
  }

  public void setArtifactSnapshot(Map<String, Object> artifactSnapshot) {
    this.artifactSnapshot = artifactSnapshot;
  }

  public String getClientIp() {
    return clientIp;
  }

  public void setClientIp(String clientIp) {
    this.clientIp = clientIp;
  }

  public String getUserAgent() {
    return userAgent;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }
}
