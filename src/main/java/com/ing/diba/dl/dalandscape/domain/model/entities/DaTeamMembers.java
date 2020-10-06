package com.ing.diba.dl.dalandscape.domain.model.entities;


import com.ing.diba.dl.dalandscape.domain.model.DaEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "dateammembers")
public class DaTeamMembers extends DaEntity {

  private String role;

  public DaTeamMembers() {
    // empty constructor needed because this is unfortunately a JPA requirement
  }

  public DaTeamMembers(String key, String displayName, String role) {
    super(key, displayName);
    this.role = role;
  }

  public String getRole() {
    return role;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof DaTeamMembers))
      return false;
    if (!super.equals(o))
      return false;
    DaTeamMembers that = (DaTeamMembers) o;
    return role.equals(that.role);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), role);
  }

  @Override
  public String toString() {
    return "DaTeamMembers{" + "role='" + role + '\'' + "} " + super.toString();
  }
}
