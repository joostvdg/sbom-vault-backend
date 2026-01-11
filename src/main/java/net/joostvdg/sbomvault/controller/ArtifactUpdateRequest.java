/* (C)2025 */
package net.joostvdg.sbomvault.controller;

import java.util.Map;

public class ArtifactUpdateRequest {
  private Map<String, String> tags;
  private Map<String, Object> labels;
  private String reason;
  private String changedBy;
  private String signature;
  private String publicKey;
  private String publicKeyFingerprint;
  private String signingKeyType;

  // Getters and setters
  public Map<String, String> getTags() {
    return tags;
  }

  public void setTags(Map<String, String> tags) {
    this.tags = tags;
  }

  public Map<String, Object> getLabels() {
    return labels;
  }

  public void setLabels(Map<String, Object> labels) {
    this.labels = labels;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public String getChangedBy() {
    return changedBy;
  }

  public void setChangedBy(String changedBy) {
    this.changedBy = changedBy;
  }

  public String getSignature() {
    return signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }

  public String getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(String publicKey) {
    this.publicKey = publicKey;
  }

  public String getPublicKeyFingerprint() {
    return publicKeyFingerprint;
  }

  public void setPublicKeyFingerprint(String publicKeyFingerprint) {
    this.publicKeyFingerprint = publicKeyFingerprint;
  }

  public String getSigningKeyType() {
    return signingKeyType;
  }

  public void setSigningKeyType(String signingKeyType) {
    this.signingKeyType = signingKeyType;
  }
}
