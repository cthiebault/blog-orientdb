package io.github.cthiebault.orientdb.document;

import java.beans.PropertyDescriptor;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orientechnologies.common.collection.OCompositeKey;
import com.orientechnologies.common.exception.OException;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.iterator.ORecordIteratorClass;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.tx.OTransaction;
import com.sun.istack.internal.NotNull;

import io.github.cthiebault.orientdb.commons.OrientDbServerFactory;
import io.github.cthiebault.orientdb.document.entity.HasUniqueProperties;

@Component
public class DocumentService {

  @Autowired
  private OrientDbServerFactory serverFactory;

  private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

  public void createUniqueIndex(Class<? extends HasUniqueProperties> clazz) {
    try(ODatabaseDocumentTx db = serverFactory.getDocumentTx()) {
      String className = clazz.getSimpleName();

      OClass indexClass;
      OSchema schema = db.getMetadata().getSchema();
      if(schema.existsClass(className)) {
        indexClass = schema.getClass(className);
      } else {
        indexClass = schema.createClass(className);
        schema.save();
      }

      StringBuilder indexName = new StringBuilder(clazz.getSimpleName());
      HasUniqueProperties bean = BeanUtils.instantiate(clazz);
      List<String> uniqueProperties = bean.getUniqueProperties();
      for(String propertyPath : uniqueProperties) {
        indexName.append(".").append(propertyPath);
        OProperty property = indexClass.getProperty(propertyPath);
        if(property == null) {
          PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(clazz, propertyPath);
          indexClass.createProperty(propertyPath, OType.getTypeByClass(propertyDescriptor.getPropertyType()));
          schema.save();
        }
      }

      indexClass.createIndex(indexName.toString(), OClass.INDEX_TYPE.UNIQUE,
          uniqueProperties.toArray(new String[uniqueProperties.size()]));
    }
  }

  public void save(HasUniqueProperties template, HasUniqueProperties entity) {

    ODatabaseDocumentTx db = serverFactory.getDocumentTx();
    try {

      // use the index to search for document that match our template
      ODocument document = findUniqueDocument(db, template);
      if(document == null) {
        document = new ODocument(entity.getClass().getSimpleName());
        document.fromJSON(gson.toJson(entity));
      } else {
        document.fromJSON(gson.toJson(entity));
      }

      db.begin(OTransaction.TXTYPE.OPTIMISTIC);
      document.save();
      db.commit();

    } catch(OException e) {
      db.rollback();
      throw e;
    } finally {
      db.close();
    }
  }

  private ODocument findUniqueDocument(ODatabaseDocumentTx db, HasUniqueProperties template) {

    // index name pattern is
    StringBuilder indexName = new StringBuilder(template.getClass().getSimpleName());
    for(String prop : template.getUniqueProperties()) {
      indexName.append(".").append(prop);
    }
    OIndex<?> index = db.getMetadata().getIndexManager().getIndex(indexName.toString());

    Object key = template.getUniqueValues().size() == 1
        ? template.getUniqueValues().get(0)
        : new OCompositeKey(template.getUniqueValues());

    OIdentifiable identifiable = (OIdentifiable) index.get(key);
    return identifiable == null ? null : identifiable.<ODocument>getRecord();
  }

  @SuppressWarnings("unchecked")
  public <T extends HasUniqueProperties> T findUnique(@NotNull HasUniqueProperties template) {
    try(ODatabaseDocumentTx db = serverFactory.getDocumentTx()) {
      ODocument document = findUniqueDocument(db, template);
      return document == null ? null : (T) gson.fromJson(document.toJSON(), template.getClass());
    }
  }

  public <T> Iterable<T> list(final Class<T> clazz) {
    try(ODatabaseDocumentTx db = serverFactory.getDocumentTx()) {
      ORecordIteratorClass<ODocument> documents = db.browseClass(clazz.getSimpleName());
      return Iterables.transform(documents, new Function<ODocument, T>() {
        @Override
        public T apply(ODocument document) {
          return gson.fromJson(document.toJSON(), clazz);
        }
      });
    }
  }

  public <T> Iterable<T> list(final Class<T> clazz, String sql, Object... params) {
    try(ODatabaseDocumentTx db = serverFactory.getDocumentTx()) {
      List<ODocument> documents = db.query(new OSQLSynchQuery<ODocument>(sql), params);
      return Iterables.transform(documents, new Function<ODocument, T>() {
        @Override
        public T apply(ODocument document) {
          return gson.fromJson(document.toJSON(), clazz);
        }
      });
    }
  }

  public <T> T execute(OrientDbTransactionCallback<T> callback) {
    ODatabaseDocumentTx db = serverFactory.getDocumentTx();
    try {
      return callback.doInTransaction(db);
    } catch(OException e) {
      db.rollback();
      throw e;
    } finally {
      db.close();
    }
  }

  interface OrientDbTransactionCallback<T> {
    T doInTransaction(ODatabaseDocumentTx db);
  }
}
