package com.ing.diba.dl.dalandscape.domain.businesslogic;

import com.ing.diba.dl.dalandscape.domain.model.DaEntity;

import java.util.Objects;

public class ConsistencyCheckWarning {
  private final Type type;
  private final DaEntity entity;

  public ConsistencyCheckWarning(Type type, DaEntity entity) {
    this.type = type;
    this.entity = entity;
  }

  public Type getType() {
    return type;
  }

  public DaEntity getEntity() {
    return entity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    ConsistencyCheckWarning warning = (ConsistencyCheckWarning) o;
    return type == warning.type && entity.equals(warning.entity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, entity);
  }

  @Override
  public String toString() {
    return "ConsistencyCheckWarning{" + "type=" + type + ", entity=" + entity + '}';
  }

  public enum Type {
    SYSTEM_DOES_NOT_CONTAIN_ANY_SYSTEMCOMPONENT,
    SYSTEM_DOES_NOT_HAVE_ANY_RESPONSIBLE_TEAM,
    SYSTEMCOMPONENT_DOES_NOT_BELONG_TO_ANY_SYSTEM,
    SYSTEMCOMPONENT_DOES_NOT_HAVE_ANY_RESPONSIBLE_TEAM
  }
}
