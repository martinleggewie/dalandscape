package com.ing.diba.dl.dalandscape.domain.model.entities;

import com.ing.diba.dl.dalandscape.domain.model.DaEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "dausecasegroups")
public class DaUseCaseGroup extends DaEntity {

  DaUseCaseGroup() {
    // empty constructor needed because this is unfortunately a JPA requirement
  }

  public DaUseCaseGroup(String key, String displayName) {
    super(key, displayName);
  }

  @Override
  public String toString() {
    return "DaUseCaseGroup{} " + super.toString();
  }
}
