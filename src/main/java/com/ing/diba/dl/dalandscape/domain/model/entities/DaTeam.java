package com.ing.diba.dl.dalandscape.domain.model.entities;

import com.ing.diba.dl.dalandscape.domain.model.DaEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "dateams")
public class DaTeam extends DaEntity {

  DaTeam() {
    // empty constructor needed because this is unfortunately a JPA requirement
  }

  public DaTeam(String key, String displayName) {
    super(key, displayName);
  }

  @Override
  public String toString() {
    return "DaTeam{} " + super.toString();
  }
}
