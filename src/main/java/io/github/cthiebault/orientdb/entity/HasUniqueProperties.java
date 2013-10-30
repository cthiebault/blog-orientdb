package io.github.cthiebault.orientdb.entity;

import java.util.List;

public interface HasUniqueProperties {

  List<String> getUniqueProperties();

  List<Object> getUniqueValues();

}
