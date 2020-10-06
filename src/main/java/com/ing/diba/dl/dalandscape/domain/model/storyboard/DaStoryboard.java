package com.ing.diba.dl.dalandscape.domain.model.storyboard;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dastoryboards")
public class DaStoryboard {

  // FetchType.EAGER used intentionally because of use of H2 as an in-memory database.
  @ManyToMany(fetch = FetchType.EAGER)
  @OrderColumn(name = "sortindex")
  @JoinTable(name = "dastoryboardscontainscenes", joinColumns = {@JoinColumn(name = "storyboardkey")}, inverseJoinColumns =
          {@JoinColumn(name = "scenekey")})
  private final List<DaScene> scenes = new ArrayList<>();
  @Id
  private String key;
  @Column(name = "displayname")
  private String displayName;

  public DaStoryboard() {
    // empty constructor needed because this is unfortunately a JPA requirement
  }

  public DaStoryboard(String key, String displayName) {
    this.key = key;
    this.displayName = displayName;
  }

  public String getKey() {
    return key;
  }

  public String getDisplayName() {
    return displayName;
  }

  public List<DaScene> getScenes() {
    return scenes;
  }

  @Override
  public String toString() {
    return "DaStoryboard{" + "key='" + key + '\'' + ", displayName='" + displayName + '\'' + ", scenes=" + scenes + '}';
  }
}
