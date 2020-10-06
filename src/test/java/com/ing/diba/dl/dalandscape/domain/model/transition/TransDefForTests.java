package com.ing.diba.dl.dalandscape.domain.model.transition;

public class TransDefForTests extends TransitionDefinition {
  TransDefForTests(String sceneKey, String transitionType, String fromEntityKey, String fromEntityType, String connectionType,
                   String toEntityKey, String toEntityType) {
    super(new Key(sceneKey, TransitionDefinition.TransitionType.valueOf(transitionType), fromEntityKey,
            TransitionDefinition.EntityType.valueOf(fromEntityType), TransitionDefinition.ConnectionType.valueOf(connectionType),
            toEntityKey, TransitionDefinition.EntityType.valueOf(toEntityType)));
  }
}
