package com.ing.diba.dl.dalandscape.infrastructure;

import com.ing.diba.dl.dalandscape.domain.model.entities.DaSystem;
import com.ing.diba.dl.dalandscape.domain.model.transition.DaEntityTransition;
import com.ing.diba.dl.dalandscape.domain.model.transition.DaTransitionBook;
import com.ing.diba.dl.dalandscape.domain.model.transition.DaTransitionSet;
import com.ing.diba.dl.dalandscape.domain.services.DaTransitionBookService;
import com.ing.diba.dl.dalandscape.infrastructure.plantuml.AllSystemsCentricTransitionBookSnippetGenerator;
import com.ing.diba.dl.dalandscape.infrastructure.plantuml.AllSystemsCentricTransitionSetSnippetGenerator;
import com.ing.diba.dl.dalandscape.infrastructure.plantuml.OneSystemCentricTransitionSetSnippetGenerator;
import com.ing.diba.dl.dalandscape.infrastructure.plantuml.PlantUmlGenerator;
import com.ing.diba.dl.dalandscape.infrastructure.plantuml.UseCaseCentricTransitionSetSnippetGenerator;
import com.ing.diba.dl.dalandscape.infrastructure.plantuml.UseCaseGroupCentricTransitionSetSnippetGenerator;
import com.ing.diba.dl.dalandscape.infrastructure.plantuml.UserCentricTransitionBookSnippetGenerator;
import com.ing.diba.dl.dalandscape.infrastructure.plantuml.UserCentricTransitionSetSnippetGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static com.ing.diba.dl.dalandscape.infrastructure.plantuml.AllSystemsCentricTransitionSetSnippetGenerator.Mode.SYSTEMS_ONLY;
import static com.ing.diba.dl.dalandscape.infrastructure.plantuml.PlantUmlGenerator.Mode.DEFAULT;
import static com.ing.diba.dl.dalandscape.infrastructure.plantuml.PlantUmlGenerator.Mode.MINDMAP;
import static com.ing.diba.dl.dalandscape.infrastructure.plantuml.UseCaseCentricTransitionSetSnippetGenerator.Mode.GROUPED_BY_SYSTEM;
import static com.ing.diba.dl.dalandscape.infrastructure.plantuml.UseCaseCentricTransitionSetSnippetGenerator.Mode.GROUPED_BY_SYSTEMCOMPONENT;
import static com.ing.diba.dl.dalandscape.infrastructure.plantuml.UseCaseCentricTransitionSetSnippetGenerator.Mode.GROUPED_BY_USECASE;
import static com.ing.diba.dl.dalandscape.infrastructure.plantuml.UseCaseGroupCentricTransitionSetSnippetGenerator.Mode.UCG_SYSTEM;
import static com.ing.diba.dl.dalandscape.infrastructure.plantuml.UseCaseGroupCentricTransitionSetSnippetGenerator.Mode.UCG_SYSTEMCOMPONENT;
import static com.ing.diba.dl.dalandscape.infrastructure.plantuml.UseCaseGroupCentricTransitionSetSnippetGenerator.Mode.UCG_USECASE;

@Component
public class DiagramFilesCreator {

  @Autowired
  private final DaTransitionBookService transitionBookService;

  @Autowired
  private final Config config;

  public DiagramFilesCreator(DaTransitionBookService transitionBookService, Config config) {
    this.transitionBookService = transitionBookService;
    this.config = config;

    Set<DaTransitionBook> transitionBooks = transitionBookService.findAllTransitionBooks();
    createSystemCentricTransactionBookDiagrams(transitionBooks);
    createUseCaseCentricTransactionBookDiagrams(transitionBooks);
    createUseCaseGroupCentricTransactionBookDiagrams(transitionBooks);
    createUserCentricTransactionBookDiagrams(transitionBooks);
  }

