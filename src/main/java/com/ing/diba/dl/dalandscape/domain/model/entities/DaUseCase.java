package com.ing.diba.dl.dalandscape.domain.model.entities;

import com.ing.diba.dl.dalandscape.domain.model.DaEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "dausecases")
public class DaUseCase extends DaEntity {

  DaUseCase() {
    // empty constructor needed because this is unfortunately a JPA requirement
  }

  public DaUseCase(String key, String displayName) {
    super(key, displayName);
  }

  @Override
  public String toString() {
    return "DaUseCase{} " + super.toString();
  }
}
