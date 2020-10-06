package com.ing.diba.dl.dalandscape.infrastructure.plantuml;

import java.util.ArrayList;
import java.util.List;

public class PlantUmlGenerator {

  private final String title;
  private final Mode mode;
  private final List<SnippetGenerator> generators = new ArrayList<>();
  public PlantUmlGenerator(String title, Mode mode) {
    this.title = title;
    this.mode = mode;
  }

  public PlantUmlGenerator add(SnippetGenerator snippetGenerator) {
    generators.add(snippetGenerator);
    return this;
  }

  public String generate() {
    StringBuilder result = new StringBuilder();
    result.append(createHeader(title));
    generators.forEach(g -> result.append(g.generate()));
    result.append(createFooter());
    return result.toString();
  }

  private String createHeader(String title) {
    StringBuilder result = new StringBuilder();
    if (mode.equals(Mode.DEFAULT)) {
      result.append(String.format("@startuml %s\n", title.replaceAll(" --> ", "-")));
    } else if (mode.equals(Mode.MINDMAP)) {
      result.append(String.format("@startmindmap %s\n", title.replaceAll(" --> ", "-")));
    }
    result.append("\n");
    result.append("\n");
    result.append("left to right direction\n");
    result.append("\n");
    result.append("skinparam shadowing false\n");
    result.append("skinparam componentStyle uml2\n");
    result.append("skinparam WrapWidth 300\n");
    result.append("\n");
    result.append("skinparam arrowColor #black\n");
    result.append("skinparam arrowThickness 2\n");
    result.append("\n");
    result.append("skinparam actorBorderColor #black\n");
    result.append("skinparam actorFontSize 16\n");
    result.append("\n");
    result.append("skinparam usecaseBackgroundColor #F8F8F8\n");
    result.append("skinparam usecaseBorderColor #black\n");
    result.append("skinparam usecaseBorderThickness 2\n");
    result.append("skinparam usecaseFontSize 16\n");
    result.append("\n");
    result.append("skinparam rectangleBackgroundColor #F8F8F8\n");
    result.append("skinparam rectangleBorderColor #black\n");
    result.append("skinparam rectangleBorderThickness 2\n");
    result.append("skinparam rectangleFontSize 16\n");
    result.append("\n");
    result.append("skinparam componentBorderColor #black\n");
    result.append("skinparam componentBorderThickness 2\n");
    result.append("skinparam componentFontSize 16\n");
    result.append("\n");
    result.append("skinparam databaseBackgroundColor #F8F8F8\n");
    result.append("skinparam databaseBorderColor #black\n");
    result.append("'unfortunately database does not support border thickness at the moment. Nevertheless I kept the following setting\n");
    result.append("'in to a) remind me of that weakness, and b) maybe make use of it in a future PlantUML version.\n");
    result.append("skinparam databaseBorderThickness 2\n");
    result.append("skinparam databaseFontSize 16\n");
    result.append("\n");
    result.append("\n");
    return result.toString();
  }

  private String createFooter() {
    if (mode.equals(Mode.DEFAULT)) {
      return "\n@enduml";
    } else if (mode.equals(Mode.MINDMAP)) {
      return "\n@endmindmap";
    } else {
      return "BOOH. BUG!";
    }
  }

  public enum Mode {
    DEFAULT,
    MINDMAP
  }
}
