package com.ing.diba.dl.dalandscape.domain.model.transition;

import com.ing.diba.dl.dalandscape.domain.model.DaConnection;
import com.ing.diba.dl.dalandscape.domain.model.DaEntity;

import java.util.Objects;

public class DaConnectionTransition extends DaTransition {

  private final DaConnection connection;

  public DaConnectionTransition(DaConnection connection, State state) {
    super(state);
    this.connection = connection;
  }

  public DaEntity fromEntity() {
    return connection.getFromEntity();
  }

  public DaEntity toEntity() {
    return connection.getToEntity();
  }

  public DaConnection.Type type() {
    return connection.getType();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof DaConnectionTransition))
      return false;
    if (!super.equals(o))
      return false;
    DaConnectionTransition that = (DaConnectionTransition) o;
    return connection.equals(that.connection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), connection);
  }

  @Override
  public String toString() {
    return "DaConnectionTransition{" + "connection=" + connection + "} " + super.toString();
  }
}
