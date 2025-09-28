/* (C)2025 */
package net.joostvdg.sbomvault.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "signature")
public class Signature {

  @Id @GeneratedValue private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "artifact_id", nullable = false)
  private Artifact artifact;

  @Column(name = "signer")
  private String signer;

  @Column(name = "key_id")
  private String keyId;

  @Column(name = "certificate_pem")
  private String certificatePem;

  @Column(name = "transparency_log_id")
  private String transparencyLogId;

  @Column(name = "raw_sig_bytes")
  private byte[] rawSigBytes;

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

  public String getSigner() {
    return signer;
  }

  public void setSigner(String signer) {
    this.signer = signer;
  }

  public String getKeyId() {
    return keyId;
  }

  public void setKeyId(String keyId) {
    this.keyId = keyId;
  }

  public String getCertificatePem() {
    return certificatePem;
  }

  public void setCertificatePem(String certificatePem) {
    this.certificatePem = certificatePem;
  }

  public String getTransparencyLogId() {
    return transparencyLogId;
  }

  public void setTransparencyLogId(String transparencyLogId) {
    this.transparencyLogId = transparencyLogId;
  }

  public byte[] getRawSigBytes() {
    return rawSigBytes;
  }

  public void setRawSigBytes(byte[] rawSigBytes) {
    this.rawSigBytes = rawSigBytes;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
