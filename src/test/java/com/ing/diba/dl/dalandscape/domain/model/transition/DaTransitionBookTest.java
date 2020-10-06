package com.ing.diba.dl.dalandscape.domain.model.transition;

import com.ing.diba.dl.dalandscape.domain.model.DaConnection;
import com.ing.diba.dl.dalandscape.domain.model.DaEntity;
import com.ing.diba.dl.dalandscape.domain.model.entities.DaSystem;
import com.ing.diba.dl.dalandscape.domain.model.entities.DaSystemComponent;
import com.ing.diba.dl.dalandscape.domain.model.storyboard.DaScene;
import com.ing.diba.dl.dalandscape.domain.model.storyboard.DaStoryboard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DaTransitionBookTest {

  private final Set<DaEntity> entities = new HashSet<>();

  @BeforeEach
  public void prepareEntities() {
    entities.add(new DaSystem("system1", "System 1", "Type 1"));
    entities.add(new DaSystem("system2", "System 2", "Type 2"));
    entities.add(new DaSystemComponent("systemComponent1", "SystemComponent 1"));
    entities.add(new DaSystemComponent("systemComponent2", "SystemComponent 2"));
  }

  @Test
  public void testOneSceneWithEntity() {

    // 1. Arrange
    TransDefForTests def01 = new TransDefForTests("scene1", "ADD", "system1", "SYSTEM", "NN", "NN", "NN");
    DaScene scene1 = new DaScene("scene1", "Scene 1");
    DaStoryboard storyboard = new DaStoryboard("storyboard", "Storyboard");
    storyboard.getScenes().add(scene1);

    Set<TransitionDefinition> transitionDefinitions = new HashSet<>();
    transitionDefinitions.add(def01);

    DaTransitionBook cut = new DaTransitionBook(storyboard, transitionDefinitions, entities);

    // 2. Act
    List<DaTransitionSet> result = cut.createTransitionSets();

    // 3. Assert
    assertEquals(1, result.size());

    DaTransitionSet transitionSet1 = result.get(0);
    assertEquals("scene1", transitionSet1.getKey());
    assertEquals("Scene 1", transitionSet1.getDisplayName());
    assertEquals(1, transitionSet1.getTransitions().size());
    assertTrue(transitionSet1.getTransitions()
            .contains(new DaEntityTransition(new DaSystem("system1", "System 1", "Type 1"), DaTransition.State.STAYS_UNCHANGED)));
  }

  @Test
  public void testThreeScenesWithEntities() {

    // 1. Arrange
    TransDefForTests def01 = new TransDefForTests("scene1", "ADD", "system1", "SYSTEM", "NN", "NN", "NN");
    TransDefForTests def02 = new TransDefForTests("scene2", "ADD", "system2", "SYSTEM", "NN", "NN", "NN");
    TransDefForTests def03 = new TransDefForTests("scene3", "REMOVE", "system1", "SYSTEM", "NN", "NN", "NN");
    DaScene scene1 = new DaScene("scene1", "Scene 1");
    DaScene scene2 = new DaScene("scene2", "Scene 2");
    DaScene scene3 = new DaScene("scene3", "Scene 3");
    DaStoryboard storyboard = new DaStoryboard("storyboard", "Storyboard");
    storyboard.getScenes().add(scene1);
    storyboard.getScenes().add(scene2);
    storyboard.getScenes().add(scene3);

    Set<TransitionDefinition> transitionDefinitions = new HashSet<>();
    transitionDefinitions.add(def01);
    transitionDefinitions.add(def02);
    transitionDefinitions.add(def03);

    DaTransitionBook cut = new DaTransitionBook(storyboard, transitionDefinitions, entities);

    // 2. Act
    List<DaTransitionSet> result = cut.createTransitionSets();

    // 3. Assert
    assertEquals(5, result.size());

    {
      DaTransitionSet transitionSet1 = result.get(0);
      assertEquals("scene1", transitionSet1.getKey());
      assertEquals("Scene 1", transitionSet1.getDisplayName());
      assertEquals(1, transitionSet1.getTransitions().size());
      assertTrue(transitionSet1.getTransitions()
              .contains(new DaEntityTransition(new DaSystem("system1", "System 1", "Type 1"), DaTransition.State.STAYS_UNCHANGED)));
    }

    {
      DaTransitionSet transitionSet2 = result.get(1);
      assertEquals("scene12scene2", transitionSet2.getKey());
      assertEquals("Scene 1 --> Scene 2", transitionSet2.getDisplayName());
      assertEquals(2, transitionSet2.getTransitions().size());
      assertTrue(transitionSet2.getTransitions()
              .contains(new DaEntityTransition(new DaSystem("system1", "System 1", "Type 1"), DaTransition.State.STAYS_UNCHANGED)));
      assertTrue(transitionSet2.getTransitions()
              .contains(new DaEntityTransition(new DaSystem("system2", "System 2", "Type 2"), DaTransition.State.WILL_BE_ADDED)));
    }

    {
      DaTransitionSet transitionSet3 = result.get(2);
      assertEquals("scene2", transitionSet3.getKey());
      assertEquals("Scene 2", transitionSet3.getDisplayName());
      assertEquals(2, transitionSet3.getTransitions().size());
      assertTrue(transitionSet3.getTransitions()
              .contains(new DaEntityTransition(new DaSystem("system1", "System 1", "Type 1"), DaTransition.State.STAYS_UNCHANGED)));
      assertTrue(transitionSet3.getTransitions()
              .contains(new DaEntityTransition(new DaSystem("system2", "System 2", "Type 2"), DaTransition.State.STAYS_UNCHANGED)));
    }

    {
      DaTransitionSet transitionSet4 = result.get(3);
      assertEquals("scene22scene3", transitionSet4.getKey());
      assertEquals("Scene 2 --> Scene 3", transitionSet4.getDisplayName());
      assertEquals(2, transitionSet4.getTransitions().size());
      assertTrue(transitionSet4.getTransitions()
              .contains(new DaEntityTransition(new DaSystem("system1", "System 1", "Type 1"), DaTransition.State.WILL_BE_REMOVED)));
      assertTrue(transitionSet4.getTransitions()
              .contains(new DaEntityTransition(new DaSystem("system2", "System 2", "Type 2"), DaTransition.State.STAYS_UNCHANGED)));
    }

    {
      DaTransitionSet transitionSet5 = result.get(4);
      assertEquals("scene3", transitionSet5.getKey());
      assertEquals("Scene 3", transitionSet5.getDisplayName());
      assertEquals(1, transitionSet5.getTransitions().size());
      assertTrue(transitionSet5.getTransitions()
              .contains(new DaEntityTransition(new DaSystem("system2", "System 2", "Type 2"), DaTransition.State.STAYS_UNCHANGED)));
    }
  }

  @Test
  public void testOneSceneWithConnection() {

    // 1. Arrange
    TransDefForTests def01 = new TransDefForTests("scene1", "ADD", "system1", "SYSTEM", "CONTAINS", "systemComponent1", "SYSTEMCOMPONENT");
    DaScene scene1 = new DaScene("scene1", "Scene 1");
    DaStoryboard storyboard = new DaStoryboard("storyboard", "Storyboard");
    storyboard.getScenes().add(scene1);

    Set<TransitionDefinition> transitionDefinitions = new HashSet<>();
    transitionDefinitions.add(def01);

    DaTransitionBook cut = new DaTransitionBook(storyboard, transitionDefinitions, entities);

    // 2. Act
    List<DaTransitionSet> result = cut.createTransitionSets();

    // 3. Assert
    assertEquals(1, result.size());

    DaTransitionSet transitionSet1 = result.get(0);
    assertEquals("scene1", transitionSet1.getKey());
    assertEquals("Scene 1", transitionSet1.getDisplayName());
    assertEquals(3, transitionSet1.getTransitions().size());

    DaEntity entity1 = new DaSystem("system1", "System 1", "Type 1");
    DaEntity entity2 = new DaSystemComponent("systemComponent1", "SystemComponent 1");
    DaEntityTransition entityTransition1 = new DaEntityTransition(entity1, DaTransition.State.STAYS_UNCHANGED);
    DaEntityTransition entityTransition2 = new DaEntityTransition(entity2, DaTransition.State.STAYS_UNCHANGED);
    DaConnectionTransition connectionTransition1 = new DaConnectionTransition(
            new DaConnection(entity1, DaConnection.Type.CONTAINS, entity2), DaTransition.State.STAYS_UNCHANGED);
    assertTrue(transitionSet1.getTransitions().contains(entityTransition1));
    assertTrue(transitionSet1.getTransitions().contains(entityTransition2));
    assertTrue(transitionSet1.getTransitions().contains(connectionTransition1));
  }

  @Test
  public void testThreeScenesWithConnections() {

    // 1. Arrange
    TransDefForTests def01 = new TransDefForTests("scene1", "ADD", "system1", "SYSTEM", "CONTAINS", "systemComponent1", "SYSTEMCOMPONENT");
    TransDefForTests def02 = new TransDefForTests("scene2", "ADD", "system1", "SYSTEM", "CONTAINS", "systemComponent2", "SYSTEMCOMPONENT");
    TransDefForTests def03 = new TransDefForTests("scene3", "REMOVE", "system1", "SYSTEM", "CONTAINS", "systemComponent1",
            "SYSTEMCOMPONENT");
    DaScene scene1 = new DaScene("scene1", "Scene 1");
    DaScene scene2 = new DaScene("scene2", "Scene 2");
    DaScene scene3 = new DaScene("scene3", "Scene 3");
    DaStoryboard storyboard = new DaStoryboard("storyboard", "Storyboard");
    storyboard.getScenes().add(scene1);
    storyboard.getScenes().add(scene2);
    storyboard.getScenes().add(scene3);

    Set<TransitionDefinition> transitionDefinitions = new HashSet<>();
    transitionDefinitions.add(def01);
    transitionDefinitions.add(def02);
    transitionDefinitions.add(def03);

    DaTransitionBook cut = new DaTransitionBook(storyboard, transitionDefinitions, entities);

    // 2. Act
    List<DaTransitionSet> result = cut.createTransitionSets();

    // 3. Assert
    assertEquals(5, result.size());

    {
      DaTransitionSet transitionSet1 = result.get(0);
      assertEquals("scene1", transitionSet1.getKey());
      assertEquals("Scene 1", transitionSet1.getDisplayName());
      assertEquals(3, transitionSet1.getTransitions().size());

      DaEntity entity1 = new DaSystem("system1", "System 1", "Type 1");
      DaEntity entity2 = new DaSystemComponent("systemComponent1", "SystemComponent 1");
      DaEntityTransition entityTransition1 = new DaEntityTransition(entity1, DaTransition.State.STAYS_UNCHANGED);
      DaEntityTransition entityTransition2 = new DaEntityTransition(entity2, DaTransition.State.STAYS_UNCHANGED);
      DaConnectionTransition connectionTransition1 = new DaConnectionTransition(
              new DaConnection(entity1, DaConnection.Type.CONTAINS, entity2), DaTransition.State.STAYS_UNCHANGED);
      assertTrue(transitionSet1.getTransitions().contains(entityTransition1));
      assertTrue(transitionSet1.getTransitions().contains(entityTransition2));
      assertTrue(transitionSet1.getTransitions().contains(connectionTransition1));
    }

    {
      DaTransitionSet transitionSet2 = result.get(1);
      assertEquals("scene12scene2", transitionSet2.getKey());
      assertEquals("Scene 1 --> Scene 2", transitionSet2.getDisplayName());
      assertEquals(5, transitionSet2.getTransitions().size());

      DaEntity entity1 = new DaSystem("system1", "System 1", "Type 1");
      DaEntity entity2 = new DaSystemComponent("systemComponent1", "SystemComponent 1");
      DaEntity entity3 = new DaSystemComponent("systemComponent2", "SystemComponent 2");
      DaEntityTransition entityTransition1 = new DaEntityTransition(entity1, DaTransition.State.STAYS_UNCHANGED);
      DaEntityTransition entityTransition2 = new DaEntityTransition(entity2, DaTransition.State.STAYS_UNCHANGED);
      DaEntityTransition entityTransition3 = new DaEntityTransition(entity3, DaTransition.State.WILL_BE_ADDED);
      DaConnectionTransition connectionTransition1 = new DaConnectionTransition(
              new DaConnection(entity1, DaConnection.Type.CONTAINS, entity2), DaTransition.State.STAYS_UNCHANGED);
      DaConnectionTransition connectionTransition2 = new DaConnectionTransition(
              new DaConnection(entity1, DaConnection.Type.CONTAINS, entity3), DaTransition.State.WILL_BE_ADDED);

      assertTrue(transitionSet2.getTransitions().contains(entityTransition1));
      assertTrue(transitionSet2.getTransitions().contains(entityTransition2));
      assertTrue(transitionSet2.getTransitions().contains(entityTransition3));
      assertTrue(transitionSet2.getTransitions().contains(connectionTransition1));
      assertTrue(transitionSet2.getTransitions().contains(connectionTransition2));
    }

    {
      DaTransitionSet transitionSet3 = result.get(2);
      assertEquals("scene2", transitionSet3.getKey());
      assertEquals("Scene 2", transitionSet3.getDisplayName());
      assertEquals(5, transitionSet3.getTransitions().size());

      DaEntity entity1 = new DaSystem("system1", "System 1", "Type 1");
      DaEntity entity2 = new DaSystemComponent("systemComponent1", "SystemComponent 1");
      DaEntity entity3 = new DaSystemComponent("systemComponent2", "SystemComponent 2");
      DaEntityTransition entityTransition1 = new DaEntityTransition(entity1, DaTransition.State.STAYS_UNCHANGED);
      DaEntityTransition entityTransition2 = new DaEntityTransition(entity2, DaTransition.State.STAYS_UNCHANGED);
      DaEntityTransition entityTransition3 = new DaEntityTransition(entity3, DaTransition.State.STAYS_UNCHANGED);
      DaConnectionTransition connectionTransition1 = new DaConnectionTransition(
              new DaConnection(entity1, DaConnection.Type.CONTAINS, entity2), DaTransition.State.STAYS_UNCHANGED);
      DaConnectionTransition connectionTransition2 = new DaConnectionTransition(
              new DaConnection(entity1, DaConnection.Type.CONTAINS, entity3), DaTransition.State.STAYS_UNCHANGED);

      assertTrue(transitionSet3.getTransitions().contains(entityTransition1));
      assertTrue(transitionSet3.getTransitions().contains(entityTransition2));
      assertTrue(transitionSet3.getTransitions().contains(entityTransition3));
      assertTrue(transitionSet3.getTransitions().contains(connectionTransition1));
      assertTrue(transitionSet3.getTransitions().contains(connectionTransition2));
    }

    {
      DaTransitionSet transitionSet4 = result.get(3);
      assertEquals("scene22scene3", transitionSet4.getKey());
      assertEquals("Scene 2 --> Scene 3", transitionSet4.getDisplayName());
      assertEquals(5, transitionSet4.getTransitions().size());

      DaEntity entity1 = new DaSystem("system1", "System 1", "Type 1");
      DaEntity entity2 = new DaSystemComponent("systemComponent1", "SystemComponent 1");
      DaEntity entity3 = new DaSystemComponent("systemComponent2", "SystemComponent 2");
      DaEntityTransition entityTransition1 = new DaEntityTransition(entity1, DaTransition.State.STAYS_UNCHANGED);
      DaEntityTransition entityTransition2 = new DaEntityTransition(entity2, DaTransition.State.STAYS_UNCHANGED);
      DaEntityTransition entityTransition3 = new DaEntityTransition(entity3, DaTransition.State.STAYS_UNCHANGED);
      DaConnectionTransition connectionTransition1 = new DaConnectionTransition(
              new DaConnection(entity1, DaConnection.Type.CONTAINS, entity2), DaTransition.State.WILL_BE_REMOVED);
      DaConnectionTransition connectionTransition2 = new DaConnectionTransition(
              new DaConnection(entity1, DaConnection.Type.CONTAINS, entity3), DaTransition.State.STAYS_UNCHANGED);

      assertTrue(transitionSet4.getTransitions().contains(entityTransition1));
      assertTrue(transitionSet4.getTransitions().contains(entityTransition2));
      assertTrue(transitionSet4.getTransitions().contains(entityTransition3));
      assertTrue(transitionSet4.getTransitions().contains(connectionTransition1));
      assertTrue(transitionSet4.getTransitions().contains(connectionTransition2));
    }

    {
      DaTransitionSet transitionSet5 = result.get(4);
      assertEquals("scene3", transitionSet5.getKey());
      assertEquals("Scene 3", transitionSet5.getDisplayName());
      assertEquals(4, transitionSet5.getTransitions().size());

      DaEntity entity1 = new DaSystem("system1", "System 1", "Type 1");
      DaEntity entity2 = new DaSystemComponent("systemComponent1", "SystemComponent 1");
      DaEntity entity3 = new DaSystemComponent("systemComponent2", "SystemComponent 2");
      DaEntityTransition entityTransition1 = new DaEntityTransition(entity1, DaTransition.State.STAYS_UNCHANGED);
      DaEntityTransition entityTransition2 = new DaEntityTransition(entity2, DaTransition.State.STAYS_UNCHANGED);
      DaEntityTransition entityTransition3 = new DaEntityTransition(entity3, DaTransition.State.STAYS_UNCHANGED);
      DaConnectionTransition connectionTransition2 = new DaConnectionTransition(
              new DaConnection(entity1, DaConnection.Type.CONTAINS, entity3), DaTransition.State.STAYS_UNCHANGED);

      assertTrue(transitionSet5.getTransitions().contains(entityTransition1));
      assertTrue(transitionSet5.getTransitions().contains(entityTransition2));
      assertTrue(transitionSet5.getTransitions().contains(entityTransition3));
      assertTrue(transitionSet5.getTransitions().contains(connectionTransition2));
    }
  }

  @Test
  public void testOneSceneWithUnknownEntities() {
    // 1. Arrange
    TransDefForTests def01 = new TransDefForTests("scene1", "ADD", "system1", "SYSTEM", "NN", "NN", "NN");
    TransDefForTests def02 = new TransDefForTests("scene1", "ADD", "systemcomponent1", "SYSTEMCOMPONENT", "NN", "NN", "NN");
    DaScene scene1 = new DaScene("scene1", "Scene 1");
    DaStoryboard storyboard = new DaStoryboard("storyboard", "Storyboard");
    storyboard.getScenes().add(scene1);

    Set<TransitionDefinition> transitionDefinitions = new HashSet<>();
    transitionDefinitions.add(def01);
    transitionDefinitions.add(def02);

    DaTransitionBook cut = new DaTransitionBook(storyboard, transitionDefinitions, new HashSet<>());

    // 2. Act
    List<DaTransitionSet> result = cut.createTransitionSets();

    // 3. Assert
    assertEquals(1, result.size());

    DaTransitionSet transitionSet1 = result.get(0);
    assertEquals("scene1", transitionSet1.getKey());
    assertEquals("Scene 1", transitionSet1.getDisplayName());
    assertEquals(2, transitionSet1.getTransitions().size());
    assertTrue(transitionSet1.getTransitions().contains(
            new DaEntityTransition(new DaSystem("system1", "system1 (missing system definition)", "application"),
                    DaTransition.State.STAYS_UNCHANGED)));
    assertTrue(transitionSet1.getTransitions().contains(
            new DaEntityTransition(new DaSystemComponent("systemcomponent1", "systemcomponent1 (missing system component definition)"),
                    DaTransition.State.STAYS_UNCHANGED)));

  }

}