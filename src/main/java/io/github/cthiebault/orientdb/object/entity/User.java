package io.github.cthiebault.orientdb.object.entity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.base.Objects;

import io.github.cthiebault.orientdb.document.entity.Address;
import io.github.cthiebault.orientdb.document.entity.Group;

public class User extends AbstractOrientDbEntity {

  @Nonnull
  private String firstName;

  @Nonnull
  private String lastName;

  @Nonnull
  private String username;

  private String email;

  private List<Address> addresses = new ArrayList<>();

  private List<Group> groups = new ArrayList<>();

  @Nonnull
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(@Nonnull String firstName) {
    this.firstName = firstName;
  }

  @Nonnull
  public String getLastName() {
    return lastName;
  }

  public void setLastName(@Nonnull String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public List<Address> getAddresses() {
    return addresses;
  }

  public void setAddresses(List<Address> addresses) {
    this.addresses = addresses;
  }

  @Nonnull
  public String getUsername() {
    return username;
  }

  public void setUsername(@Nonnull String username) {
    this.username = username;
  }

  public List<Group> getGroups() {
    return groups;
  }

  public void setGroups(List<Group> groups) {
    this.groups = groups;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("id", getId()).add("firstName", firstName).add("lastName", lastName)
        .add("username", username).add("email", email).add("groups", groups).add("addresses", addresses).toString();
  }
}
