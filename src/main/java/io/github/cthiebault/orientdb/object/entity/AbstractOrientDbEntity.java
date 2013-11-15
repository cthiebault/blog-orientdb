package io.github.cthiebault.orientdb.object.entity;

import javax.persistence.Id;
import javax.persistence.Version;

import com.google.common.base.Objects;

public abstract class AbstractOrientDbEntity {

  @Id
  private String id;

  @Version
  private Integer version;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj) return true;
    if(obj == null || getClass() != obj.getClass()) return false;
    return Objects.equal(id, ((AbstractOrientDbEntity) obj).id);
  }
}
