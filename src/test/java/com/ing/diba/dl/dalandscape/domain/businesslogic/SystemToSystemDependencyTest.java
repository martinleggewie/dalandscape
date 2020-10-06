package com.ing.diba.dl.dalandscape.domain.businesslogic;

import com.ing.diba.dl.dalandscape.domain.model.DaConnection;
import com.ing.diba.dl.dalandscape.domain.model.entities.DaSystem;
import com.ing.diba.dl.dalandscape.domain.model.entities.DaSystemComponent;
import com.ing.diba.dl.dalandscape.domain.model.transition.DaConnectionTransition;
import com.ing.diba.dl.dalandscape.domain.model.transition.DaEntityTransition;
import com.ing.diba.dl.dalandscape.domain.model.transition.DaTransitionSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ing.diba.dl.dalandscape.domain.model.DaConnection.Type.CONNECTS_TO;
import static com.ing.diba.dl.dalandscape.domain.model.DaConnection.Type.CONTAINS;
import static com.ing.diba.dl.dalandscape.domain.model.DaConnection.Type.DEPENDS_ON;
import static com.ing.diba.dl.dalandscape.domain.model.transition.DaTransition.State.STAYS_UNCHANGED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SystemToSystemDependencyTest {

  private final DaSystem fromSystemEntity = new DaSystem("fromSystem", "From System", "APPLICATION");
  private final DaSystem toSystemEntity = new DaSystem("toSystem", "To System", "APPLICATION");

  private final DaSystemComponent fromSysCompEntity1 = new DaSystemComponent("fromSysComp1", "(not important)");
  private final DaSystemComponent fromSysCompEntity2 = new DaSystemComponent("fromSysComp2", "(not important)");
  private final DaSystemComponent fromSysCompEntity3 = new DaSystemComponent("fromSysComp3", "(not important)");
  private final DaSystemComponent toSysCompEntity1 = new DaSystemComponent("toSysComp1", "(not important)");
  private final DaSystemComponent toSysCompEntity2 = new DaSystemComponent("toSysComp2", "(not important)");
  private final DaSystemComponent toSysCompEntity4 = new DaSystemComponent("toSysComp4", "(not important)");

  private final DaConnection fromSystemConnectsToToSystemConn = new DaConnection(fromSystemEntity, CONNECTS_TO, toSystemEntity);

  private final DaConnection fromSystemContainsFromSysComp1Conn = new DaConnection(fromSystemEntity, CONTAINS, fromSysCompEntity1);
  private final DaConnection fromSystemContainsFromSysComp2Conn = new DaConnection(fromSystemEntity, CONTAINS, fromSysCompEntity2);
  private final DaConnection fromSystemContainsFromSysComp3Conn = new DaConnection(fromSystemEntity, CONTAINS, fromSysCompEntity3);
  private final DaConnection toSystemContainsToSysComp1Conn = new DaConnection(toSystemEntity, CONTAINS, toSysCompEntity1);
  private final DaConnection toSystemContainsToSysComp2Conn = new DaConnection(toSystemEntity, CONTAINS, toSysCompEntity2);
  private final DaConnection toSystemContainsToSysComp4Conn = new DaConnection(toSystemEntity, CONTAINS, toSysCompEntity4);

  private final DaConnection fromSysComp1DependsOnToSysComp1Conn = new DaConnection(fromSysCompEntity1, DEPENDS_ON, toSysCompEntity1);
  private final DaConnection fromSysComp2DependsOnToSysComp2Conn = new DaConnection(fromSysCompEntity2, DEPENDS_ON, toSysCompEntity2);

  @Mock
  private DaTransitionSet transitionSet1;


  @BeforeEach
  void prepareTransitionSet() {
    when(transitionSet1.getKey()).thenReturn("transitionSet1");

    prepareConnectionTransitions();
    prepareEntityTransitions();

  }

  private void prepareEntityTransitions() {
    Set<DaEntityTransition> entityTrans = new HashSet<>();

    // Just add all the system and system components as entity transitions
    entityTrans.add(new DaEntityTransition(fromSystemEntity, STAYS_UNCHANGED));
    entityTrans.add(new DaEntityTransition(fromSysCompEntity1, STAYS_UNCHANGED));
    entityTrans.add(new DaEntityTransition(fromSysCompEntity2, STAYS_UNCHANGED));
    entityTrans.add(new DaEntityTransition(fromSysCompEntity3, STAYS_UNCHANGED));
    entityTrans.add(new DaEntityTransition(toSystemEntity, STAYS_UNCHANGED));
    entityTrans.add(new DaEntityTransition(toSysCompEntity1, STAYS_UNCHANGED));
    entityTrans.add(new DaEntityTransition(toSysCompEntity2, STAYS_UNCHANGED));
    entityTrans.add(new DaEntityTransition(toSysCompEntity4, STAYS_UNCHANGED));

    // And now prepare the mock
    when(transitionSet1.findEntityTransitions()).thenReturn(entityTrans);
    when(transitionSet1.findEntityTransitionByEntity(fromSystemEntity))
            .thenReturn(entityTrans.stream().filter(e -> e.getEntity().equals(fromSystemEntity)).iterator().next());
    when(transitionSet1.findEntityTransitionByEntity(fromSysCompEntity1))
            .thenReturn(entityTrans.stream().filter(e -> e.getEntity().equals(fromSysCompEntity1)).iterator().next());
    when(transitionSet1.findEntityTransitionByEntity(fromSysCompEntity2))
            .thenReturn(entityTrans.stream().filter(e -> e.getEntity().equals(fromSysCompEntity2)).iterator().next());
    when(transitionSet1.findEntityTransitionByEntity(fromSysCompEntity3))
            .thenReturn(entityTrans.stream().filter(e -> e.getEntity().equals(fromSysCompEntity3)).iterator().next());
    when(transitionSet1.findEntityTransitionByEntity(toSystemEntity))
            .thenReturn(entityTrans.stream().filter(e -> e.getEntity().equals(toSystemEntity)).iterator().next());
    when(transitionSet1.findEntityTransitionByEntity(toSysCompEntity1))
            .thenReturn(entityTrans.stream().filter(e -> e.getEntity().equals(toSysCompEntity1)).iterator().next());
    when(transitionSet1.findEntityTransitionByEntity(toSysCompEntity2))
            .thenReturn(entityTrans.stream().filter(e -> e.getEntity().equals(toSysCompEntity2)).iterator().next());
    when(transitionSet1.findEntityTransitionByEntity(toSysCompEntity4))
            .thenReturn(entityTrans.stream().filter(e -> e.getEntity().equals(toSysCompEntity4)).iterator().next());
  }

  private void prepareConnectionTransitions() {
    // Prepare the set containing all needed connection transitions. We will use this set later when we come the "when" part and apply
    // corresponding Java streams filter operations. By this we only need one set and can use this to prepare the mock for all kinds of
    // find operations on connection transitions.
    Set<DaConnectionTransition> conTrans = new HashSet<>();

    // Add the system connects_to system connection transitions
    conTrans.add(new DaConnectionTransition(fromSystemConnectsToToSystemConn, STAYS_UNCHANGED));

    // Add the system contains system component connection transitions
    conTrans.add(new DaConnectionTransition(fromSystemContainsFromSysComp1Conn, STAYS_UNCHANGED));
    conTrans.add(new DaConnectionTransition(fromSystemContainsFromSysComp2Conn, STAYS_UNCHANGED));
    conTrans.add(new DaConnectionTransition(fromSystemContainsFromSysComp3Conn, STAYS_UNCHANGED));
    conTrans.add(new DaConnectionTransition(toSystemContainsToSysComp1Conn, STAYS_UNCHANGED));
    conTrans.add(new DaConnectionTransition(toSystemContainsToSysComp2Conn, STAYS_UNCHANGED));
    conTrans.add(new DaConnectionTransition(toSystemContainsToSysComp4Conn, STAYS_UNCHANGED));

    // Add the system component depends_on system component connection transitions
    conTrans.add(new DaConnectionTransition(fromSysComp1DependsOnToSysComp1Conn, STAYS_UNCHANGED));
    conTrans.add(new DaConnectionTransition(fromSysComp2DependsOnToSysComp2Conn, STAYS_UNCHANGED));

    // Prepare the scenario in which the client needs all connection transitions
    when(transitionSet1.findConnectionTransitions()).thenReturn(conTrans);

    // Prepare the system connects_to system connection transitions
    Set<DaConnectionTransition> sysConnectsToSysSet = conTrans.stream()
            .filter(c -> c.fromEntity() instanceof DaSystem && c.type().equals(CONNECTS_TO) && c.toEntity() instanceof DaSystem)
            .collect(Collectors.toSet());
    when(transitionSet1.findConnectionTransitions(fromSystemEntity, CONNECTS_TO, toSystemEntity)).thenReturn(sysConnectsToSysSet);
    when(transitionSet1.findConnectionTransitions(DaSystem.class, CONNECTS_TO, toSystemEntity)).thenReturn(sysConnectsToSysSet);
    when(transitionSet1.findConnectionTransitions(fromSystemEntity, CONNECTS_TO, DaSystem.class)).thenReturn(sysConnectsToSysSet);
    when(transitionSet1.findConnectionTransitions(DaSystem.class, CONNECTS_TO, DaSystem.class)).thenReturn(sysConnectsToSysSet);

    // Prepare the system contains system component connection transitions
    when(transitionSet1.findConnectionTransitions(fromSystemEntity, CONTAINS, DaSystemComponent.class)).thenReturn(conTrans.stream()
            .filter(c -> c.fromEntity().equals(fromSystemEntity) && c.type().equals(CONTAINS) && c.toEntity() instanceof DaSystemComponent)
            .collect(Collectors.toSet()));
    when(transitionSet1.findConnectionTransitions(toSystemEntity, CONTAINS, DaSystemComponent.class)).thenReturn(conTrans.stream()
            .filter(c -> c.fromEntity().equals(toSystemEntity) && c.type().equals(CONTAINS) && c.toEntity() instanceof DaSystemComponent)
            .collect(Collectors.toSet()));
    when(transitionSet1.findConnectionTransitions(DaSystem.class, CONTAINS, DaSystemComponent.class)).thenReturn(conTrans.stream()
            .filter(c -> c.fromEntity() instanceof DaSystem && c.type().equals(CONTAINS) && c.toEntity() instanceof DaSystemComponent)
            .collect(Collectors.toSet()));

    // Prepare the system component depends_on system component connection transitions
    when(transitionSet1.findConnectionTransitions(DaSystemComponent.class, DEPENDS_ON, DaSystemComponent.class)).thenReturn(
            conTrans.stream().filter(c -> c.fromEntity() instanceof DaSystemComponent && c.type().equals(DEPENDS_ON) && c
                    .toEntity() instanceof DaSystemComponent).collect(Collectors.toSet()));
    when(transitionSet1.findConnectionTransitions(fromSysCompEntity1, DEPENDS_ON, DaSystemComponent.class)).thenReturn(conTrans.stream()
            .filter(c -> c.fromEntity().equals(fromSysCompEntity1) && c.type().equals(DEPENDS_ON) && c
                    .toEntity() instanceof DaSystemComponent).collect(Collectors.toSet()));
    when(transitionSet1.findConnectionTransitions(fromSysCompEntity2, DEPENDS_ON, DaSystemComponent.class)).thenReturn(conTrans.stream()
            .filter(c -> c.fromEntity().equals(fromSysCompEntity2) && c.type().equals(DEPENDS_ON) && c
                    .toEntity() instanceof DaSystemComponent).collect(Collectors.toSet()));
    when(transitionSet1.findConnectionTransitions(fromSysCompEntity3, DEPENDS_ON, DaSystemComponent.class)).thenReturn(conTrans.stream()
            .filter(c -> c.fromEntity().equals(fromSysCompEntity3) && c.type().equals(DEPENDS_ON) && c
                    .toEntity() instanceof DaSystemComponent).collect(Collectors.toSet()));
    when(transitionSet1.findConnectionTransitions(DaSystemComponent.class, DEPENDS_ON, toSysCompEntity1)).thenReturn(conTrans.stream()
            .filter(c -> c.fromEntity() instanceof DaSystemComponent && c.type().equals(DEPENDS_ON) && c.toEntity()
                    .equals(toSysCompEntity1)).collect(Collectors.toSet()));
    when(transitionSet1.findConnectionTransitions(DaSystemComponent.class, DEPENDS_ON, toSysCompEntity2)).thenReturn(conTrans.stream()
            .filter(c -> c.fromEntity() instanceof DaSystemComponent && c.type().equals(DEPENDS_ON) && c.toEntity()
                    .equals(toSysCompEntity2)).collect(Collectors.toSet()));
    when(transitionSet1.findConnectionTransitions(DaSystemComponent.class, DEPENDS_ON, toSysCompEntity4)).thenReturn(conTrans.stream()
            .filter(c -> c.fromEntity() instanceof DaSystemComponent && c.type().equals(DEPENDS_ON) && c.toEntity()
                    .equals(toSysCompEntity4)).collect(Collectors.toSet()));
  }

  @Test
  void testFindSysCompDependsOnSysCompConnectionTransition() {
    // Act
    SystemToSystemDependency cut = new SystemToSystemDependency(transitionSet1, fromSystemEntity, toSystemEntity);
    Set<DaConnectionTransition> result1 = cut.findSysCompDependsOnSysCompConnectionTransitions();

    // Assert
    assertEquals(2, result1.size());
    assertTrue(result1.contains(new DaConnectionTransition(fromSysComp1DependsOnToSysComp1Conn, STAYS_UNCHANGED)));
    assertTrue(result1.contains(new DaConnectionTransition(fromSysComp2DependsOnToSysComp2Conn, STAYS_UNCHANGED)));
  }

  @Test
  void testFindFromSystemConnectsToToSystemConnectionTransition() {
    // Act
    SystemToSystemDependency cut = new SystemToSystemDependency(transitionSet1, fromSystemEntity, toSystemEntity);
    DaConnectionTransition result1 = cut.findFromSystemConnectsToToSystemConnectionTransition();

    // Assert
    assertEquals(new DaConnectionTransition(fromSystemConnectsToToSystemConn, STAYS_UNCHANGED), result1);
  }

  @Test
  void testFindRelevantSystemContainsSysCompConnTransForFromSystem() {
    // Act
    SystemToSystemDependency cut = new SystemToSystemDependency(transitionSet1, fromSystemEntity, toSystemEntity);
    Set<DaConnectionTransition> result1 = cut.findRelevantSystemContainsSysCompConnTransForFromSystem();

    // Assert
    assertEquals(2, result1.size());
    assertTrue(result1.contains(new DaConnectionTransition(fromSystemContainsFromSysComp1Conn, STAYS_UNCHANGED)));
    assertTrue(result1.contains(new DaConnectionTransition(fromSystemContainsFromSysComp2Conn, STAYS_UNCHANGED)));
  }

  @Test
  void testFindRelevantSystemContainsSysCompConnTransForToSystem() {
    // Act
    SystemToSystemDependency cut = new SystemToSystemDependency(transitionSet1, fromSystemEntity, toSystemEntity);
    Set<DaConnectionTransition> result1 = cut.findRelevantSystemContainsSysCompConnTransForToSystem();

    // Assert
    assertEquals(2, result1.size());
    assertTrue(result1.contains(new DaConnectionTransition(toSystemContainsToSysComp1Conn, STAYS_UNCHANGED)));
    assertTrue(result1.contains(new DaConnectionTransition(toSystemContainsToSysComp2Conn, STAYS_UNCHANGED)));
  }
}