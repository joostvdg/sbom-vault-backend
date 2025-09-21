package com.example.sbomvault.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonType;
import org.hibernate.annotations.Type;

@Entity @Table(name = "sbom")
public class Sbom {
  @Id @GeneratedValue private UUID id;
  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "artifact_id") private Artifact artifact;
  private String format;
  private String source;
  private String docName;
  private String docVersion;
  @Type(JsonType.class) @Column(columnDefinition = "jsonb") private JsonNode jsonb;
  @CreationTimestamp private Instant createdAt;
  public UUID getId(){return id;}
  public Artifact getArtifact(){return artifact;}
  public void setArtifact(Artifact a){this.artifact=a;}
}

@Entity @Table(name = "attestation")
class Attestation {
  @Id @GeneratedValue private UUID id;
  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "artifact_id") private Artifact artifact;
  private String type;
  private String predicateType;
  private String issuer;
  @Type(JsonType.class) @Column(columnDefinition = "jsonb") private JsonNode jsonb;
  @CreationTimestamp private Instant createdAt;
}

@Entity @Table(name = "signature")
class Signature {
  @Id @GeneratedValue private UUID id;
  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "artifact_id") private Artifact artifact;
  private String signer; private String keyId; private String certificatePem; private String transparencyLogId;
  @Lob private byte[] rawSigBytes;
  @CreationTimestamp private Instant createdAt;
}

@Entity @Table(name = "checksum")
class Checksum {
  @Id @GeneratedValue private UUID id;
  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "artifact_id") private Artifact artifact;
  private String algo; private String value;
  @CreationTimestamp private Instant createdAt;
}

@Entity @Table(name = "verification_run")
class VerificationRun {
  @Id @GeneratedValue private UUID id;
  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "artifact_id") private Artifact artifact;
  private String tool; private String policy; private String result;
  @Type(JsonType.class) @Column(columnDefinition = "jsonb") private JsonNode detailsJsonb;
  private Instant startedAt; private Instant finishedAt;
}
