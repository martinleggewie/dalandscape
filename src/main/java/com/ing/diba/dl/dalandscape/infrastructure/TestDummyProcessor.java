package com.ing.diba.dl.dalandscape.infrastructure;

import com.ing.diba.dl.dalandscape.domain.repositories.DaBoundaryRepository;
import com.ing.diba.dl.dalandscape.domain.repositories.DaStoryboardRepository;
import com.ing.diba.dl.dalandscape.domain.repositories.TransitionDefinitionRepository;
import com.ing.diba.dl.dalandscape.domain.services.DaTransitionBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestDummyProcessor {
  @Autowired
  private final TransitionDefinitionRepository transitionDefinitionRepository;
  @Autowired
  private final DaStoryboardRepository newStoryboardRepository;
  @Autowired
  private final DaTransitionBookService transitionBookService;
  @Autowired
  private final DaBoundaryRepository boundaryRepository;

  public TestDummyProcessor(TransitionDefinitionRepository transitionDefinitionRepository, DaStoryboardRepository newStoryboardRepository,
                            DaTransitionBookService transitionBookService, DaBoundaryRepository boundaryRepository) {
    this.transitionDefinitionRepository = transitionDefinitionRepository;
    this.newStoryboardRepository = newStoryboardRepository;
    this.transitionBookService = transitionBookService;
    this.boundaryRepository = boundaryRepository;

    doSomething();
  }

  private void doSomething() {
    //    Set<DaStoryboard> storyboards = new HashSet<>();
    //    storyboardRepository.findAll().forEach(storyboards::add);
    //
    //    Set<DaTeamContainsPersonConnection> connections = new HashSet<>();
    //    teamContainsPersonConnectionRepository.findAll().forEach(connections::add);
    //
    //    Set<DaStoryboard> otherStoryboards = new HashSet<>();
    //    otherStoryboards.addAll(storyboardService.findAll());
    //
    //    transitionDefinitionRepository.findAll().forEach(System.out::println);

    newStoryboardRepository.findAll().forEach(System.out::println);

    //    transitionBookService.findAllTransitionBooks().forEach(System.out::println);

    boundaryRepository.findAll().forEach(System.out::println);

  }
}
