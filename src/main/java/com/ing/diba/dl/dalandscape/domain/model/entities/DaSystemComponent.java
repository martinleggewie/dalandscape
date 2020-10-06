package com.ing.diba.dl.dalandscape.domain.model.entities;

import com.ing.diba.dl.dalandscape.domain.model.DaEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "dasystemcomponents")
public class DaSystemComponent extends DaEntity {

  DaSystemComponent() {
    // empty constructor needed because this is unfortunately a JPA requirement
  }

  public DaSystemComponent(String key, String displayName) {
    super(key, displayName);
  }

  @Override
  public String toString() {
    return "DaSystemComponent{} " + super.toString();
  }
}
