package com.ing.diba.dl.dalandscape.domain.businesslogic;

import com.ing.diba.dl.dalandscape.domain.model.DaEntity;
import com.ing.diba.dl.dalandscape.domain.model.entities.DaSystemComponent;
import com.ing.diba.dl.dalandscape.domain.model.transition.DaConnectionTransition;
import com.ing.diba.dl.dalandscape.domain.model.transition.DaEntityTransition;
import com.ing.diba.dl.dalandscape.domain.model.transition.DaTransitionSet;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ing.diba.dl.dalandscape.domain.model.DaConnection.Type.CONNECTS_TO;
import static com.ing.diba.dl.dalandscape.domain.model.DaConnection.Type.CONTAINS;
import static com.ing.diba.dl.dalandscape.domain.model.DaConnection.Type.DEPENDS_ON;

/**
 * Finds information about transitions for a given tupel of two systems. These information contains a) the list of all system component
 * entity transitions for each of the two systems, b) the list of all connection transitions of type syscomp depends_on syscomp where the
 * dependent syscomps belong to the from system and the dependee syscomps belong to the to system, and c) the connection transition of the
 * system connects_to system connection between from system and to system.
 */
public class SystemToSystemDependency {
  private final DaTransitionSet transitionSet;
  private final DaEntity fromSystemEntity;
  private final DaEntity toSystemEntity;

  public SystemToSystemDependency(DaTransitionSet transitionSet, DaEntity fromSystemEntity, DaEntity toSystemEntity) {
    this.transitionSet = transitionSet;
    this.fromSystemEntity = fromSystemEntity;
    this.toSystemEntity = toSystemEntity;
  }

  public DaEntity getFromSystemEntity() {
    return fromSystemEntity;
  }

  public DaEntity getToSystemEntity() {
    return toSystemEntity;
  }

  public DaConnectionTransition findFromSystemConnectsToToSystemConnectionTransition() {
    Set<DaConnectionTransition> result = transitionSet.findConnectionTransitions(fromSystemEntity, CONNECTS_TO, toSystemEntity);
    if (result.size() > 1) {
      throw new IllegalStateException(
              "PROGRAMMER'S ERROR: The computer has found more than one DaConnectionTransition. " + result.toString());
    } else {
      return result.iterator().next();
    }
  }

  public Set<DaConnectionTransition> findRelevantSystemContainsSysCompConnTransForFromSystem() {
    // A system contains system component connection transition becomes relevant when its sys comps depend on any of the sys comps of the
    // toSystem. To determine this we collect all the conn trans of the fromSystem, and only if the conn trans's sys comps belong to the
    // ones which depend on the toSystem's system components, we add it to the result.
    Set<DaEntity> relevantSysCompEntities = findSysCompDependsOnSysCompConnectionTransitions().stream()
            .map(DaConnectionTransition::fromEntity).collect(Collectors.toSet());
    return transitionSet.findConnectionTransitions(fromSystemEntity, CONTAINS, DaSystemComponent.class).stream()
            .filter(c -> relevantSysCompEntities.contains(c.toEntity())).collect(Collectors.toSet());
  }

  public Set<DaConnectionTransition> findRelevantSystemContainsSysCompConnTransForToSystem() {
    // A system contains system component connection transition becomes relevant when its sys comps are dependees of any of the
    // sys comps of the fromSystem. To determine this we collect all the conn trans of the toSystem, and only if the conn trans's sys
    // comp belongs to the ones which are dependees of the fromSystem's system components, we add it to the result.
    Set<DaEntity> relevantSysCompEntities = findSysCompDependsOnSysCompConnectionTransitions().stream()
            .map(DaConnectionTransition::toEntity).collect(Collectors.toSet());
    return transitionSet.findConnectionTransitions(toSystemEntity, CONTAINS, DaSystemComponent.class).stream()
            .filter(c -> relevantSysCompEntities.contains(c.toEntity())).collect(Collectors.toSet());
  }

  public Set<DaConnectionTransition> findSysCompDependsOnSysCompConnectionTransitions() {
    // We can find the correct sysComp depends_on sysComp connection transitions like this:
    // Step 1. Find all sysComp entities of fromSystem (the fromSysComp entities), and find all sysComp entities of toSystem (the toSysComp
    // entities)
    // Step 2. For each of the fromSysComp entities find the set of sysCompDependsOnSysComp connection transitions in which the fromSysComp
    // entities are on the "from side"
    // Step 3. From this set of sysCompDependsOnSysComp connection transitions only add these entries to the result set in which the sysComp
    // entity on the "to side" is consisted by the toSystem entity.
    Set<DaConnectionTransition> result = new HashSet<>();

    // Step 1
    Set<DaEntity> fromSysCompEntities = findSysCompEntityTransitionsContainedInSystem(fromSystemEntity).stream()
            .map(DaEntityTransition::getEntity).collect(Collectors.toSet());
    Set<DaEntity> toSysCompEntities = findSysCompEntityTransitionsContainedInSystem(toSystemEntity).stream()
            .map(DaEntityTransition::getEntity).collect(Collectors.toSet());

    // Step 2
    Set<DaConnectionTransition> fromSysCompDependsOnSysCompConnectionTransitions = new HashSet<>();
    for (DaEntity fromSysCompEntity : fromSysCompEntities) {
      Set<DaConnectionTransition> fromSysCompDependsOnSysCompConnTrans = transitionSet
              .findConnectionTransitions(fromSysCompEntity, DEPENDS_ON, DaSystemComponent.class);
      for (DaConnectionTransition fromSysCompDependsOnSysCompConnTran : fromSysCompDependsOnSysCompConnTrans) {
        if (toSysCompEntities.contains(fromSysCompDependsOnSysCompConnTran.toEntity())) {
          // Step 3
          result.add(fromSysCompDependsOnSysCompConnTran);
        }
      }
    }

    return result;
  }

  private Set<DaEntityTransition> findSysCompEntityTransitionsContainedInSystem(DaEntity systemEntity) {
    return transitionSet.findConnectionTransitions(systemEntity, CONTAINS, DaSystemComponent.class).stream()
            .map(c -> transitionSet.findEntityTransitionByEntity(c.toEntity())).collect(Collectors.toSet());
  }

}
