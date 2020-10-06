package com.ing.diba.dl.dalandscape.infrastructure.plantuml;

import com.ing.diba.dl.dalandscape.domain.model.transition.DaTransitionBook;
import com.ing.diba.dl.dalandscape.domain.model.transition.DaTransitionSet;

public class AllSystemsCentricTransitionBookSnippetGenerator implements SnippetGenerator {
  private final DaTransitionBook transitionBook;
  private final AllSystemsCentricTransitionSetSnippetGenerator.Mode mode;

  public AllSystemsCentricTransitionBookSnippetGenerator(DaTransitionBook transitionBook,
                                                         AllSystemsCentricTransitionSetSnippetGenerator.Mode mode) {
    this.transitionBook = transitionBook;
    this.mode = mode;
  }

  @Override
  public String generate() {
    StringBuilder result = new StringBuilder();

    for (DaTransitionSet transitionSet : transitionBook.createTransitionSets()) {
      AllSystemsCentricTransitionSetSnippetGenerator snippetGenerator = new AllSystemsCentricTransitionSetSnippetGenerator(transitionSet,
              mode);
      result.append(snippetGenerator.generate());
    }

    result.append("\n\n");

    // try to convince PlantUML layout algorithm to locate the transition sets following their sorted order
    DaTransitionSet previousTransitionSet = null;
    for (DaTransitionSet transitionSet : transitionBook.createTransitionSets()) {
      if (previousTransitionSet != null) {
        result.append(String.format("%s -[hidden]-> %s\n", previousTransitionSet.getKey(), transitionSet.getKey()));
      }
      previousTransitionSet = transitionSet;
    }
    return result.toString();
  }
}
