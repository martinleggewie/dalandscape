package com.ing.diba.dl.dalandscape.infrastructure.plantuml;

import com.ing.diba.dl.dalandscape.domain.model.transition.DaTransitionBook;
import com.ing.diba.dl.dalandscape.domain.model.transition.DaTransitionSet;

public class UserCentricTransitionBookSnippetGenerator implements SnippetGenerator {
  private final DaTransitionBook transitionBook;

  public UserCentricTransitionBookSnippetGenerator(DaTransitionBook transitionBook) {
    this.transitionBook = transitionBook;
  }

  @Override
  public String generate() {
    StringBuilder result = new StringBuilder();

    for (DaTransitionSet transitionSet : transitionBook.createTransitionSets()) {
      UserCentricTransitionSetSnippetGenerator snippetGenerator = new UserCentricTransitionSetSnippetGenerator(transitionSet);
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
