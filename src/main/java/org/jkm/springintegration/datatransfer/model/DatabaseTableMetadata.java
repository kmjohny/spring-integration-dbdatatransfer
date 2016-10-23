package org.jkm.springintegration.datatransfer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DatabaseTableMetadata {
  @JsonProperty
  private transient String tableName;
  @JsonProperty(value = "columns")
  private transient List<DatabaseColumnMetadata> databaseColumnMetadatas;
}