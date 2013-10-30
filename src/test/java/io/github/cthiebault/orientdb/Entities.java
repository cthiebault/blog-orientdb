package io.github.cthiebault.orientdb;

import com.google.common.collect.Lists;

import io.github.cthiebault.orientdb.entity.Address;
import io.github.cthiebault.orientdb.entity.Person;

public class Entities {

  public static Person createPerson() {
    Person person = new Person();
    person.setFirstName("Cedric");
    person.setLastName("Thiebault");
    person.setUsername("cthiebault");
    person.setEmail("cedric.thiebault@gmail.com");

    Address address1 = new Address();
    address1.setStreet("street1");
    address1.setCity("city1");
    address1.setZip("zip1");
    address1.setCountry("country1");

    Address address2 = new Address();
    address2.setStreet("street2");
    address2.setCity("city2");
    address2.setZip("zip2");
    address2.setCountry("country2");

    person.setAddresses(Lists.newArrayList(address1, address2));
    return person;
  }

}
