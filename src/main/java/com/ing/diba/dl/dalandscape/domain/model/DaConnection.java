package com.ing.diba.dl.dalandscape.domain.model;

import java.util.Objects;

public class DaConnection {

  private final DaEntity fromEntity;
  private final DaEntity toEntity;
  private final Type type;

  public DaConnection(DaEntity fromEntity, Type type, DaEntity toEntity) {
    this.fromEntity = fromEntity;
    this.toEntity = toEntity;
    this.type = type;
  }

  public DaEntity getFromEntity() {
    return fromEntity;
  }

  public DaEntity getToEntity() {
    return toEntity;
  }

  public Type getType() {
    return type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof DaConnection))
      return false;
    DaConnection that = (DaConnection) o;
    return fromEntity.equals(that.fromEntity) && toEntity.equals(that.toEntity) && type == that.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(fromEntity, toEntity, type);
  }

  @Override
  public String toString() {
    return "DaConnection{" + "fromEntity=" + fromEntity + ", toEntity=" + toEntity + ", type=" + type + '}';
  }

  public enum Type {
    CONNECTS_TO,
    CONTAINS,
    DEPENDS_ON,
    IMPLEMENTS,
    RESPONSIBLE_FOR,
    USES
  }
}
