package io.github.cthiebault.orientdb.entity;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

public class Acl implements HasUniqueProperties {

  @Nonnull
  private String username;

  @Nonnull
  private String permission;

  @Nonnull
  private String resource;

  public Acl() {
  }

  public Acl(@Nonnull String username, @Nonnull String permission, @Nonnull String resource) {
    this.username = username;
    this.permission = permission;
    this.resource = resource;
  }

  @Nonnull
  public String getUsername() {
    return username;
  }

  public void setUsername(@Nonnull String username) {
    this.username = username;
  }

  @Nonnull
  public String getPermission() {
    return permission;
  }

  public void setPermission(@Nonnull String permission) {
    this.permission = permission;
  }

  @Nonnull
  public String getResource() {
    return resource;
  }

  public void setResource(@Nonnull String resource) {
    this.resource = resource;
  }

  @Override
  public List<String> getUniqueProperties() {
    return Lists.newArrayList("username", "permission", "resource");
  }

  @Override
  public List<Object> getUniqueValues() {
    return Lists.<Object>newArrayList(username, permission, resource);
  }

  @Override
  public boolean equals(Object o) {
    if(this == o) return true;
    if(!(o instanceof Acl)) return false;
    Acl acl = (Acl) o;
    return permission.equals(acl.permission) && resource.equals(acl.resource) && username.equals(acl.username);
  }

  @Override
  public int hashCode() {
    int result = username.hashCode();
    result = 31 * result + permission.hashCode();
    result = 31 * result + resource.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("username", username).add("permission", permission)
        .add("resource", resource).toString();
  }
}