  private void createUseCaseGroupCentricTransactionBookDiagrams(Set<DaTransitionBook> transitionBooks) {
    for (DaTransitionBook transitionBook : transitionBooks) {
      int counter = 1;
      for (DaTransitionSet transitionSet : transitionBook.createTransitionSets()) {

        String byUseCaseGroupTitle = String.format("%03d_%s_usecasegroupcentric_usecases", counter, transitionSet.getKey());
        PlantUmlGenerator byUseCaseGroupGenerator = new PlantUmlGenerator(byUseCaseGroupTitle, MINDMAP)
                .add(new UseCaseGroupCentricTransitionSetSnippetGenerator(transitionSet, UCG_USECASE));
        String byUseCaseGroupPumlString = byUseCaseGroupGenerator.generate();
        createAndWriteOutput(transitionBook.key(), byUseCaseGroupTitle, byUseCaseGroupPumlString);

        String bySystemComponentTitle = String.format("%03d_%s_usecasegroupcentric_systemcomponents", counter, transitionSet.getKey());
        PlantUmlGenerator bySystemComponentGenerator = new PlantUmlGenerator(bySystemComponentTitle, MINDMAP)
                .add(new UseCaseGroupCentricTransitionSetSnippetGenerator(transitionSet, UCG_SYSTEMCOMPONENT));
        String bySystemComponentPumlString = bySystemComponentGenerator.generate();
        createAndWriteOutput(transitionBook.key(), bySystemComponentTitle, bySystemComponentPumlString);

        String bySystemTitle = String.format("%03d_%s_usecasegroupcentric_systems", counter, transitionSet.getKey());
        PlantUmlGenerator bySystemGenerator = new PlantUmlGenerator(bySystemTitle, MINDMAP)
                .add(new UseCaseGroupCentricTransitionSetSnippetGenerator(transitionSet, UCG_SYSTEM));
        String bySystemPumlString = bySystemGenerator.generate();
        createAndWriteOutput(transitionBook.key(), bySystemTitle, bySystemPumlString);

        counter++;
      }
    }
  }

  private void createUseCaseCentricTransactionBookDiagrams(Set<DaTransitionBook> transitionBooks) {
    for (DaTransitionBook transitionBook : transitionBooks) {
      int counter = 1;
      for (DaTransitionSet transitionSet : transitionBook.createTransitionSets()) {

        String byUseCaseTitle = String.format("%03d_%s_usecasecentric_byusecase", counter, transitionSet.getKey());
        PlantUmlGenerator byUseCaseGenerator = new PlantUmlGenerator(byUseCaseTitle, MINDMAP)
                .add(new UseCaseCentricTransitionSetSnippetGenerator(transitionSet, GROUPED_BY_USECASE));
        String byUseCasePumlString = byUseCaseGenerator.generate();
        createAndWriteOutput(transitionBook.key(), byUseCaseTitle, byUseCasePumlString);

        String bySystemComponentTitle = String.format("%03d_%s_usecasecentric_bysystemcomponent", counter, transitionSet.getKey());
        PlantUmlGenerator bySystemComponentGenerator = new PlantUmlGenerator(bySystemComponentTitle, MINDMAP)
                .add(new UseCaseCentricTransitionSetSnippetGenerator(transitionSet, GROUPED_BY_SYSTEMCOMPONENT));
        String bySystemComponentPumlString = bySystemComponentGenerator.generate();
        createAndWriteOutput(transitionBook.key(), bySystemComponentTitle, bySystemComponentPumlString);

        String bySystemTitle = String.format("%03d_%s_usecasecentric_bysystem", counter, transitionSet.getKey());
        PlantUmlGenerator bySystemGenerator = new PlantUmlGenerator(bySystemTitle, MINDMAP)
                .add(new UseCaseCentricTransitionSetSnippetGenerator(transitionSet, GROUPED_BY_SYSTEM));
        String bySystemPumlString = bySystemGenerator.generate();
        createAndWriteOutput(transitionBook.key(), bySystemTitle, bySystemPumlString);

        counter++;
      }
    }
  }

  private void createUserCentricTransactionBookDiagrams(Set<DaTransitionBook> transitionBooks) {
    for (DaTransitionBook transitionBook : transitionBooks) {
      String title = "usercentric";
      PlantUmlGenerator generator = new PlantUmlGenerator(title, DEFAULT)
              .add(new UserCentricTransitionBookSnippetGenerator(transitionBook));
      String pumlString = generator.generate();
      createAndWriteOutput(transitionBook.key(), title, pumlString);
    }

    for (DaTransitionBook transitionBook : transitionBooks) {
      int counter = 1;
      for (DaTransitionSet transitionSet : transitionBook.createTransitionSets()) {
        String title = String.format("%03d_%s_usercentric", counter, transitionSet.getKey());
        PlantUmlGenerator byUseCaseGenerator = new PlantUmlGenerator(title, DEFAULT)
                .add(new UserCentricTransitionSetSnippetGenerator(transitionSet));
        String pumlString = byUseCaseGenerator.generate();
        createAndWriteOutput(transitionBook.key(), title, pumlString);
        counter++;
      }
    }
  }

