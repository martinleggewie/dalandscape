package com.ing.diba.dl.dalandscape.domain.model.transition;

import java.util.Objects;

public abstract class DaTransition {

  private final State state;

  public DaTransition(State state) {
    this.state = state;
  }

  public State getState() {
    return state;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof DaTransition))
      return false;
    DaTransition that = (DaTransition) o;
    return state == that.state;
  }

  @Override
  public int hashCode() {
    return Objects.hash(state);
  }

  @Override
  public String toString() {
    return "DaTransition{" + "state=" + state + '}';
  }

  public enum State {
    STAYS_UNCHANGED,
    WILL_BE_ADDED,
    WILL_BE_REMOVED
  }
}
