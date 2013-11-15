package io.github.cthiebault.orientdb.document.entity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

public class User implements HasUniqueProperties {

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
  public List<String> getUniqueProperties() {
    return Lists.newArrayList("username");
  }

  @Override
  public List<Object> getUniqueValues() {
    return Lists.<Object>newArrayList(username);
  }

  @Override
  public boolean equals(Object o) {
    if(this == o) return true;
    if(!(o instanceof User)) return false;
    User user = (User) o;
    return firstName.equals(user.firstName) && lastName.equals(user.lastName);
  }

  @Override
  public int hashCode() {
    int result = firstName.hashCode();
    result = 31 * result + lastName.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("firstName", firstName).add("lastName", lastName).add("username", username)
        .add("email", email).add("groups", groups).add("addresses", addresses).toString();
  }
}
