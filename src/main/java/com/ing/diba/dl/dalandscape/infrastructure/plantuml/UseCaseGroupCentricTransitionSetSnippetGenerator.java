package com.ing.diba.dl.dalandscape.infrastructure.plantuml;

import com.ing.diba.dl.dalandscape.domain.model.DaEntity;
import com.ing.diba.dl.dalandscape.domain.model.entities.DaSystem;
import com.ing.diba.dl.dalandscape.domain.model.entities.DaSystemComponent;
import com.ing.diba.dl.dalandscape.domain.model.entities.DaUseCase;
import com.ing.diba.dl.dalandscape.domain.model.entities.DaUseCaseGroup;
import com.ing.diba.dl.dalandscape.domain.model.transition.DaConnectionTransition;
import com.ing.diba.dl.dalandscape.domain.model.transition.DaEntityTransition;
import com.ing.diba.dl.dalandscape.domain.model.transition.DaTransition;
import com.ing.diba.dl.dalandscape.domain.model.transition.DaTransitionSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ing.diba.dl.dalandscape.domain.model.DaConnection.Type.CONTAINS;
import static com.ing.diba.dl.dalandscape.domain.model.DaConnection.Type.IMPLEMENTS;
import static com.ing.diba.dl.dalandscape.domain.model.transition.DaTransition.State.STAYS_UNCHANGED;
import static com.ing.diba.dl.dalandscape.domain.model.transition.DaTransition.State.WILL_BE_ADDED;
import static com.ing.diba.dl.dalandscape.domain.model.transition.DaTransition.State.WILL_BE_REMOVED;

public class UseCaseGroupCentricTransitionSetSnippetGenerator implements SnippetGenerator {
  private final DaTransitionSet transitionSet;
  private final Mode mode;

  public UseCaseGroupCentricTransitionSetSnippetGenerator(DaTransitionSet transitionSet, Mode mode) {
    this.transitionSet = transitionSet;
    this.mode = mode;
  }

