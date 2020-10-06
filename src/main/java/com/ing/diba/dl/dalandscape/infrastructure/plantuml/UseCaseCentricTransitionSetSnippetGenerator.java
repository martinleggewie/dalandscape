package com.ing.diba.dl.dalandscape.infrastructure.plantuml;

import com.ing.diba.dl.dalandscape.domain.model.DaConnection;
import com.ing.diba.dl.dalandscape.domain.model.DaEntity;
import com.ing.diba.dl.dalandscape.domain.model.entities.DaSystem;
import com.ing.diba.dl.dalandscape.domain.model.entities.DaSystemComponent;
import com.ing.diba.dl.dalandscape.domain.model.entities.DaUseCase;
import com.ing.diba.dl.dalandscape.domain.model.transition.DaConnectionTransition;
import com.ing.diba.dl.dalandscape.domain.model.transition.DaTransition;
import com.ing.diba.dl.dalandscape.domain.model.transition.DaTransitionSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class UseCaseCentricTransitionSetSnippetGenerator implements SnippetGenerator {
  private final DaTransitionSet transitionSet;
  private final Mode mode;

  public UseCaseCentricTransitionSetSnippetGenerator(DaTransitionSet transitionSet, Mode mode) {
    this.transitionSet = transitionSet;
    this.mode = mode;
  }

  @Override
  public String generate() {
    StringBuilder result = new StringBuilder();

    result.append("\n\n'----------------------------------------\n");

    // To get started, find all connections between use cases and system components
    Set<DaConnectionTransition> systemComponentImplementsUseCaseConnectionTransitions = transitionSet
            .findConnectionTransitions(DaSystemComponent.class, DaConnection.Type.IMPLEMENTS, DaUseCase.class);

    // Depending on the mode, we will now either find all system components for a given use case or do it the other way around.
    if (mode.equals(Mode.GROUPED_BY_USECASE)) {
      result.append(String.format("*[#aliceblue] =Use cases: \"%s\"\\n(Grouped by use cases)\n", transitionSet.getDisplayName()));
      Set<DaEntity> useCaseEntities = systemComponentImplementsUseCaseConnectionTransitions.stream().map(DaConnectionTransition::toEntity)
              .collect(Collectors.toSet());
      for (DaEntity useCaseEntity : useCaseEntities) {
        result.append(String.format("**[#aliceblue] ==%s\n", useCaseEntity.getDisplayName()));

        List<DaConnectionTransition> filteredOnUseCaseConnectionTransitions = systemComponentImplementsUseCaseConnectionTransitions.stream()
                .filter(c -> c.toEntity().equals(useCaseEntity))
                .sorted((o1, o2) -> o1.fromEntity().getKey().compareTo(o2.fromEntity().getKey())).collect(Collectors.toList());
        for (DaConnectionTransition transition : filteredOnUseCaseConnectionTransitions) {
          result.append(String.format("***[#%s] %s\n", deriveColor(transition), transition.fromEntity().getDisplayName()));
        }
      }

    } else if (mode.equals(Mode.GROUPED_BY_SYSTEMCOMPONENT)) {
      result.append(String.format("*[#aliceblue] =Use cases: \"%s\"\\n(Grouped by system components)\n", transitionSet.getDisplayName()));
      Set<DaEntity> systemComponentEntities = systemComponentImplementsUseCaseConnectionTransitions.stream()
              .map(DaConnectionTransition::fromEntity).collect(Collectors.toSet());

      for (DaEntity systemComponentEntity : systemComponentEntities) {
        result.append(String.format("**[#aliceblue] ==%s\n", systemComponentEntity.getDisplayName()));

        List<DaConnectionTransition> filteredOnSystemComponentConnectionTransitions = systemComponentImplementsUseCaseConnectionTransitions
                .stream().filter(c -> c.fromEntity().equals(systemComponentEntity))
                .sorted((o1, o2) -> o1.toEntity().getKey().compareTo(o2.toEntity().getKey())).collect(Collectors.toList());
        for (DaConnectionTransition transition : filteredOnSystemComponentConnectionTransitions) {
          result.append(String.format("***[#%s] %s\n", deriveColor(transition), transition.toEntity().getDisplayName()));
        }
      }

    } else if (mode.equals(Mode.GROUPED_BY_SYSTEM)) {
      result.append(String.format("*[#aliceblue] =Use cases: \"%s\"\\n(Grouped by systems)\n", transitionSet.getDisplayName()));
      Set<DaEntity> systemComponentEntities = systemComponentImplementsUseCaseConnectionTransitions.stream()
              .map(DaConnectionTransition::fromEntity).collect(Collectors.toSet());

      Map<DaEntity, Set<DaConnectionTransition>> systemImplementsUseCasesMap = new HashMap<>();
      for (DaEntity systemComponentEntity : systemComponentEntities) {
        // 1. Find all connection transitions in which the current system component implements use cases
        Set<DaConnectionTransition> filteredOnUseCaseConnectionTransitions = systemComponentImplementsUseCaseConnectionTransitions.stream()
                .filter(c -> c.fromEntity().equals(systemComponentEntity)).collect(Collectors.toSet());

        // 2. Find all systems which contain the current system component
        Set<DaEntity> systemEntities = transitionSet
                .findConnectionTransitions(DaSystem.class, DaConnection.Type.CONTAINS, systemComponentEntity).stream()
                .map(DaConnectionTransition::fromEntity).collect(Collectors.toSet());

        // 3. And now add all the use case connection transitions to the current systems
        for (DaEntity systemEntity : systemEntities) {
          Objects.requireNonNull(systemImplementsUseCasesMap.computeIfAbsent(systemEntity, c -> new HashSet<>()))
                  .addAll(filteredOnUseCaseConnectionTransitions);
        }
      }

      // 4. Now that we have collected all information in a map, we can just add the PlantUML entries to the result string
      for (DaEntity systemEntity : systemImplementsUseCasesMap.keySet()) {
        result.append(String.format("**[#aliceblue] ==%s\n", systemEntity.getDisplayName()));
        List<DaConnectionTransition> filteredOnSystemConnectionTransitions = systemImplementsUseCasesMap.get(systemEntity).stream()
                .sorted((o1, o2) -> o1.toEntity().getKey().compareTo(o2.toEntity().getKey())).collect(Collectors.toList());
        for (DaConnectionTransition transition : filteredOnSystemConnectionTransitions) {
          result.append(String.format("***[#%s] %s\n", deriveColor(transition), transition.toEntity().getDisplayName()));
        }
      }
    }

    result.append("\n\n");

    return result.toString();
  }

  private String deriveColor(DaTransition transition) {
    switch (transition.getState()) {
      case STAYS_UNCHANGED:
        return "cornsilk";
      case WILL_BE_ADDED:
        return "CDEAC0";
      case WILL_BE_REMOVED:
        return "FEC3A6";
      default:
        return "magenta";
    }
  }

  public enum Mode {
    GROUPED_BY_USECASE,
    GROUPED_BY_SYSTEMCOMPONENT,
    GROUPED_BY_SYSTEM
  }
}
