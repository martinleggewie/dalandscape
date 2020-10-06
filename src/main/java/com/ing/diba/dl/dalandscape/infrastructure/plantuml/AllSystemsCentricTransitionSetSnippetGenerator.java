package com.ing.diba.dl.dalandscape.infrastructure.plantuml;

import com.ing.diba.dl.dalandscape.domain.model.DaConnection;
import com.ing.diba.dl.dalandscape.domain.model.DaEntity;
import com.ing.diba.dl.dalandscape.domain.model.entities.DaBoundary;
import com.ing.diba.dl.dalandscape.domain.model.entities.DaSystem;
import com.ing.diba.dl.dalandscape.domain.model.entities.DaSystemComponent;
import com.ing.diba.dl.dalandscape.domain.model.entities.DaUser;
import com.ing.diba.dl.dalandscape.domain.model.transition.DaConnectionTransition;
import com.ing.diba.dl.dalandscape.domain.model.transition.DaEntityTransition;
import com.ing.diba.dl.dalandscape.domain.model.transition.DaTransition;
import com.ing.diba.dl.dalandscape.domain.model.transition.DaTransitionSet;

import java.util.Set;
import java.util.stream.Collectors;

import static com.ing.diba.dl.dalandscape.domain.model.transition.DaTransition.State.WILL_BE_ADDED;
import static com.ing.diba.dl.dalandscape.domain.model.transition.DaTransition.State.WILL_BE_REMOVED;

public class AllSystemsCentricTransitionSetSnippetGenerator implements SnippetGenerator {
  private final DaTransitionSet transitionSet;
  private final Mode mode;

  public AllSystemsCentricTransitionSetSnippetGenerator(DaTransitionSet transitionSet, Mode mode) {
    this.transitionSet = transitionSet;
    this.mode = mode;
  }