  @Override
  public String generate() {
    StringBuilder result = new StringBuilder();

    result.append("\n\n'----------------------------------------\n");

    // To get started, we need to collect the set of all ucg contains uc connection transitions. Based on this, we can collect all other
    // needed information transitively, depending on the needs of the given diagram.
    Set<DaConnectionTransition> ucgContainsUcConnTrans = transitionSet
            .findConnectionTransitions(DaUseCaseGroup.class, CONTAINS, DaUseCase.class);
    Set<DaEntityTransition> ucgEntityTrans = ucgContainsUcConnTrans.stream()
            .map(c -> transitionSet.findEntityTransitionByEntity(c.fromEntity())).collect(Collectors.toSet());

    // Depending on the mode we collect more data, if needed, and then create the corresponding diagram.
    if (mode.equals(Mode.UCG_USECASE)) {
      result.append(String.format("*[#aliceblue] =Use case groups and their use cases\\n\"%s\"\n", transitionSet.getDisplayName()));
      for (DaEntityTransition ucgEntityTran : ucgEntityTrans) {
        result.append(String.format("**[#%s] ==%s\n", deriveColor(ucgEntityTran), ucgEntityTran.getEntity().getDisplayName()));

        List<DaConnectionTransition> filteredUcgContainsUcConnTrans = ucgContainsUcConnTrans.stream()
                .filter(c -> c.fromEntity().equals(ucgEntityTran.getEntity()))
                .sorted((o1, o2) -> o1.toEntity().getKey().compareTo(o2.toEntity().getKey())).collect(Collectors.toList());
        for (DaConnectionTransition connTran : filteredUcgContainsUcConnTrans) {
          result.append(String.format("***[#%s] %s\n", deriveColor(connTran), connTran.toEntity().getDisplayName()));
        }
      }

    } else if (mode.equals(Mode.UCG_SYSTEMCOMPONENT)) {
      result.append(
              String.format("*[#aliceblue] =Use case groups and related system components\\n\"%s\"\n", transitionSet.getDisplayName()));

      for (DaEntityTransition ucgEntityTran : ucgEntityTrans) {
        Map<DaEntityTransition, Set<DaEntityTransition>> ucgEntityTranRelatedToSysCompEntityTranMap = new HashMap<>();

        Set<DaConnectionTransition> filteredUcgContainsUcConnTrans = ucgContainsUcConnTrans.stream()
                .filter(c -> c.fromEntity().equals(ucgEntityTran.getEntity())).collect(Collectors.toSet());
        for (DaConnectionTransition ucgContainsUcConnTran : filteredUcgContainsUcConnTrans) {
          DaEntity ucEntity = ucgContainsUcConnTran.toEntity();
          Set<DaConnectionTransition> sysCompImplementsUcConnTrans = transitionSet
                  .findConnectionTransitions(DaSystemComponent.class, IMPLEMENTS, ucEntity);
          Set<DaEntityTransition> sysCompEntityTrans = sysCompImplementsUcConnTrans.stream()
                  .map(c -> new DaEntityTransition(c.fromEntity(), ucgContainsUcConnTran.getState())).collect(Collectors.toSet());
          for (DaEntityTransition sysCompEntityTran : sysCompEntityTrans) {
            Set<DaEntityTransition> collectedSysCompEntityTrans = ucgEntityTranRelatedToSysCompEntityTranMap
                    .computeIfAbsent(ucgEntityTran, c -> new HashSet<>());
            DaEntityTransition removedSysCompEntityTran = new DaEntityTransition(sysCompEntityTran.getEntity(), WILL_BE_REMOVED);
            DaEntityTransition unchangedSysCompEntityTran = new DaEntityTransition(sysCompEntityTran.getEntity(), STAYS_UNCHANGED);
            if (collectedSysCompEntityTrans.contains(unchangedSysCompEntityTran)) {
              if (sysCompEntityTran.getState().equals(WILL_BE_ADDED) || sysCompEntityTran.getState().equals(WILL_BE_REMOVED)) {
                collectedSysCompEntityTrans.remove(unchangedSysCompEntityTran);
              }
            } else if (collectedSysCompEntityTrans.contains(removedSysCompEntityTran)) {
              if (sysCompEntityTran.getState().equals(WILL_BE_ADDED)) {
                collectedSysCompEntityTrans.remove(removedSysCompEntityTran);
              }
            }
            collectedSysCompEntityTrans.add(sysCompEntityTran);
          }
        }

        result.append(String.format("**[#%s] ==%s\n", deriveColor(ucgEntityTran), ucgEntityTran.getEntity().getDisplayName()));
        List<DaEntityTransition> sortedSysCompEntityTrans = ucgEntityTranRelatedToSysCompEntityTranMap.get(ucgEntityTran).stream()
                .sorted((o1, o2) -> o1.getEntity().getKey().compareTo(o2.getEntity().getKey())).collect(Collectors.toList());
        for (DaEntityTransition sysCompEntityTran : sortedSysCompEntityTrans) {
          result.append(String.format("***[#%s] %s\n", deriveColor(sysCompEntityTran), sysCompEntityTran.getEntity().getDisplayName()));
        }
      }

    } else if (mode.equals(Mode.UCG_SYSTEM)) {
      result.append(String.format("*[#aliceblue] =Use case groups and related systems\\n\"%s\"\n", transitionSet.getDisplayName()));

      for (DaEntityTransition ucgEntityTran : ucgEntityTrans) {
        Map<DaEntityTransition, Set<DaEntityTransition>> ucgEntityTranRelatedToSystemEntityTranMap = new HashMap<>();

        Set<DaConnectionTransition> filteredUcgContainsUcConnTrans = ucgContainsUcConnTrans.stream()
                .filter(c -> c.fromEntity().equals(ucgEntityTran.getEntity())).collect(Collectors.toSet());
        for (DaConnectionTransition ucgContainsUcConnTran : filteredUcgContainsUcConnTrans) {
          Set<DaConnectionTransition> sysCompImplementsUcConnTrans = transitionSet
                  .findConnectionTransitions(DaSystemComponent.class, IMPLEMENTS, ucgContainsUcConnTran.toEntity());
          for (DaConnectionTransition sysCompImplementsUcConnTran : sysCompImplementsUcConnTrans) {
            Set<DaConnectionTransition> systemContainsSysCompConnTrans = transitionSet
                    .findConnectionTransitions(DaSystem.class, CONTAINS, sysCompImplementsUcConnTran.fromEntity());
            Set<DaEntityTransition> systemEntityTrans = systemContainsSysCompConnTrans.stream()
                    .map(c -> new DaEntityTransition(c.fromEntity(), ucgContainsUcConnTran.getState())).collect(Collectors.toSet());
            for (DaEntityTransition systemEntityTran : systemEntityTrans) {
              Set<DaEntityTransition> collectedSystemEntityTrans = ucgEntityTranRelatedToSystemEntityTranMap
                      .computeIfAbsent(ucgEntityTran, c -> new HashSet<>());
              DaEntityTransition removedSystemEntityTran = new DaEntityTransition(systemEntityTran.getEntity(), WILL_BE_REMOVED);
              DaEntityTransition unchangedSystemEntityTran = new DaEntityTransition(systemEntityTran.getEntity(), STAYS_UNCHANGED);
              if (collectedSystemEntityTrans.contains(unchangedSystemEntityTran)) {
                if (systemEntityTran.getState().equals(WILL_BE_ADDED) || systemEntityTran.getState().equals(WILL_BE_REMOVED)) {
                  collectedSystemEntityTrans.remove(unchangedSystemEntityTran);
                }
              } else if (collectedSystemEntityTrans.contains(removedSystemEntityTran)) {
                if (systemEntityTran.getState().equals(WILL_BE_ADDED)) {
                  collectedSystemEntityTrans.remove(removedSystemEntityTran);
                }
              }
              collectedSystemEntityTrans.add(systemEntityTran);
            }
          }
        }

        result.append(String.format("**[#%s] ==%s\n", deriveColor(ucgEntityTran), ucgEntityTran.getEntity().getDisplayName()));
        List<DaEntityTransition> sortedSystemEntityTrans = ucgEntityTranRelatedToSystemEntityTranMap.get(ucgEntityTran).stream()
                .sorted((o1, o2) -> o1.getEntity().getKey().compareTo(o2.getEntity().getKey())).collect(Collectors.toList());
        for (DaEntityTransition systemEntityTran : sortedSystemEntityTrans) {
          result.append(String.format("***[#%s] %s\n", deriveColor(systemEntityTran), systemEntityTran.getEntity().getDisplayName()));
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
    UCG_USECASE,
    UCG_SYSTEMCOMPONENT,
    UCG_SYSTEM
  }
}
