package com.ing.diba.dl.dalandscape.domain.model.entities;

import com.ing.diba.dl.dalandscape.domain.model.DaEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "dasystems")
public class DaSystem extends DaEntity {

  private String type;

  DaSystem() {
    // empty constructor needed because this is unfortunately a JPA requirement
    super();
  }

  public DaSystem(String key, String displayName, String type) {
    super(key, displayName);
    this.type = type;
  }

  public String getType() {
    return type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof DaSystem))
      return false;
    if (!super.equals(o))
      return false;
    DaSystem that = (DaSystem) o;
    return type.equals(that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), type);
  }

  @Override
  public String toString() {
    return "DaSystem{" + "type='" + type + '\'' + "} " + super.toString();
  }
}
