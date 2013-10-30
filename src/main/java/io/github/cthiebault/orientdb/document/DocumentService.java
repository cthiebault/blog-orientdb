package io.github.cthiebault.orientdb.document;

import java.util.List;

import javax.annotation.Nonnull;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orientechnologies.common.collection.OCompositeKey;
import com.orientechnologies.common.exception.OException;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.tx.OTransaction;

import io.github.cthiebault.orientdb.commons.OrientDbServerFactory;
import io.github.cthiebault.orientdb.entity.HasUniqueProperties;

@Component
public class DocumentService {

  @Autowired
  private OrientDbServerFactory serverFactory;

  private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

  public void createIndex(Class<?> clazz, String propertyPath, OClass.INDEX_TYPE indexType, OType type) {
    ODatabaseDocumentTx db = serverFactory.getDocumentTx();
    try {
      String className = clazz.getSimpleName();

      OClass indexClass;
      OSchema schema = db.getMetadata().getSchema();
      if(schema.existsClass(className)) {
        indexClass = schema.getClass(className);
      } else {
        indexClass = schema.createClass(className);
        schema.save();
      }

      OProperty property = indexClass.getProperty(propertyPath);
      if(property == null) {
        indexClass.createProperty(propertyPath, type);
        schema.save();
      }

      indexClass.createIndex(className + "." + propertyPath, indexType, propertyPath);

    } finally {
      db.close();
    }
  }

  public void createUniqueIndex(Class<?> clazz, String propertyPath, OType type) {
    createIndex(clazz, propertyPath, OClass.INDEX_TYPE.UNIQUE, type);
  }

  public void createUniqueStringIndex(Class<?> clazz, String propertyPath) {
    createUniqueIndex(clazz, propertyPath, OType.STRING);
  }

  public void createUniqueIndex(Class<? extends HasUniqueProperties> clazz) {
    ODatabaseDocumentTx db = serverFactory.getDocumentTx();
    try {
      String className = clazz.getSimpleName();

      OClass indexClass;
      OSchema schema = db.getMetadata().getSchema();
      if(schema.existsClass(className)) {
        indexClass = schema.getClass(className);
      } else {
        indexClass = schema.createClass(className);
        schema.save();
      }

      HasUniqueProperties hasUniqueProperties = BeanUtils.instantiate(clazz);
      List<String> uniqueProperties = hasUniqueProperties.getUniqueProperties();
      for(String propertyPath : uniqueProperties) {
        OProperty property = indexClass.getProperty(propertyPath);
        if(property == null) {
          // TODO fix type
          indexClass.createProperty(propertyPath, OType.STRING);
          schema.save();
        }
      }

      indexClass.createIndex(getIndexName(hasUniqueProperties), OClass.INDEX_TYPE.UNIQUE,
          uniqueProperties.toArray(new String[uniqueProperties.size()]));

    } finally {
      db.close();
    }
  }

  @SuppressWarnings("unchecked")
  public <T extends HasUniqueProperties> T findUnique(HasUniqueProperties template) {
    ODatabaseDocumentTx db = serverFactory.getDocumentTx();
    try {
      ODocument document = findUniqueDocument(db, template);
      return document == null ? null : (T) fromDocument(template.getClass(), document);
    } finally {
      db.close();
    }
  }

  public ODocument findUniqueDocument(ODatabaseDocumentTx db, HasUniqueProperties template) {
    OIndex<?> index = db.getMetadata().getIndexManager().getIndex(getIndexName(template));
    OIdentifiable identifiable;
    if(template.getUniqueValues().size() == 1) {
      identifiable = (OIdentifiable) index.get(template.getUniqueValues().get(0));
    } else {
      OCompositeKey key = new OCompositeKey(template.getUniqueValues());
      identifiable = (OIdentifiable) index.get(key);
    }
    return identifiable == null ? null : identifiable.<ODocument>getRecord();
  }

  public String getIndexName(HasUniqueProperties hasUniqueProperties) {
    StringBuilder indexName = new StringBuilder(hasUniqueProperties.getClass().getSimpleName());
    for(String prop : hasUniqueProperties.getUniqueProperties()) {
      indexName.append(".").append(prop);
    }
    return indexName.toString();
  }

  public <T> void save(@Nonnull HasUniqueProperties hasUniqueProperties) {

    ODatabaseDocumentTx db = serverFactory.getDocumentTx();
    try {
      ODocument document = findUniqueDocument(db, hasUniqueProperties);
      if(document == null) {
        document = toDocument(hasUniqueProperties);
      } else {
        copyToDocument(hasUniqueProperties, document);
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

  public <T> ODocument toDocument(T t) {
    ODocument document = new ODocument(t.getClass().getSimpleName());
    copyToDocument(t, document);
    return document;
  }

  public <T> void copyToDocument(T t, ODocument document) {
    document.fromJSON(gson.toJson(t));
  }

  public <T> T fromDocument(Class<T> clazz, ODocument document) {
    return gson.fromJson(document.toJSON(), clazz);
  }

}
