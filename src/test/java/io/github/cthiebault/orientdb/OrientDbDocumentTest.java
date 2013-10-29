package io.github.cthiebault.orientdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import io.github.cthiebault.orientdb.commons.OrientDbServerFactory;

@ContextConfiguration("classpath:applicationContext-test.xml")
public class OrientDbDocumentTest extends AbstractTestNGSpringContextTests {

  private static final Logger log = LoggerFactory.getLogger(OrientDbServerFactory.class);

  @Autowired
  private OrientDbServerFactory orientDbServerFactory;

  @Test
  public void testDocument() {

  }
}
