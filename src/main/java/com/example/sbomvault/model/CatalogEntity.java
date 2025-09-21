package com.example.sbomvault.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name = "catalog_entity")
public class CatalogEntity {
  @Id
  private String entityRef; // PK

  private String kind;
  private String namespace_;
  private String name;
  private String title;
  private String ownerRef;
  private String systemRef;

  @Lob
  @Column(columnDefinition = "jsonb")
  private String relationsJsonb;

  @Lob
  @Column(columnDefinition = "jsonb")
  private String rawEntityJsonb;

  private Instant updatedAt;

  // getters/setters omitted for brevity
  public String getEntityRef(){return entityRef;}
  public void setEntityRef(String r){this.entityRef=r;}
}
