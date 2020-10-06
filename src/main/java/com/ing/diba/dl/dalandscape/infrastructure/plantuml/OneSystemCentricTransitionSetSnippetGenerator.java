package com.ing.diba.dl.dalandscape.infrastructure.plantuml;

import com.ing.diba.dl.dalandscape.domain.businesslogic.SystemToSystemDependency;
import com.ing.diba.dl.dalandscape.domain.model.DaConnection;
import com.ing.diba.dl.dalandscape.domain.model.DaEntity;
import com.ing.diba.dl.dalandscape.domain.model.entities.DaSystem;
import com.ing.diba.dl.dalandscape.domain.model.entities.DaSystemComponent;
import com.ing.diba.dl.dalandscape.domain.model.entities.DaUser;
import com.ing.diba.dl.dalandscape.domain.model.transition.DaConnectionTransition;
import com.ing.diba.dl.dalandscape.domain.model.transition.DaEntityTransition;
import com.ing.diba.dl.dalandscape.domain.model.transition.DaTransition;
import com.ing.diba.dl.dalandscape.domain.model.transition.DaTransitionSet;

import java.util.Set;
import java.util.stream.Collectors;

import static com.ing.diba.dl.dalandscape.domain.model.DaConnection.Type.CONNECTS_TO;
import static com.ing.diba.dl.dalandscape.domain.model.DaConnection.Type.CONTAINS;
import static com.ing.diba.dl.dalandscape.domain.model.DaConnection.Type.USES;
import static com.ing.diba.dl.dalandscape.domain.model.transition.DaTransition.State.WILL_BE_ADDED;
import static com.ing.diba.dl.dalandscape.domain.model.transition.DaTransition.State.WILL_BE_REMOVED;
import static com.ing.diba.dl.dalandscape.infrastructure.plantuml.OneSystemCentricTransitionSetSnippetGenerator.Mode.SYSTEMS_ONLY;

public class OneSystemCentricTransitionSetSnippetGenerator implements SnippetGenerator {
  private final DaTransitionSet transitionSet;
  private final Mode mode;
  private final DaSystem system;

  public OneSystemCentricTransitionSetSnippetGenerator(DaTransitionSet transitionSet, Mode mode, DaSystem system) {
    this.transitionSet = transitionSet;
    this.mode = mode;
    this.system = system;
  }

