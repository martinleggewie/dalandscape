package com.ing.diba.dl.dalandscape.domain.model.transition;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "datransitiondefinitions")
public class TransitionDefinition {

  @EmbeddedId
  protected Key key;

  public TransitionDefinition() {
    // empty constructor needed because this is unfortunately a JPA requirement
  }

  public TransitionDefinition(Key key) {
    this.key = key;
  }

  public Key getKey() {
    return key;
  }

  public boolean storesEntityTransition() {
    return key.getConnectionType().equals(ConnectionType.NN);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof TransitionDefinition))
      return false;
    TransitionDefinition that = (TransitionDefinition) o;
    return key.equals(that.key);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key);
  }

  @Override
  public String toString() {
    return "TransitionDefinition{" + "key=" + key + '}';
  }

  public enum TransitionType {
    ADD,
    REMOVE
  }

  public enum EntityType {
    BOUNDARY,
    SYSTEM,
    SYSTEMCOMPONENT,
    TEAM,
    TEAMMEMBER,
    USECASE,
    USECASEGROUP,
    USER,
    NN  // NN: not needed
  }

  public enum ConnectionType {
    CONNECTS_TO,
    CONTAINS,
    DEPENDS_ON,
    IMPLEMENTS,
    RESPONSIBLE_FOR,
    USES,
    NN  // NN: not needed
  }

  @Embeddable
  public static class Key implements Serializable {

    @Column(name = "scenekey")
    private String sceneKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "transitiontype")
    private TransitionType transitionType;

    @Column(name = "fromentitykey")
    private String fromEntityKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "fromentitytype")
    private EntityType fromEntityType;

    @Enumerated(EnumType.STRING)
    @Column(name = "connectiontype")
    private ConnectionType connectionType;

    @Column(name = "toentitykey")
    private String toEntityKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "toentitytype")
    private EntityType toEntityType;

    public Key() {
      // empty constructor needed because this is unfortunately a JPA requirement
    }

    public Key(String sceneKey, TransitionType transitionType, String fromEntityKey, EntityType fromEntityType,
               ConnectionType connectionType, String toEntityKey, EntityType toEntityType) {
      this.sceneKey = sceneKey;
      this.transitionType = transitionType;
      this.fromEntityKey = fromEntityKey;
      this.fromEntityType = fromEntityType;
      this.connectionType = connectionType;
      this.toEntityKey = toEntityKey;
      this.toEntityType = toEntityType;
    }

    public String getSceneKey() {
      return sceneKey;
    }

    public TransitionType getTransitionType() {
      return transitionType;
    }

    public String getFromEntityKey() {
      return fromEntityKey;
    }

    public EntityType getFromEntityType() {
      return fromEntityType;
    }

    public ConnectionType getConnectionType() {
      return connectionType;
    }

    public String getToEntityKey() {
      return toEntityKey;
    }

    public EntityType getToEntityType() {
      return toEntityType;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (!(o instanceof Key))
        return false;
      Key key = (Key) o;
      return sceneKey.equals(key.sceneKey) && transitionType == key.transitionType && fromEntityKey
              .equals(key.fromEntityKey) && fromEntityType == key.fromEntityType && connectionType == key.connectionType && toEntityKey
              .equals(key.toEntityKey) && toEntityType == key.toEntityType;
    }

    @Override
    public int hashCode() {
      return Objects.hash(sceneKey, transitionType, fromEntityKey, fromEntityType, connectionType, toEntityKey, toEntityType);
    }

    @Override
    public String toString() {
      return "Key{" + "sceneKey='" + sceneKey + '\'' + ", transitionType=" + transitionType + ", fromEntityKey='" + fromEntityKey + '\'' + ", fromEntityType=" + fromEntityType + ", connectionType=" + connectionType + ", toEntityKey='" + toEntityKey + '\'' + ", toEntityType=" + toEntityType + '}';
    }
  }
}
