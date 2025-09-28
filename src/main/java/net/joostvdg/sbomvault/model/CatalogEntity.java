/* (C)2025 */
package net.joostvdg.sbomvault.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "catalog_entity")
public class CatalogEntity {

  @Id
  @Column(name = "entity_ref")
  private String entityRef;

  @Column(name = "kind", nullable = false)
  private String kind;

  @Column(name = "namespace")
  private String namespace;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "title")
  private String title;

  @Column(name = "owner_ref")
  private String ownerRef;

  @Column(name = "system_ref")
  private String systemRef;

  @Column(name = "relations_jsonb")
  @JdbcTypeCode(SqlTypes.JSON)
  private Object relationsJsonb;

  @Column(name = "raw_entity_jsonb")
  @JdbcTypeCode(SqlTypes.JSON)
  private Object rawEntityJsonb;

  @Column(name = "updated_at", nullable = false, updatable = false)
  private OffsetDateTime updatedAt;

  public String getEntityRef() {
    return entityRef;
  }

  public void setEntityRef(String entityRef) {
    this.entityRef = entityRef;
  }

  public String getKind() {
    return kind;
  }

  public void setKind(String kind) {
    this.kind = kind;
  }

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getOwnerRef() {
    return ownerRef;
  }

  public void setOwnerRef(String ownerRef) {
    this.ownerRef = ownerRef;
  }

  public String getSystemRef() {
    return systemRef;
  }

  public void setSystemRef(String systemRef) {
    this.systemRef = systemRef;
  }

  public Object getRelationsJsonb() {
    return relationsJsonb;
  }

  public void setRelationsJsonb(Object relationsJsonb) {
    this.relationsJsonb = relationsJsonb;
  }

  public Object getRawEntityJsonb() {
    return rawEntityJsonb;
  }

  public void setRawEntityJsonb(Object rawEntityJsonb) {
    this.rawEntityJsonb = rawEntityJsonb;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
