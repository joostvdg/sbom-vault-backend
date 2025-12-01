/* (C)2025 */
package net.joostvdg.sbomvault.controller;

import com.fasterxml.jackson.databind.JsonNode;

public class SbomUploadRequest {
  private String format;
  private String source;
  private String docName;
  private String docVersion;
  private JsonNode jsonb;

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

  public JsonNode getJsonb() {
    return jsonb;
  }

  public void setJsonb(JsonNode jsonb) {
    this.jsonb = jsonb;
  }
}
