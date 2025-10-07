/* (C)2025 */
package net.joostvdg.sbomvault.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "artifact")
public class Artifact {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @Column(name = "catalog_reference", nullable = false)
  private String catalogReference;

  @Column(name = "kind", nullable = false)
  private String kind;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "version")
  private String artifactVersion;

  @Column(name = "digest", unique = true)
  private String digest;

  @Column(name = "registry")
  private String registry;

  @Column(name = "repository")
  private String repository;

  @Column(name = "uri")
  private String uri;

  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  @OneToMany(mappedBy = "artifact", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Sbom> sboms;

  @OneToMany(mappedBy = "artifact", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Signature> signatures;

  @OneToMany(mappedBy = "artifact", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Checksum> checksums;

  @OneToMany(mappedBy = "artifact", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Attestation> attestations;

  @OneToMany(mappedBy = "artifact", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<VerificationRun> verificationRuns;

  @OneToMany(mappedBy = "artifact", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<ArtifactVerification> verifications;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getCatalogReference() {
    return catalogReference;
  }

  public void setCatalogReference(String catalogReference) {
    this.catalogReference = catalogReference;
  }

  public String getKind() {
    return kind;
  }

  public void setKind(String kind) {
    this.kind = kind;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getArtifactVersion() {
    return artifactVersion;
  }

  public void setArtifactVersion(String version) {
    this.artifactVersion = version;
  }

  public String getDigest() {
    return digest;
  }

  public void setDigest(String digest) {
    this.digest = digest;
  }

  public String getRegistry() {
    return registry;
  }

  public void setRegistry(String registry) {
    this.registry = registry;
  }

  public String getRepository() {
    return repository;
  }

  public void setRepository(String repository) {
    this.repository = repository;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public Set<Sbom> getSboms() {
    return sboms;
  }

  public void setSboms(Set<Sbom> sboms) {
    this.sboms = sboms;
  }

  public Set<Signature> getSignatures() {
    return signatures;
  }

  public void setSignatures(Set<Signature> signatures) {
    this.signatures = signatures;
  }

  public Set<Checksum> getChecksums() {
    return checksums;
  }

  public void setChecksums(Set<Checksum> checksums) {
    this.checksums = checksums;
  }

  public Set<Attestation> getAttestations() {
    return attestations;
  }

  public void setAttestations(Set<Attestation> attestations) {
    this.attestations = attestations;
  }

  public Set<VerificationRun> getVerificationRuns() {
    return verificationRuns;
  }

  public void setVerificationRuns(Set<VerificationRun> verificationRuns) {
    this.verificationRuns = verificationRuns;
  }

  public Set<ArtifactVerification> getVerifications() {
    return verifications;
  }

  public void setVerifications(Set<ArtifactVerification> verifications) {
    this.verifications = verifications;
  }

  @Override
  public String toString() {
    return "Artifact{"
        + "id="
        + id
        + ", catalog_reference="
        + catalogReference
        + ", kind='"
        + kind
        + '\''
        + ", name='"
        + name
        + '\''
        + ", version='"
        + artifactVersion
        + '\''
        + ", digest='"
        + digest
        + '\''
        + ", registry='"
        + registry
        + '\''
        + ", repository='"
        + repository
        + '\''
        + ", uri='"
        + uri
        + '\''
        + ", createdAt="
        + createdAt
        + ", sboms="
        + sboms
        + ", signatures="
        + signatures
        + ", checksums="
        + checksums
        + ", attestations="
        + attestations
        + ", verificationRuns="
        + verificationRuns
        + ", verifications="
        + verifications
        + '}';
  }
}
