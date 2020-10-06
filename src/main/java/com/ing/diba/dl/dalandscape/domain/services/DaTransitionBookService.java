package com.ing.diba.dl.dalandscape.domain.services;

import com.ing.diba.dl.dalandscape.domain.model.DaEntity;
import com.ing.diba.dl.dalandscape.domain.model.storyboard.DaScene;
import com.ing.diba.dl.dalandscape.domain.model.storyboard.DaStoryboard;
import com.ing.diba.dl.dalandscape.domain.model.transition.DaTransitionBook;
import com.ing.diba.dl.dalandscape.domain.model.transition.TransitionDefinition;
import com.ing.diba.dl.dalandscape.domain.repositories.DaBoundaryRepository;
import com.ing.diba.dl.dalandscape.domain.repositories.DaStoryboardRepository;
import com.ing.diba.dl.dalandscape.domain.repositories.DaSystemComponentRepository;
import com.ing.diba.dl.dalandscape.domain.repositories.DaSystemRepository;
import com.ing.diba.dl.dalandscape.domain.repositories.DaTeamRepository;
import com.ing.diba.dl.dalandscape.domain.repositories.DaUseCaseGroupRepository;
import com.ing.diba.dl.dalandscape.domain.repositories.DaUseCaseRepository;
import com.ing.diba.dl.dalandscape.domain.repositories.DaUserRepository;
import com.ing.diba.dl.dalandscape.domain.repositories.TransitionDefinitionRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DaTransitionBookService {

  private final DaBoundaryRepository boundaryRepository;
  private final DaSystemRepository systemRepository;
  private final DaSystemComponentRepository systemComponentRepository;
  private final DaUseCaseRepository useCaseRepository;
  private final DaUseCaseGroupRepository useCaseGroupRepository;
  private final DaUserRepository userRepository;
  private final DaStoryboardRepository storyboardRepository;
  private final DaTeamRepository teamRepository;
  private final TransitionDefinitionRepository transitionDefinitionRepository;

  public DaTransitionBookService(DaBoundaryRepository boundaryRepository, DaSystemRepository systemRepository,
                                 DaSystemComponentRepository systemComponentRepository, DaUseCaseRepository useCaseRepository,
                                 DaUseCaseGroupRepository useCaseGroupRepository, DaUserRepository userRepository,
                                 DaStoryboardRepository storyboardRepository, DaTeamRepository teamRepository,
                                 TransitionDefinitionRepository transitionDefinitionRepository) {
    this.boundaryRepository = boundaryRepository;
    this.systemRepository = systemRepository;
    this.systemComponentRepository = systemComponentRepository;
    this.useCaseRepository = useCaseRepository;
    this.useCaseGroupRepository = useCaseGroupRepository;
    this.userRepository = userRepository;
    this.storyboardRepository = storyboardRepository;
    this.teamRepository = teamRepository;
    this.transitionDefinitionRepository = transitionDefinitionRepository;
  }


  public Set<DaTransitionBook> findAllTransitionBooks() {
    Set<DaTransitionBook> result = new HashSet<>();

    Set<DaEntity> entities = findEntities();

    for (DaStoryboard storyboard : storyboardRepository.findAll()) {
      Set<TransitionDefinition> transitionDefinitions = new HashSet<>();
      for (DaScene scene : storyboard.getScenes()) {
        transitionDefinitionRepository.findAllBySceneKey(scene.getKey()).forEach(transitionDefinitions::add);
      }
      result.add(new DaTransitionBook(storyboard, transitionDefinitions, entities));
    }

    return result;
  }

  private Set<DaEntity> findEntities() {
    Set<DaEntity> result = new HashSet<>();
    boundaryRepository.findAll().forEach(result::add);
    systemRepository.findAll().forEach(result::add);
    systemComponentRepository.findAll().forEach(result::add);
    useCaseRepository.findAll().forEach(result::add);
    useCaseGroupRepository.findAll().forEach(result::add);
    userRepository.findAll().forEach(result::add);
    teamRepository.findAll().forEach(result::add);
    return result;
  }
}
