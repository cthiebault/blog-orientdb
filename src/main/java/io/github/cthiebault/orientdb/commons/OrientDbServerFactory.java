package io.github.cthiebault.orientdb.commons;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentPool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.object.db.OObjectDatabasePool;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.server.OServer;

@Component
public class OrientDbServerFactory {

  private static final Logger log = LoggerFactory.getLogger(OrientDbServerFactory.class);

  private String url;

  private String username;

  private String password;

  private OServer server;

  @Value("${orientdb.url}")
  public void setUrl(@Nonnull String url) {
    this.url = url;
  }

  @Value("${orientdb.username}")
  public void setUsername(String username) {
    this.username = username;
  }

  @Value("${orientdb.password}")
  public void setPassword(String password) {
    this.password = password;
  }

  @PostConstruct
  public void start() throws Exception {
    log.info("Start OrientDB server ({})", url);

    System.setProperty("ORIENTDB_HOME", url);
    server = new OServer().startup().activate();

    // create database if does not exist
    ODatabase database = new OObjectDatabaseTx(url);
    if(!database.exists()) database.create();
    database.close();
  }

  @PreDestroy
  public void stop() {
    log.info("Stop OrientDB server ({})", url);
    if(server != null) server.shutdown();
  }

  @Nonnull
  public OServer getServer() {
    return server;
  }

  @Nonnull
  public OObjectDatabaseTx getObjectTx() {
    return OObjectDatabasePool.global().acquire(url, username, password);
  }

  @Nonnull
  public ODatabaseDocumentTx getDocumentTx() {
    return ODatabaseDocumentPool.global().acquire(url, username, password);
  }

}
