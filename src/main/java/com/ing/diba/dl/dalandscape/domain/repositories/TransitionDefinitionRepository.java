package com.ing.diba.dl.dalandscape.domain.repositories;

import com.ing.diba.dl.dalandscape.domain.model.transition.TransitionDefinition;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface TransitionDefinitionRepository extends CrudRepository<TransitionDefinition, String> {

  @Query("SELECT t FROM TransitionDefinition t WHERE t.key.sceneKey = :sceneKey")
  Iterable<TransitionDefinition> findAllBySceneKey(String sceneKey);
}
