package io.github.cthiebault.orientdb.document.entity;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

public class Group implements HasUniqueProperties {

  @Nonnull
  private String name;

  public Group() {
  }

  public Group(@Nonnull String name) {
    this.name = name;
  }

  @Nonnull
  public String getName() {
    return name;
  }

  public void setName(@Nonnull String name) {
    this.name = name;
  }

  @Override
  public List<String> getUniqueProperties() {
    return Lists.newArrayList("name");
  }

  @Override
  public List<Object> getUniqueValues() {
    return Lists.<Object>newArrayList(name);
  }

  @Override
  public boolean equals(Object o) {
    if(this == o) return true;
    if(!(o instanceof Group)) return false;
    Group group = (Group) o;
    return name.equals(group.name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("name", name).toString();
  }
}