  @Override
  public String generate() {
    StringBuilder result = new StringBuilder();

    result.append("\n\n'----------------------------------------\n");
    result.append(String.format("rectangle %s as \"%s\" #FFFFFF {\n", transitionSet.getKey(), transitionSet.getDisplayName()));

    // 1a. Process the list of all boundaries including their systems which themselves include their corresponding system components. As a
    // boundary can contain other boundaries, we need to do this in a recursive way. But to avoid that we encounter a given boundary only
    // once, we need to filter out any boundary which is itself already member of another boundary.
    Set<DaConnectionTransition> boundaryInsideBoundaryConnectionTransitions = transitionSet
            .findConnectionTransitions(DaBoundary.class, DaConnection.Type.CONTAINS, DaBoundary.class);
    Set<DaEntityTransition> boundaryInsideBoundaryTransitions = boundaryInsideBoundaryConnectionTransitions.stream()
            .map(c -> new DaEntityTransition(c.toEntity(), c.getState())).collect(Collectors.toSet());
    Set<DaEntityTransition> boundaryTransitions = transitionSet.findEntityTransitionsByEntityType(DaBoundary.class);
    boundaryTransitions.removeAll(boundaryInsideBoundaryTransitions);

    for (DaEntityTransition boundaryTransition : boundaryTransitions) {
      result.append(generateBoundary(boundaryTransition, 1));
    }

    // 1b. As a system must not be part of a boundary, we need to find the list of all systems which don't belong to any boundary and
    // process them separately.
    Set<DaConnectionTransition> systemInsideBoundaryConnectionTransitions = transitionSet
            .findConnectionTransitions(DaBoundary.class, DaConnection.Type.CONTAINS, DaSystem.class);
    Set<DaEntityTransition> systemsInsideBoundaryTransitions = systemInsideBoundaryConnectionTransitions.stream()
            .map(c -> new DaEntityTransition(c.toEntity(), c.getState())).collect(Collectors.toSet());
    Set<DaEntityTransition> systemsOutsideAnyBoundaryTransitions = transitionSet.findEntityTransitionsByEntityType(DaSystem.class);
    systemsOutsideAnyBoundaryTransitions.removeAll(systemsInsideBoundaryTransitions);
    result.append(generateSystems(systemsOutsideAnyBoundaryTransitions, 1));

    // 1c. As a user also must not be part of a boundary, we need to find the list of all users which don't belong to any boundary and
    // process them separately.
    Set<DaConnectionTransition> userInsideBoundaryConnectionTransitions = transitionSet
            .findConnectionTransitions(DaBoundary.class, DaConnection.Type.CONTAINS, DaUser.class);
    Set<DaEntityTransition> userInsideBoundaryTransitions = userInsideBoundaryConnectionTransitions.stream()
            .map(c -> new DaEntityTransition(c.toEntity(), c.getState())).collect(Collectors.toSet());
    Set<DaEntityTransition> userOutsideBoundaryTransitions = transitionSet.findEntityTransitionsByEntityType(DaUser.class);
    userOutsideBoundaryTransitions.removeAll(userInsideBoundaryTransitions);
    result.append(generateUsers(userOutsideBoundaryTransitions, 1));

    // 2. Process the list of all connections between systems, system components, and users.

    // 2a. Process the system connects to system connections if the correct mode is set. But there is an exception: If there is a system
    // which contains no system component, then we want to see the connections from or to other systems to this system even when the full
    // mode is set.
    Set<DaConnectionTransition> systemConnectsToSystemConnectionTransitions = transitionSet
            .findConnectionTransitions(DaSystem.class, DaConnection.Type.CONNECTS_TO, DaSystem.class);
    for (DaConnectionTransition sctsConnectionTransition : systemConnectsToSystemConnectionTransitions) {

      // Check if either of the two systems do not contain any system components. If yes, then draw the connection between both systems
      // even when the full mode has been set.
      Set<DaConnectionTransition> fromSystemComponents = transitionSet
              .findConnectionTransitions(sctsConnectionTransition.fromEntity(), DaConnection.Type.CONTAINS, DaSystemComponent.class);
      Set<DaConnectionTransition> toSystemComponents = transitionSet
              .findConnectionTransitions(sctsConnectionTransition.toEntity(), DaConnection.Type.CONTAINS, DaSystemComponent.class);

      if (fromSystemComponents.size() == 0 || toSystemComponents.size() == 0 || mode.equals(Mode.SYSTEMS_ONLY)) {
        result.append(String.format("  %s --> %s #%s\n", transitionSet.getKey() + "_" + sctsConnectionTransition.fromEntity().getKey(),
                transitionSet.getKey() + "_" + sctsConnectionTransition.toEntity().getKey(), deriveColor(sctsConnectionTransition)));
        result.append(createLinkNoteForTransitionState(sctsConnectionTransition.getState()));
      }
    }

    // 2b. Process the system component depends on system components connections.
    // As we need to always have the system key which contains the current system component as a prefix to the system component key, we
    // need to find these systems in our current scene. This is a little bit tricky and somehow ugly.
    if (mode.equals(Mode.FULL)) {
      Set<DaConnectionTransition> systemComponentDependsOnSystemComponentConnectionTransitions = transitionSet
              .findConnectionTransitions(DaSystemComponent.class, DaConnection.Type.DEPENDS_ON, DaSystemComponent.class);
      for (DaConnectionTransition systemComponentDependsOnSystemComponentConnectionTransition :
              systemComponentDependsOnSystemComponentConnectionTransitions) {

        Set<DaConnectionTransition> fromSystemContainsSystemComponentConnectionTransitions = transitionSet
                .findConnectionTransitions(DaSystem.class, DaConnection.Type.CONTAINS,
                        systemComponentDependsOnSystemComponentConnectionTransition.fromEntity());
        Set<DaConnectionTransition> toSystemContainsSystemComponentConnectionTransitions = transitionSet
                .findConnectionTransitions(DaSystem.class, DaConnection.Type.CONTAINS,
                        systemComponentDependsOnSystemComponentConnectionTransition.toEntity());

        for (DaConnectionTransition fromConnectionTransition : fromSystemContainsSystemComponentConnectionTransitions) {
          for (DaConnectionTransition toConnectionTransition : toSystemContainsSystemComponentConnectionTransitions) {
            // Before we draw the connection arrow between the two system components, we need to check if their containing systems do
            // have a connectsTo connection as well or if both system components are contained in the very same system. Only then can we
            // draw the arrow.
            DaSystem fromSystem = (DaSystem) fromConnectionTransition.fromEntity();
            DaSystem toSystem = (DaSystem) toConnectionTransition.fromEntity();
            Set<DaConnectionTransition> relatedSystemConnectsToSystemConnectionTransitions = transitionSet
                    .findConnectionTransitions(fromSystem, DaConnection.Type.CONNECTS_TO, toSystem);

            if (relatedSystemConnectsToSystemConnectionTransitions.size() > 0 || fromSystem.equals(toSystem)) {
              result.append(String.format("  %s --> %s #%s\n",
                      transitionSet.getKey() + "_" + fromConnectionTransition.fromEntity().getKey() + "_" + fromConnectionTransition
                              .toEntity().getKey(),
                      transitionSet.getKey() + "_" + toConnectionTransition.fromEntity().getKey() + "_" + toConnectionTransition.toEntity()
                              .getKey(), deriveColor(systemComponentDependsOnSystemComponentConnectionTransition)));
              result.append(createLinkNoteForTransitionState(systemComponentDependsOnSystemComponentConnectionTransition.getState()));
            }

          }
        }
      }
    }
    result.append("}\n\n");

    // 2c. Process the user uses system connections.
    Set<DaConnectionTransition> userUsesSystemConnectionTransitions = transitionSet
            .findConnectionTransitions(DaUser.class, DaConnection.Type.USES, DaSystem.class);
    for (DaConnectionTransition uusConnectionTransition : userUsesSystemConnectionTransitions) {
      result.append(String.format("  %s --> %s #%s\n", transitionSet.getKey() + "_" + uusConnectionTransition.fromEntity().getKey(),
              transitionSet.getKey() + "_" + uusConnectionTransition.toEntity().getKey(), deriveColor(uusConnectionTransition)));
      result.append(createLinkNoteForTransitionState(uusConnectionTransition.getState()));
    }

    return result.toString();
  }

