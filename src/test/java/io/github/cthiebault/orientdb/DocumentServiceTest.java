package io.github.cthiebault.orientdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import io.github.cthiebault.orientdb.commons.OrientDbServerFactory;
import io.github.cthiebault.orientdb.document.DocumentService;
import io.github.cthiebault.orientdb.entity.Person;

@ContextConfiguration("classpath:applicationContext-test.xml")
public class DocumentServiceTest extends AbstractTestNGSpringContextTests {

  private static final Logger log = LoggerFactory.getLogger(OrientDbServerFactory.class);

  @Autowired
  private DocumentService documentService;

  @Test
  public void testPersist() {

    documentService.createUniqueIndex(Person.class);

    Person person = Entities.createPerson();

    log.debug("person: {}", person);

    documentService.save(person);

    Person found = documentService.findUnique(person);
    log.debug("found: {}", found);

    AssertJUnit.assertEquals(found, person);

    int nbAddresses = person.getAddresses().size();
    for(int i = 0; i < nbAddresses; i++) {
      AssertJUnit.assertEquals(found.getAddresses().get(i), person.getAddresses().get(i));
    }

  }

}


