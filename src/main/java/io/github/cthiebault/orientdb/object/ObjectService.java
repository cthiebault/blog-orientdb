package io.github.cthiebault.orientdb.object;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.orientechnologies.common.exception.OException;
import com.orientechnologies.orient.core.index.OIndexManager;
import com.orientechnologies.orient.core.index.ONullOutputListener;
import com.orientechnologies.orient.core.index.OPropertyIndexDefinition;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.tx.OTransaction;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

import io.github.cthiebault.orientdb.commons.OrientDbServerFactory;
import io.github.cthiebault.orientdb.object.entity.AbstractOrientDbEntity;

@Component
public class ObjectService {

  @Autowired
  private OrientDbServerFactory serverFactory;

  public void registerEntityClass(Class<? extends AbstractOrientDbEntity>... classes) {
    try(OObjectDatabaseTx db = serverFactory.getObjectTx()) {
      for(Class<?> clazz : classes) {
        db.getEntityManager().registerEntityClass(clazz);
      }
    }
  }

  public void createIndex(Class<?> clazz, String property, OClass.INDEX_TYPE indexType, OType type) {
    try(OObjectDatabaseTx db = serverFactory.getObjectTx()) {
      String className = clazz.getSimpleName();
      int clusterId = db.getClusterIdByName(className.toLowerCase());
      OIndexManager indexManager = db.getMetadata().getIndexManager();
      indexManager.createIndex(className + "." + property, indexType.name(),
          new OPropertyIndexDefinition(className, property, type), new int[] { clusterId },
          ONullOutputListener.INSTANCE);
    }
  }

  public <TEntity extends AbstractOrientDbEntity> void save(TEntity entity) {
    OObjectDatabaseTx db = serverFactory.getObjectTx();
    try {
      db.begin(OTransaction.TXTYPE.OPTIMISTIC);
      db.save(entity);
      db.commit();
    } catch(OException e) {
      db.rollback();
      throw e;
    } finally {
      db.close();
    }
  }

  public <TEntity extends AbstractOrientDbEntity> Iterable<TEntity> list(Class<TEntity> clazz) {
    try(OObjectDatabaseTx db = serverFactory.getObjectTx()) {
      return Iterables.transform(db.browseClass(clazz), new Function<TEntity, TEntity>() {
        @Override
        public TEntity apply(TEntity input) {
          return db.detach(input, true);
        }
      });
    }
  }

  public <TEntity extends AbstractOrientDbEntity> Iterable<TEntity> list(String sql, Object... params) {
    try(OObjectDatabaseTx db = serverFactory.getObjectTx()) {
      Iterable<TEntity> entities = db.command(new OSQLSynchQuery(sql)).execute(params);
      return Iterables.transform(entities, new Function<TEntity, TEntity>() {
        @Override
        public TEntity apply(TEntity input) {
          return db.detach(input, true);
        }
      });
    }
  }

  public <T> T execute(OrientDbTransactionCallback<T> action) {
    OObjectDatabaseTx db = serverFactory.getObjectTx();
    try {
      db.begin(OTransaction.TXTYPE.OPTIMISTIC);
      T t = action.doInTransaction(db);
      db.commit();
      return t;
    } catch(OException e) {
      db.rollback();
      throw e;
    } finally {
      db.close();
    }
  }

  public interface OrientDbTransactionCallback<T> {
    T doInTransaction(OObjectDatabaseTx db);
  }

}