  @Override
  public String generate() {
    StringBuilder result = new StringBuilder();

    // 1. Collect all data related to the given entity. In general we need to search for the transitions instead of the entities
    // themselves because we need to transition state to be able to derive the correct color.

    // 1a. Find the system entity transition for the given system, as well as the system component connection transitions. We need both
    // information to be able to start our drawing with focus on the given system
    DaEntityTransition systemEntityTran = transitionSet.findEntityTransitionByEntity(system);
    Set<DaConnectionTransition> systemContainsSysCompConnTrans = transitionSet
            .findConnectionTransitions(system, CONTAINS, DaSystemComponent.class);

    // 1b. Find all incoming (i.e. dependee) and all outgoing (i.e. dependent) system entities. We need these to be able to calculate all
    // pairs of systems, their system components, and the dependencies between the system components. And we need these pairs to draw
    // these elements and relations
    Set<DaEntity> fromSystemEntities = transitionSet.findConnectionTransitions(DaSystem.class, CONNECTS_TO, system).stream()
            .map(DaConnectionTransition::fromEntity).collect(Collectors.toSet());
    Set<DaEntity> toSystemEntities = transitionSet.findConnectionTransitions(system, CONNECTS_TO, DaSystem.class).stream()
            .map(DaConnectionTransition::toEntity).collect(Collectors.toSet());
    Set<SystemToSystemDependency> fromDependencies = fromSystemEntities.stream()
            .map(e -> new SystemToSystemDependency(transitionSet, e, system)).collect(Collectors.toSet());
    Set<SystemToSystemDependency> toDependencies = toSystemEntities.stream()
            .map(e -> new SystemToSystemDependency(transitionSet, system, e)).collect(Collectors.toSet());

    // 1c. Find all relevant user entity and connection transitions, that is, all users which use the given system. We need both entity
    // and connection transitions because we need to make the difference between if a user itself stays unchanged, has been added or
    // removed, and if the connection between the user and the system stays unchanged, has been added or removed.
    Set<DaConnectionTransition> userConnectionTransitions = transitionSet.findConnectionTransitions(DaUser.class, USES, system);
    Set<DaEntityTransition> userTransitions = userConnectionTransitions.stream().map(DaConnectionTransition::fromEntity)
            .map(transitionSet::findEntityTransitionByEntity).collect(Collectors.toSet());


    // 2. Now that we have all the data, let's start drawing all the entities
    result.append("\n\n'----------------------------------------\n");
    result.append(String.format("rectangle %s as \"%s\" #FFFFFF {\n", transitionSet.getKey(), transitionSet.getDisplayName()));

    // 2a. Draw the given system and its system components. Add an additional border around it to emphasize it.
    result.append("\n  ' System in focus\n");
    result.append("  rectangle systemBorder as \"(all eyes on me)\" #aliceblue {\n");
    result.append(generateSystemAndSysCompsNode(systemEntityTran, systemContainsSysCompConnTrans, 2));
    result.append("  }\n");

    // 2b. Draw all incoming users
    if (userTransitions.size() > 0) {
      result.append("\n  ' Users of the system in focus\n");
      for (DaEntityTransition userTransition : userTransitions) {
        result.append(String.format("  actor %s as \"==%s\" #%s\n", transitionSet.getKey() + "_" + userTransition.getEntity().getKey(),
                userTransition.getEntity().getDisplayName(), deriveColor(userTransition)));
      }
    }

    // 2c. Draw all other systems and their relevant system components.
    if (fromDependencies.size() > 0) {
      result.append("\n  ' Incoming systems (systems which depend on the system in focus)\n");
      for (SystemToSystemDependency fromDependency : fromDependencies) {
        Set<DaConnectionTransition> connTrans = fromDependency.findRelevantSystemContainsSysCompConnTransForFromSystem();
        result.append(
                generateSystemAndSysCompsNode(transitionSet.findEntityTransitionByEntity(fromDependency.getFromSystemEntity()), connTrans,
                        1));
      }
    }
    if (toDependencies.size() > 0) {
      result.append("\n  ' Outgoing systems (systems on which the system in focus depends on)\n");
      for (SystemToSystemDependency toDependency : toDependencies) {
        Set<DaConnectionTransition> connTrans = toDependency.findRelevantSystemContainsSysCompConnTransForToSystem();
        result.append(
                generateSystemAndSysCompsNode(transitionSet.findEntityTransitionByEntity(toDependency.getToSystemEntity()), connTrans, 1));
      }
    }


    // 3. Finalize the diagram by drawing all connections

    // 3a. Draw all connections from the incoming users to the focused system
    if (userConnectionTransitions.size() > 0) {
      result.append("\n  ' Connections from the user to the system in focus\n");
      for (DaConnectionTransition userConnTran : userConnectionTransitions) {
        result.append(String.format("  %s --> %s #%s\n", transitionSet.getKey() + "_" + userConnTran.fromEntity().getKey(),
                transitionSet.getKey() + "_" + userConnTran.toEntity().getKey(), deriveColor(userConnTran)));
        result.append(createLinkNoteForTransitionState(userConnTran.getState()));
      }
    }

    // 3b. Draw all connections from incoming systems incl. their contained system components, depending on the mode.
    if (fromDependencies.size() > 0) {
      result.append("\n  ' Connections from incoming systems to the system in focus\n");
      for (SystemToSystemDependency fromDependency : fromDependencies) {
        result.append(generateSystemToSystemDependencyRelation(fromDependency));
      }
    }
    if (toDependencies.size() > 0) {
      result.append("\n  ' Connections from the system in focus to outgoing systems\n");
      for (SystemToSystemDependency toDependency : toDependencies) {
        result.append(generateSystemToSystemDependencyRelation(toDependency));
      }
    }

    result.append("}\n");

    return result.toString();
  }


  private String derivePumlEntityType(DaEntity entity) {
    if (entity instanceof DaSystem) {
      DaSystem system = (DaSystem) entity;
      switch (system.getType().toLowerCase()) {
        case "application":
          return "rectangle";
        case "database":
          return "database";
        case "mobileapplication":
          return "rectangle";
        case "webapplication":
          return "rectangle";
        default:
          return "unknown";
      }
    } else if (entity instanceof DaSystemComponent) {
      return "rectangle";
    } else {
      return "unknown";
    }
  }

