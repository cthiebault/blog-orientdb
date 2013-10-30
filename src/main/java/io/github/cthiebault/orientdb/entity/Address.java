package io.github.cthiebault.orientdb.entity;

import com.google.common.base.Objects;

public class Address {

  private String street;

  private String city;

  private String zip;

  private String country;

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getZip() {
    return zip;
  }

  public void setZip(String zip) {
    this.zip = zip;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("street", street).add("city", city).add("zip", zip).add("country", country)
        .toString();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(street, city, zip, country);
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj) {
      return true;
    }
    if(obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Address other = (Address) obj;
    return Objects.equal(street, other.street) && Objects.equal(city, other.city) &&
        Objects.equal(zip, other.zip) && Objects.equal(country, other.country);
  }
}
