package com.ing.diba.dl.dalandscape.domain.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Objects;

@MappedSuperclass
public abstract class DaEntity {

  @Id
  private String key;

  @Column(name = "displayname")
  private String displayName;

  public DaEntity() {
    // empty constructor needed because this is unfortunately a JPA requirement
  }

  public DaEntity(String key, String displayName) {
    this.key = key;
    this.displayName = displayName;
  }

  public String getKey() {
    return key;
  }

  public String getDisplayName() {
    return displayName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof DaEntity))
      return false;
    DaEntity that = (DaEntity) o;
    return key.equals(that.key) && displayName.equals(that.displayName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, displayName);
  }

  @Override
  public String toString() {
    return "DaEntity{" + "key='" + key + '\'' + ", displayName='" + displayName + '\'' + '}';
  }
}
