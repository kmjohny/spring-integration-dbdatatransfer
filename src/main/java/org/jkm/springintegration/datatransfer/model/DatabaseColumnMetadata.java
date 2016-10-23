package org.jkm.springintegration.datatransfer.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DatabaseColumnMetadata {
  @JsonProperty
  private transient String columnName;
  @JsonProperty
  private transient String columnType;
  @JsonProperty
  private transient Integer columnSize;
  @JsonProperty
  private transient Integer decimalDigits;
  @JsonProperty
  private transient Boolean nullable;
}