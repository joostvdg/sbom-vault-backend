/* (C)2025 */
package net.joostvdg.sbomvault.controller;

import java.util.Map;

public class ArtifactCreateRequest {
  private String catalogReference;
  private String kind;
  private String name;
  private String artifactVersion;
  private String digest;
  private String registry;
  private String repository;
  private String uri;
  private Map<String, Object> labels;

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

  public void setArtifactVersion(String artifactVersion) {
    this.artifactVersion = artifactVersion;
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

  public Map<String, Object> getLabels() {
    return labels;
  }

  public void setLabels(Map<String, Object> labels) {
    this.labels = labels;
  }
}
