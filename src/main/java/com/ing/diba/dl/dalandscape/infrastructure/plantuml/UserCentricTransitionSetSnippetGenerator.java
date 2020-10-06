package com.ing.diba.dl.dalandscape.infrastructure.plantuml;

import com.ing.diba.dl.dalandscape.domain.model.DaConnection;
import com.ing.diba.dl.dalandscape.domain.model.entities.DaSystem;
import com.ing.diba.dl.dalandscape.domain.model.entities.DaUser;
import com.ing.diba.dl.dalandscape.domain.model.transition.DaConnectionTransition;
import com.ing.diba.dl.dalandscape.domain.model.transition.DaEntityTransition;
import com.ing.diba.dl.dalandscape.domain.model.transition.DaTransition;
import com.ing.diba.dl.dalandscape.domain.model.transition.DaTransitionSet;

import java.util.Set;
import java.util.stream.Collectors;

import static com.ing.diba.dl.dalandscape.domain.model.transition.DaTransition.State.WILL_BE_ADDED;
import static com.ing.diba.dl.dalandscape.domain.model.transition.DaTransition.State.WILL_BE_REMOVED;

public class UserCentricTransitionSetSnippetGenerator implements SnippetGenerator {
  private final DaTransitionSet transitionSet;

  public UserCentricTransitionSetSnippetGenerator(DaTransitionSet transitionSet) {
    this.transitionSet = transitionSet;
  }

  @Override
  public String generate() {
    StringBuilder result = new StringBuilder();

    result.append("\n\n'----------------------------------------\n");
    result.append(String.format("rectangle %s as \"%s\" #FFFFFF {\n", transitionSet.getKey(), transitionSet.getDisplayName()));


    // 1. Add all users to the diagram ...
    Set<DaEntityTransition> userTransitions = transitionSet.findEntityTransitionsByEntityType(DaUser.class);
    for (DaEntityTransition userTransition : userTransitions) {
      result.append(String.format("  actor %s as \"==%s\" #%s\n", transitionSet.getKey() + "_" + userTransition.getEntity().getKey(),
              userTransition.getEntity().getDisplayName(), deriveColor(userTransition)));
    }

    // 2. ... then all systems which are at least used by one of the users ...
    Set<DaConnectionTransition> userUsesSystemConnectionTransitions = transitionSet
            .findConnectionTransitions(DaUser.class, DaConnection.Type.USES, DaSystem.class);

    Set<DaEntityTransition> systemTransitions = userUsesSystemConnectionTransitions.stream()
            .map(c -> transitionSet.findEntityTransitionByEntity(c.toEntity())).collect(Collectors.toSet());
    for (DaEntityTransition systemTransition : systemTransitions) {
      result.append(String.format("  %s %s as \"%s\" #%s\n", derivePumlEntityType(systemTransition),
              transitionSet.getKey() + "_" + systemTransition.getEntity().getKey(), systemTransition.getEntity().getDisplayName(),
              deriveColor(systemTransition)));
    }

    // 3. ... and finally all "user uses system" connections to be able to link both users and systems
    for (DaConnectionTransition connectionTransition : userUsesSystemConnectionTransitions) {
      result.append(String.format("  %s --> %s #%s\n", transitionSet.getKey() + "_" + connectionTransition.fromEntity().getKey(),
              transitionSet.getKey() + "_" + connectionTransition.toEntity().getKey(), deriveColor(connectionTransition)));
      result.append(createLinkNoteForTransitionState(connectionTransition.getState()));
    }

    result.append("}\n\n");

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

}
