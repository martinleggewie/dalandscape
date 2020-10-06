package com.ing.diba.dl.dalandscape.domain.model.storyboard;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dascenes")
public class DaScene {

  @Id
  private String key;

  @Column(name = "displayname")
  private String displayName;

  public DaScene() {
    // empty constructor needed because this is unfortunately a JPA requirement
  }

  public DaScene(String key, String displayName) {
    this.key = key;
    this.displayName = displayName;
  }

  public String getKey() {
    return key;
  }

  public String getDisplayName() {
    return displayName;
  }

  @Override
  public String toString() {
    return "DaScene{" + "key='" + key + '\'' + ", displayName='" + displayName + '\'' + '}';
  }
}
