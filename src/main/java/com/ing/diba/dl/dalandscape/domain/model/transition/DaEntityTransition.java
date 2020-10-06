package com.ing.diba.dl.dalandscape.domain.model.transition;

import com.ing.diba.dl.dalandscape.domain.model.DaEntity;

import java.util.Objects;

public class DaEntityTransition extends DaTransition {

  private final DaEntity entity;

  public DaEntityTransition(DaEntity entity, State state) {
    super(state);
    this.entity = entity;
  }

  public DaEntity getEntity() {
    return entity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof DaEntityTransition))
      return false;
    if (!super.equals(o))
      return false;
    DaEntityTransition that = (DaEntityTransition) o;
    return entity.equals(that.entity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), entity);
  }

  @Override
  public String toString() {
    return "DaEntityTransition{" + "entity=" + entity + "} " + super.toString();
  }
}