  private void createSystemCentricTransactionBookDiagrams(Set<DaTransitionBook> transitionBooks) {
    // For all defined transition books create one diagram for all systems which contains all transition sets
    for (DaTransitionBook transitionBook : transitionBooks) {
      String fullTitle = "allsystems_full";
      PlantUmlGenerator fullGenerator = new PlantUmlGenerator(fullTitle, DEFAULT)
              .add(new AllSystemsCentricTransitionBookSnippetGenerator(transitionBook,
                      AllSystemsCentricTransitionSetSnippetGenerator.Mode.FULL));
      String fullPumlString = fullGenerator.generate();
      createAndWriteOutput(transitionBook.key(), fullTitle, fullPumlString);

      String systemsOnlyTitle = "allsystems_systemsonly";
      PlantUmlGenerator systemsOnlyGenerator = new PlantUmlGenerator(systemsOnlyTitle, DEFAULT)
              .add(new AllSystemsCentricTransitionBookSnippetGenerator(transitionBook, SYSTEMS_ONLY));
      String systemsOnlyPumlString = systemsOnlyGenerator.generate();
      createAndWriteOutput(transitionBook.key(), systemsOnlyTitle, systemsOnlyPumlString);
    }

    // For all defined transition books create a diagram for all systems for each defined transition set
    for (DaTransitionBook transitionBook : transitionBooks) {
      int counter = 1;
      for (DaTransitionSet transitionSet : transitionBook.createTransitionSets()) {
        String fullTitle = String.format("%03d_%s_allsystems_full", counter, transitionSet.getKey());
        PlantUmlGenerator fullGenerator = new PlantUmlGenerator(fullTitle, DEFAULT)
                .add(new AllSystemsCentricTransitionSetSnippetGenerator(transitionSet,
                        AllSystemsCentricTransitionSetSnippetGenerator.Mode.FULL));
        String fullPumlString = fullGenerator.generate();
        createAndWriteOutput(transitionBook.key(), fullTitle, fullPumlString);

        String systemsOnlyTitle = String.format("%03d_%s_allsystems_systemsonly", counter, transitionSet.getKey());
        PlantUmlGenerator systemsOnlyGenerator = new PlantUmlGenerator(systemsOnlyTitle, DEFAULT)
                .add(new AllSystemsCentricTransitionSetSnippetGenerator(transitionSet, SYSTEMS_ONLY));
        String systemsOnlyPumlString = systemsOnlyGenerator.generate();
        createAndWriteOutput(transitionBook.key(), systemsOnlyTitle, systemsOnlyPumlString);
        counter++;
      }
    }

    // For all defined transition books and for all contained transition sets create one diagram for each system and dependency depth
    for (DaTransitionBook transitionBook : transitionBooks) {
      int counter = 1;
      for (DaTransitionSet transitionSet : transitionBook.createTransitionSets()) {
        Set<DaEntityTransition> systemTransitions = transitionSet.findEntityTransitionsByEntityType(DaSystem.class);
        for (DaEntityTransition systemTransition : systemTransitions) {
          String fullTitle = String
                  .format("%03d_%s_onesystem_%s_full", counter, transitionSet.getKey(), systemTransition.getEntity().getKey());
          PlantUmlGenerator fullGenerator = new PlantUmlGenerator(fullTitle, DEFAULT)
                  .add(new OneSystemCentricTransitionSetSnippetGenerator(transitionSet,
                          OneSystemCentricTransitionSetSnippetGenerator.Mode.FULL, (DaSystem) systemTransition.getEntity()));
          String fullPumlString = fullGenerator.generate();
          createAndWriteOutput(transitionBook.key(), fullTitle, fullPumlString);

          String systemsOnlyTitle = String
                  .format("%03d_%s_onesystem_%s_systemsonly", counter, transitionSet.getKey(), systemTransition.getEntity().getKey());
          PlantUmlGenerator systemsOnlyGenerator = new PlantUmlGenerator(systemsOnlyTitle, DEFAULT)
                  .add(new OneSystemCentricTransitionSetSnippetGenerator(transitionSet,
                          OneSystemCentricTransitionSetSnippetGenerator.Mode.SYSTEMS_ONLY, (DaSystem) systemTransition.getEntity()));
          String systemsOnlyPumlString = systemsOnlyGenerator.generate();
          createAndWriteOutput(transitionBook.key(), systemsOnlyTitle, systemsOnlyPumlString);

        }
        counter++;
      }
    }
  }

  private void createAndWriteOutput(String transitionBookKey, String title, String content) {
    String filename = title.toLowerCase().replaceAll("[^a-zA-Z0-9]", "_") + ".puml";
    try {
      Path outputFolderPath = Paths
              .get(config.getOutputPath() + File.separator + transitionBookKey.toLowerCase().replaceAll("[^a-zA-Z0-9]", "_"));
      if (!Files.exists(outputFolderPath)) {
        Files.createDirectories(outputFolderPath);
      }
      Path outputFilePath = Paths.get(outputFolderPath + File.separator + filename);
      FileWriter fileWriter = new FileWriter(outputFilePath.toFile(), false);
      fileWriter.write(content);
      fileWriter.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
