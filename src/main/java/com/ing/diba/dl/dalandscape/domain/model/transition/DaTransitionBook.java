package com.ing.diba.dl.dalandscape.domain.model.transition;

import com.ing.diba.dl.dalandscape.domain.model.DaConnection;
import com.ing.diba.dl.dalandscape.domain.model.DaEntity;
import com.ing.diba.dl.dalandscape.domain.model.entities.DaBoundary;
import com.ing.diba.dl.dalandscape.domain.model.entities.DaSystem;
import com.ing.diba.dl.dalandscape.domain.model.entities.DaSystemComponent;
import com.ing.diba.dl.dalandscape.domain.model.entities.DaTeam;
import com.ing.diba.dl.dalandscape.domain.model.entities.DaUseCase;
import com.ing.diba.dl.dalandscape.domain.model.entities.DaUseCaseGroup;
import com.ing.diba.dl.dalandscape.domain.model.entities.DaUser;
import com.ing.diba.dl.dalandscape.domain.model.storyboard.DaScene;
import com.ing.diba.dl.dalandscape.domain.model.storyboard.DaStoryboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DaTransitionBook {

  private final DaStoryboard storyboard;
  private final Set<TransitionDefinition> transitionDefinitions;
  private final Set<DaEntity> entities;

  public DaTransitionBook(DaStoryboard storyboard, Set<TransitionDefinition> transitionDefinitions, Set<DaEntity> entities) {
    this.storyboard = storyboard;
    this.transitionDefinitions = transitionDefinitions;
    this.entities = entities;
  }

  public String key() {
    return storyboard.getKey();
  }

  public String displayName() {
    return storyboard.getDisplayName();
  }

  public List<DaTransitionSet> createTransitionSets() {
    List<DaTransitionSet> result = new ArrayList<>();

    DaTransitionSet previousIntermediateTransitionSet = null;
    for (DaScene scene : storyboard.getScenes()) {
      Set<TransitionDefinition> currentTransitionDefinitions = findTransitionDefinitionsForScene(transitionDefinitions, scene.getKey());

      // Before we start, let's define something important - the intermediate transition set: A transition set which only
      // contains "STAYS_UNCHANGED" transitions. In the following implementation, we calculate such an intermediate transition set
      // by taking a previous intermediate transition set and apply the transition definitions on it.
      if (previousIntermediateTransitionSet == null) {
        // 1. In case the previous intermediate transition set is null, calculate current intermediate transition set from the list of
        // current transition definitions. Then add this current intermediate transition set to result
        DaTransitionSet currentIntermediateTransitionSet = calculateIntermediateTransitionSet(previousIntermediateTransitionSet,
                currentTransitionDefinitions, scene);
        result.add(currentIntermediateTransitionSet);
        previousIntermediateTransitionSet = currentIntermediateTransitionSet;
      } else {
        // 2. In case the previous intermediate transition set is not null, then we need to create two more transition sets:

        // 2.1. Create a current normal transition set, based on the previous intermediate transition set and the set of current transition
        // definitions. Add this one to the result.
        DaTransitionSet currentNormalTransitionSet = calculateNormalTransitionSet(previousIntermediateTransitionSet,
                currentTransitionDefinitions, scene);
        result.add(currentNormalTransitionSet);

        // 2.2. Create the next intermediate transition set out of the current normal transition set. Add this one also to the result. And
        // assign the previous intermediate transition set to this new current intermediate transition set.
        DaTransitionSet nextIntermediateTransitionSet = calculateIntermediateTransitionSet(previousIntermediateTransitionSet,
                currentTransitionDefinitions, scene);
        result.add(nextIntermediateTransitionSet);

        previousIntermediateTransitionSet = nextIntermediateTransitionSet;
      }
    }

    return result;
  }

  private DaTransitionSet calculateNormalTransitionSet(DaTransitionSet baseTransitionSet, Set<TransitionDefinition> transitionDefinitions,
                                                       DaScene scene) {
    DaTransitionSet result = new DaTransitionSet(baseTransitionSet.getKey() + "2" + scene.getKey(),
            baseTransitionSet.getDisplayName() + " --> " + scene.getDisplayName());

    // copy everything from the base transition set to the current one because the transition definitions only contain the delta
    // corresponding to the current scene
    baseTransitionSet.getTransitions().forEach(result.getTransitions()::add);

    // And now either add the new transition definition as a TO_BE_ADDED transition, or in case it is a REMOVE transition definition, first
    // remove the existing STAYS_UNCHANGED transition from the base transition and then add the new transition definition as a
    // TO_BE_REMOVED transition
    for (TransitionDefinition transitionDefinition : transitionDefinitions) {
      if (transitionDefinition.getKey().getTransitionType().equals(TransitionDefinition.TransitionType.ADD)) {
        DaTransition toBeAddedTransition = createTransitionFromTransitionDefinition(transitionDefinition, false);
        result.getTransitions().add(toBeAddedTransition);
      } else {
        DaTransition staysUnchangedTransition = createTransitionFromTransitionDefinition(transitionDefinition, true);
        result.getTransitions().remove(staysUnchangedTransition);
        DaTransition toBeRemovedTransition = createTransitionFromTransitionDefinition(transitionDefinition, false);
        result.getTransitions().add(toBeRemovedTransition);
      }
    }

    // Before we can return the result, there is some post-processing needed:
    //
    // We need to find out which entities have been (implicitly) added after we have added all current transition definitions.
    // We need to add all entities which were implicitly defined because they were part of a connection. For this, we collect all
    // explicitly known entities in a lookup set, and only when we find a new entity in one of the connections, we will add the new
    // entity as an explicit entity transition.
    //
    // A little bit tricky is how to interprete if a given entity will be removed when there are only connection transitions and no
    // explicit entity transitions defined in the transition definitions. How should we handle the fact when all connections for a given
    // entity have been removed, leaving an entity without any connections? Our answer here is the following: In this case we will still
    // keep the entity. If the person who models the transition definitions wants to have the entity removed, she needs to also
    // explicitly add this as a transition definition with transition type "REMOVE" and connection type "NN".
    //
    Set<DaEntity> entities = result.getTransitions().stream().filter(t -> t instanceof DaEntityTransition).map(t -> (DaEntityTransition) t)
            .map(DaEntityTransition::getEntity).collect(Collectors.toSet());

    Set<DaConnectionTransition> connectionTransitions = result.getTransitions().stream().filter(t -> t instanceof DaConnectionTransition)
            .map(t -> (DaConnectionTransition) t).collect(Collectors.toSet());
    for (DaConnectionTransition connectionTransition : connectionTransitions) {
      if (!entities.contains(connectionTransition.fromEntity())) {
        result.getTransitions().add(new DaEntityTransition(connectionTransition.fromEntity(), DaTransition.State.WILL_BE_ADDED));
      }
      if (!entities.contains(connectionTransition.toEntity())) {
        result.getTransitions().add(new DaEntityTransition(connectionTransition.toEntity(), DaTransition.State.WILL_BE_ADDED));
      }
    }

    return result;
  }

  private DaTransitionSet calculateIntermediateTransitionSet(DaTransitionSet baseTransitionSet,
                                                             Set<TransitionDefinition> transitionDefinitions, DaScene scene) {
    DaTransitionSet result = new DaTransitionSet(scene.getKey(), scene.getDisplayName());

    // copy everything from the base transition set to the current one because the transition definitions only contain the delta
    // corresponding to the current scene
    if (baseTransitionSet != null) {
      baseTransitionSet.getTransitions().forEach(result.getTransitions()::add);
    }

    // And now add the new transitions as intermediate transitions or remove already existing intermediate transitions in case the new
    // transition is a REMOVE transition.
    for (TransitionDefinition transitionDefinition : transitionDefinitions) {
      if (transitionDefinition.getKey().getTransitionType().equals(TransitionDefinition.TransitionType.ADD)) {
        DaTransition toBeAddedTransition = createTransitionFromTransitionDefinition(transitionDefinition, true);
        result.getTransitions().add(toBeAddedTransition);
      } else {
        DaTransition staysUnchangedTransition = createTransitionFromTransitionDefinition(transitionDefinition, true);
        result.getTransitions().remove(staysUnchangedTransition);
      }
    }

    // Before we can return the result, we need to add entity transitions for all implicitly defined entities. An entity might have been
    // defined implicitly when it was part of a connection transition but not explicitly defined as an entity transition.
    Set<DaConnectionTransition> connectionTransitions = result.getTransitions().stream().filter(t -> t instanceof DaConnectionTransition)
            .map(t -> (DaConnectionTransition) t).collect(Collectors.toSet());
    for (DaConnectionTransition connectionTransition : connectionTransitions) {
      result.getTransitions().add(new DaEntityTransition(connectionTransition.fromEntity(), DaTransition.State.STAYS_UNCHANGED));
      result.getTransitions().add(new DaEntityTransition(connectionTransition.toEntity(), DaTransition.State.STAYS_UNCHANGED));
    }

    return result;
  }

  private Set<TransitionDefinition> findTransitionDefinitionsForScene(Set<TransitionDefinition> transitionDefinitions, String sceneKey) {
    return transitionDefinitions.stream().filter(t -> t.getKey().getSceneKey().equals(sceneKey)).collect(Collectors.toSet());
  }

  private DaTransition createTransitionFromTransitionDefinition(TransitionDefinition transitionDefinition, boolean intermediate) {
    DaTransition.State transitionState = calculateTransitionStateFromTransitionDefinition(transitionDefinition, intermediate);
    TransitionDefinition.Key key = transitionDefinition.getKey();
    if (key.getConnectionType().equals(TransitionDefinition.ConnectionType.NN)) {
      DaEntity entity = createEntity(key.getFromEntityType(), key.getFromEntityKey());
      return new DaEntityTransition(entity, transitionState);
    } else {
      DaEntity fromEntity = createEntity(key.getFromEntityType(), key.getFromEntityKey());
      DaEntity toEntity = createEntity(key.getToEntityType(), key.getToEntityKey());
      DaConnection.Type connectionType = mapFrom(key.getConnectionType());
      return new DaConnectionTransition(new DaConnection(fromEntity, connectionType, toEntity), transitionState);
    }
  }

  private DaTransition.State calculateTransitionStateFromTransitionDefinition(TransitionDefinition transitionDefinition,
                                                                              boolean intermediate) {
    if (intermediate) {
      return DaTransition.State.STAYS_UNCHANGED;
    } else if (transitionDefinition.getKey().getTransitionType().equals(TransitionDefinition.TransitionType.ADD)) {
      return DaTransition.State.WILL_BE_ADDED;
    } else {
      return DaTransition.State.WILL_BE_REMOVED;
    }
  }

  private DaEntity createEntity(TransitionDefinition.EntityType entityType, String entityKey) {
    // We should find the to-be-created entity in the set provided from the outside. If we cannot find any entity, then we gracefully
    // create one. But this one will only have the key set correctly, whereas the displayName is set to contain an error message. And any
    // entity-specific property will be set to a default value. So, dear user of this class, better provide properly defined entities.
    for (DaEntity entity : entities) {
      if (entity.getKey().equals(entityKey)) {
        switch (entityType) {
          case BOUNDARY:
            if (entity.getClass().equals(DaBoundary.class)) {
              return entity;
            }
          case SYSTEM:
            if (entity.getClass().equals(DaSystem.class)) {
              return entity;
            }
          case SYSTEMCOMPONENT:
            if (entity.getClass().equals(DaSystemComponent.class)) {
              return entity;
            }
          case USECASE:
            if (entity.getClass().equals(DaUseCase.class)) {
              return entity;
            }
          case USECASEGROUP:
            if (entity.getClass().equals(DaUseCaseGroup.class)) {
              return entity;
            }
          case USER:
            if (entity.getClass().equals(DaUser.class)) {
              return entity;
            }
          case TEAM:
            if (entity.getClass().equals(DaTeam.class)) {
              return entity;
            }
          default:
            throw new IllegalArgumentException("Unknown entity type found: " + entityType + " (in entity " + entity + ")");
        }
      }
    }

    // If we come to this point, then we didn't find any entity yet. So we have to create one with default values.
    switch (entityType) {
      case BOUNDARY:
        return new DaBoundary(entityKey, entityKey + " (missing boundary definition)");
      case SYSTEM:
        return new DaSystem(entityKey, entityKey + " (missing system definition)", "application");
      case SYSTEMCOMPONENT:
        return new DaSystemComponent(entityKey, entityKey + " (missing system component definition)");
      case USER:
        return new DaUser(entityKey, entityKey + " (missing user definition)");
      default:
        throw new IllegalArgumentException("Unknown entity type found: " + entityType);
    }
  }

  private DaConnection.Type mapFrom(TransitionDefinition.ConnectionType type) {
    switch (type) {
      case CONNECTS_TO:
        return DaConnection.Type.CONNECTS_TO;
      case CONTAINS:
        return DaConnection.Type.CONTAINS;
      case DEPENDS_ON:
        return DaConnection.Type.DEPENDS_ON;
      case IMPLEMENTS:
        return DaConnection.Type.IMPLEMENTS;
      case RESPONSIBLE_FOR:
        return DaConnection.Type.RESPONSIBLE_FOR;
      case USES:
        return DaConnection.Type.USES;
      default:
        throw new IllegalArgumentException("Not allowed connection type found: " + type);
    }
  }

  @Override
  public String toString() {
    return "DaTransitionBook{" + "storyboard=" + storyboard + ", transitionDefinitions=" + transitionDefinitions + '}';
  }
}
