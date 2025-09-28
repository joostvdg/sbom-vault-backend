/* (C)2025 */
package net.joostvdg.sbomvault.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "artifact_verification")
public class ArtifactVerification {

  @Id @GeneratedValue private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "artifact_id", nullable = false)
  private Artifact artifact;

  @Column(name = "verified")
  private Boolean verified;

  @Column(name = "verification_tool")
  private String verificationTool;

  @Column(name = "verification_method")
  private String verificationMethod;

  @Column(name = "verification_output")
  private String verificationOutput;

  @Column(name = "verification_timestamp")
  private OffsetDateTime verificationTimestamp;

  @Column(name = "last_verified")
  private OffsetDateTime lastVerified;

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

  public Boolean getVerified() {
    return verified;
  }

  public void setVerified(Boolean verified) {
    this.verified = verified;
  }

  public String getVerificationTool() {
    return verificationTool;
  }

  public void setVerificationTool(String verificationTool) {
    this.verificationTool = verificationTool;
  }

  public String getVerificationMethod() {
    return verificationMethod;
  }

  public void setVerificationMethod(String verificationMethod) {
    this.verificationMethod = verificationMethod;
  }

  public String getVerificationOutput() {
    return verificationOutput;
  }

  public void setVerificationOutput(String verificationOutput) {
    this.verificationOutput = verificationOutput;
  }

  public OffsetDateTime getVerificationTimestamp() {
    return verificationTimestamp;
  }

  public void setVerificationTimestamp(OffsetDateTime verificationTimestamp) {
    this.verificationTimestamp = verificationTimestamp;
  }

  public OffsetDateTime getLastVerified() {
    return lastVerified;
  }

  public void setLastVerified(OffsetDateTime lastVerified) {
    this.lastVerified = lastVerified;
  }
}
