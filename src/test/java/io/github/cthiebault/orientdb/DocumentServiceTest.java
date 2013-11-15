package io.github.cthiebault.orientdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import io.github.cthiebault.orientdb.commons.OrientDbServerFactory;
import io.github.cthiebault.orientdb.document.DocumentService;
import io.github.cthiebault.orientdb.document.entity.Acl;
import io.github.cthiebault.orientdb.document.entity.Address;
import io.github.cthiebault.orientdb.document.entity.Group;
import io.github.cthiebault.orientdb.document.entity.User;

@ContextConfiguration("classpath:applicationContext-test.xml")
public class DocumentServiceTest extends AbstractTestNGSpringContextTests {

  private static final Logger log = LoggerFactory.getLogger(OrientDbServerFactory.class);

  @Autowired
  private DocumentService documentService;

  @Test
  public void testPersist() {

    documentService.createUniqueIndex(User.class);

    User user = createUser();

    log.debug("user: {}", user);

    documentService.save(user, user);

    User found = documentService.findUnique(user);
    log.debug("found: {}", found);

    AssertJUnit.assertEquals(found, user);

    int nbAddresses = user.getAddresses().size();
    for(int i = 0; i < nbAddresses; i++) {
      AssertJUnit.assertEquals(found.getAddresses().get(i), user.getAddresses().get(i));
    }
  }

  @Test
  public void testPersistComposedIndex() {

    documentService.createUniqueIndex(Acl.class);

    Acl acl = new Acl("cthiebault", "create", "user");
    log.debug("acl: {}", acl);

    documentService.save(acl, acl);

    Acl found = documentService.findUnique(acl);

    log.debug("found: {}", found);

    AssertJUnit.assertEquals(found, acl);
  }

  @Test
  public void testPersistRelatedDocuments() {

    documentService.createUniqueIndex(User.class);
    documentService.createUniqueIndex(Group.class);

    User user = createUser();
    user.getGroups().add(new Group("group1"));
    user.getGroups().add(new Group("group2"));
    documentService.save(user, user);

    User found = documentService.findUnique(user);
    log.debug("found: {}", found);
    AssertJUnit.assertEquals(found, user);

    int nbGroups = user.getGroups().size();
    for(int i = 0; i < nbGroups; i++) {
      AssertJUnit.assertEquals(found.getGroups().get(i), user.getGroups().get(i));
    }

//    AssertJUnit.assertEquals(2, documentService.count(Group.class));

    Iterable<Group> groups = documentService.list(Group.class);
    log.debug("groups: {}", groups);

  }

  private static User createUser() {
    User user = new User();
    user.setFirstName("Cedric");
    user.setLastName("Thiebault");
    user.setUsername("cthiebault");
    user.setEmail("cedric.thiebault@gmail.com");

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

    user.setAddresses(Lists.newArrayList(address1, address2));
    return user;
  }

}