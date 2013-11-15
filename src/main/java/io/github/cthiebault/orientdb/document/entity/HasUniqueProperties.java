package io.github.cthiebault.orientdb.document.entity;

import java.util.List;

public interface HasUniqueProperties {

  List<String> getUniqueProperties();

  List<Object> getUniqueValues();

}
