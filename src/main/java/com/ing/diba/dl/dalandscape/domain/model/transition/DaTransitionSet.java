package com.ing.diba.dl.dalandscape.domain.model.transition;

import com.ing.diba.dl.dalandscape.domain.model.DaConnection;
import com.ing.diba.dl.dalandscape.domain.model.DaEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DaTransitionSet {
  private final String key;
  private final String displayName;
  private final Set<DaTransition> transitions = new HashSet<>();

  public DaTransitionSet(String key, String displayName) {
    this.key = key;
    this.displayName = displayName;
  }

  public String getKey() {
    return key;
  }

  public String getDisplayName() {
    return displayName;
  }

  public Set<DaTransition> getTransitions() {
    return transitions;
  }

  public Set<DaEntityTransition> findEntityTransitionsByEntityType(Class<? extends DaEntity> clazz) {
    return findEntityTransitions().stream().filter(e -> e.getEntity().getClass().equals(clazz)).collect(Collectors.toSet());
  }

  public DaEntityTransition findEntityTransitionByEntity(DaEntity entity) {
    Set<DaEntityTransition> result = findEntityTransitions().stream().filter(e -> e.getEntity().equals(entity)).collect(Collectors.toSet());
    if (result.size() > 1) {
      throw new IllegalStateException(
              "PROGRAMMER'S ERROR: The computer has found more than one DaEntityTransition for a given DaEntity. " + result.toString());
    } else {
      return result.iterator().next();
    }
  }

  public Set<DaEntityTransition> findEntityTransitions() {
    return transitions.stream().filter(t -> t instanceof DaEntityTransition).map(t -> (DaEntityTransition) t).collect(Collectors.toSet());
  }

  public Set<DaConnectionTransition> findConnectionTransitions(DaEntity fromEntity, DaConnection.Type connectionType,
                                                               Class<? extends DaEntity> toEntityClazz) {
    return findConnectionTransitions().stream().filter(c -> c.fromEntity().equals(fromEntity)).filter(c -> c.type().equals(connectionType))
            .filter(c -> c.toEntity().getClass().equals(toEntityClazz)).collect(Collectors.toSet());
  }

  public Set<DaConnectionTransition> findConnectionTransitions(DaEntity fromEntity, DaConnection.Type connectionType, DaEntity toEntity) {
    return findConnectionTransitions().stream().filter(c -> c.fromEntity().equals(fromEntity)).filter(c -> c.type().equals(connectionType))
            .filter(c -> c.toEntity().equals(toEntity)).collect(Collectors.toSet());
  }

  public Set<DaConnectionTransition> findConnectionTransitions(Class<? extends DaEntity> fromEntityClazz, DaConnection.Type connectionType,
                                                               Class<? extends DaEntity> toEntityClazz) {
    return findConnectionTransitions().stream().filter(c -> c.fromEntity().getClass().equals(fromEntityClazz))
            .filter(c -> c.type().equals(connectionType)).filter(c -> c.toEntity().getClass().equals(toEntityClazz))
            .collect(Collectors.toSet());
  }

  public Set<DaConnectionTransition> findConnectionTransitions(Class<? extends DaEntity> fromEntityClazz, DaConnection.Type connectionType,
                                                               DaEntity toEntity) {
    return findConnectionTransitions().stream().filter(c -> c.fromEntity().getClass().equals(fromEntityClazz))
            .filter(c -> c.type().equals(connectionType)).filter(c -> c.toEntity().equals(toEntity)).collect(Collectors.toSet());
  }

  public Set<DaConnectionTransition> findConnectionTransitions() {
    return transitions.stream().filter(t -> t instanceof DaConnectionTransition).map(t -> (DaConnectionTransition) t)
            .collect(Collectors.toSet());
  }

}