  private String deriveColor(DaTransition transition) {

    if (transition instanceof DaEntityTransition) {
      DaEntityTransition entityTransition = (DaEntityTransition) transition;
      if (entityTransition.getEntity() instanceof DaSystem) {
        switch (entityTransition.getState()) {
          case STAYS_UNCHANGED:
            return "cornsilk";
          case WILL_BE_ADDED:
            return "CDEAC0";
          case WILL_BE_REMOVED:
            return "FEC3A6";
        }
      }
      if (entityTransition.getEntity() instanceof DaSystemComponent) {
        switch (entityTransition.getState()) {
          case STAYS_UNCHANGED:
            return "white";
          case WILL_BE_ADDED:
            return "CDEAC0";
          case WILL_BE_REMOVED:
            return "FEC3A6";
        }
      }
      if (entityTransition.getEntity() instanceof DaUser) {
        switch (entityTransition.getState()) {
          case STAYS_UNCHANGED:
            return "cornsilk";
          case WILL_BE_ADDED:
            return "CDEAC0";
          case WILL_BE_REMOVED:
            return "FEC3A6";
        }
      }
    }

    if (transition instanceof DaConnectionTransition) {
      DaConnectionTransition connectionTransition = (DaConnectionTransition) transition;

      if (connectionTransition.fromEntity() instanceof DaSystem && connectionTransition.type()
              .equals(DaConnection.Type.CONTAINS) && connectionTransition.toEntity() instanceof DaSystemComponent) {
        switch (connectionTransition.getState()) {
          case STAYS_UNCHANGED:
            return "white";
          case WILL_BE_ADDED:
            return "CDEAC0";
          case WILL_BE_REMOVED:
            return "FEC3A6";
        }
      }
      if (connectionTransition.fromEntity() instanceof DaSystem && connectionTransition.type().equals(CONNECTS_TO) && connectionTransition
              .toEntity() instanceof DaSystem) {
        switch (connectionTransition.getState()) {
          case STAYS_UNCHANGED:
            return "black";
          case WILL_BE_ADDED:
            return "green";
          case WILL_BE_REMOVED:
            return "red";
        }
      }
      if (connectionTransition.fromEntity() instanceof DaSystemComponent && connectionTransition.type()
              .equals(DaConnection.Type.DEPENDS_ON) && connectionTransition.toEntity() instanceof DaSystemComponent) {
        switch (connectionTransition.getState()) {
          case STAYS_UNCHANGED:
            return "black";
          case WILL_BE_ADDED:
            return "green";
          case WILL_BE_REMOVED:
            return "red";
        }
      }
      if (connectionTransition.fromEntity() instanceof DaUser && connectionTransition.type()
              .equals(DaConnection.Type.USES) && connectionTransition.toEntity() instanceof DaSystem) {
        switch (connectionTransition.getState()) {
          case STAYS_UNCHANGED:
            return "black";
          case WILL_BE_ADDED:
            return "green";
          case WILL_BE_REMOVED:
            return "red";
        }
      }
    }
    return "magenta";
  }

  private String createLinkNoteForTransitionState(DaTransition.State transitionState) {
    if (transitionState.equals(WILL_BE_ADDED)) {
      return "  note on link #CDEAC0\n  ADDED\n  end note\n";
    } else if (transitionState.equals(WILL_BE_REMOVED)) {
      return "  note on link #FEC3A6\n  REMOVED\n  end note\n";
    } else {
      return "";
    }
  }

  private String createDepthIndent(int depth) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < depth; i++) {
      result.append("  ");
    }
    return result.toString();
  }

  private String generateSystemAndSysCompsNode(DaEntityTransition systemEntityTran,
                                               Set<DaConnectionTransition> systemContainsSysComConnTrans, int depth) {
    StringBuilder result = new StringBuilder();

    result.append(String.format("%s%s %s as \"=%s\" #%s {\n", createDepthIndent(depth), derivePumlEntityType(systemEntityTran.getEntity()),
            transitionSet.getKey() + "_" + systemEntityTran.getEntity().getKey(), systemEntityTran.getEntity().getDisplayName(),
            deriveColor(systemEntityTran)));
    if (mode.equals(Mode.FULL)) {
      for (DaConnectionTransition sysCompConnTran : systemContainsSysComConnTrans) {
        result.append(
                String.format("%s%s %s as \"=%s\" #%s\n", createDepthIndent(depth + 1), derivePumlEntityType(sysCompConnTran.toEntity()),
                        transitionSet.getKey() + "_" + systemEntityTran.getEntity().getKey() + "_" + sysCompConnTran.toEntity().getKey(),
                        sysCompConnTran.toEntity().getDisplayName(), deriveColor(sysCompConnTran)));
      }
    }
    result.append(createDepthIndent(depth));
    result.append("}\n");

    return result.toString();
  }

  private String generateSystemToSystemDependencyRelation(SystemToSystemDependency dependency) {
    StringBuilder result = new StringBuilder();
    if (mode.equals(SYSTEMS_ONLY) || dependency.findSysCompDependsOnSysCompConnectionTransitions().size() == 0) {
      result.append(String.format("  %s --> %s #%s\n", transitionSet.getKey() + "_" + dependency.getFromSystemEntity().getKey(),
              transitionSet.getKey() + "_" + dependency.getToSystemEntity().getKey(),
              deriveColor(dependency.findFromSystemConnectsToToSystemConnectionTransition())));
      result.append(createLinkNoteForTransitionState(dependency.findFromSystemConnectsToToSystemConnectionTransition().getState()));
    } else {
      for (DaConnectionTransition connTran : dependency.findSysCompDependsOnSysCompConnectionTransitions()) {
        result.append(String.format("  %s --> %s #%s\n",
                transitionSet.getKey() + "_" + dependency.getFromSystemEntity().getKey() + "_" + connTran.fromEntity().getKey(),
                transitionSet.getKey() + "_" + dependency.getToSystemEntity().getKey() + "_" + connTran.toEntity().getKey(),
                deriveColor(connTran)));
        result.append(createLinkNoteForTransitionState(connTran.getState()));
      }
    }
    return result.toString();
  }

  public enum Mode {
    FULL,
    SYSTEMS_ONLY
  }
}