  /**
   * Watch out - recursion!
   */
  private String generateBoundary(DaEntityTransition boundaryTransition, int depth) {
    StringBuilder result = new StringBuilder();

    result.append(String.format("%srectangle %s as \"%s\" {\n", createDepthIndent(depth),
            transitionSet.getKey() + "_" + boundaryTransition.getEntity().getKey(), boundaryTransition.getEntity().getDisplayName()));

    // find all boundaries which are contained in the current boundary ...
    Set<DaConnectionTransition> boundaryContainsBoundaryTransitions = transitionSet
            .findConnectionTransitions(boundaryTransition.getEntity(), DaConnection.Type.CONTAINS, DaBoundary.class);
    Set<DaEntityTransition> subBoundaryTransitions = boundaryContainsBoundaryTransitions.stream()
            .map(c -> new DaEntityTransition(c.toEntity(), c.getState())).collect(Collectors.toSet());
    // ... and process them recursively.
    subBoundaryTransitions.forEach(t -> result.append(generateBoundary(t, depth + 1)));

    // Now let's deal with the systems which belong to the current boundary
    Set<DaConnectionTransition> boundaryContainsSystemTransitions = transitionSet
            .findConnectionTransitions(boundaryTransition.getEntity(), DaConnection.Type.CONTAINS, DaSystem.class);
    Set<DaEntityTransition> systemTransitions = boundaryContainsSystemTransitions.stream()
            .map(c -> new DaEntityTransition(c.toEntity(), c.getState())).collect(Collectors.toSet());
    result.append(generateSystems(systemTransitions, depth + 1));

    // And let's not forget the users which are part of the current boundary
    Set<DaConnectionTransition> boundaryContainsUserTransitions = transitionSet
            .findConnectionTransitions(boundaryTransition.getEntity(), DaConnection.Type.CONTAINS, DaUser.class);
    Set<DaEntityTransition> userTransitions = boundaryContainsUserTransitions.stream()
            .map(c -> new DaEntityTransition(c.toEntity(), c.getState())).collect(Collectors.toSet());
    result.append(generateUsers(userTransitions, depth + 1));

    result.append(createDepthIndent(depth)).append("}\n");

    return result.toString();
  }

  private String generateSystems(Set<DaEntityTransition> systemTransitions, int depth) {
    StringBuilder result = new StringBuilder();

    for (DaEntityTransition systemTransition : systemTransitions) {
      result.append(String.format("%s%s %s as \"==%s\" #%s {\n", createDepthIndent(depth), derivePumlEntityType(systemTransition),
              transitionSet.getKey() + "_" + systemTransition.getEntity().getKey(), systemTransition.getEntity().getDisplayName(),
              deriveColor(systemTransition)));

      if (mode.equals(Mode.FULL)) {
        Set<DaConnectionTransition> systemContainsSystemComponentConnectionTransitions = transitionSet
                .findConnectionTransitions(systemTransition.getEntity(), DaConnection.Type.CONTAINS, DaSystemComponent.class);

        for (DaConnectionTransition connectionTransition : systemContainsSystemComponentConnectionTransitions) {
          result.append(String.format("%srectangle %s as \"%s\" #%s\n", createDepthIndent(depth + 1),
                  transitionSet.getKey() + "_" + systemTransition.getEntity().getKey() + "_" + connectionTransition.toEntity().getKey(),
                  connectionTransition.toEntity().getDisplayName(), deriveColor(connectionTransition)));
        }
      }
      result.append(createDepthIndent(depth)).append("}\n");
    }

    return result.toString();
  }

  private String generateUsers(Set<DaEntityTransition> userTransitions, int depth) {
    StringBuilder result = new StringBuilder();

    for (DaEntityTransition userTransition : userTransitions) {
      result.append(String.format("%sactor %s as \"==%s\" #%s\n", createDepthIndent(depth),
              transitionSet.getKey() + "_" + userTransition.getEntity().getKey(), userTransition.getEntity().getDisplayName(),
              deriveColor(userTransition)));
    }
    if (userTransitions.size() > 0) {
      result.append("\n");
    }

    return result.toString();
  }

  private String createDepthIndent(int depth) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < depth; i++) {
      result.append("  ");
    }
    return result.toString();
  }

  private String derivePumlEntityType(DaEntityTransition entityTransition) {
    if (entityTransition.getEntity() instanceof DaSystem) {
      DaSystem system = (DaSystem) entityTransition.getEntity();
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
      if (connectionTransition.fromEntity() instanceof DaSystem && connectionTransition.type()
              .equals(DaConnection.Type.CONNECTS_TO) && connectionTransition.toEntity() instanceof DaSystem) {
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

  public enum Mode {
    FULL,
    SYSTEMS_ONLY
  }
}
