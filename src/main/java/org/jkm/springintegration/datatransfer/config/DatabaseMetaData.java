package org.jkm.springintegration.datatransfer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class DatabaseMetaData {
  @Value("${dataTransfer.dbMetaData.catalog}")
  private transient String catalog;

  @Value("${dataTransfer.dbMetaData.schemaPattern?:#{null}}")
  private transient String schemaPattern;

  @Value("${dataTransfer.dbMetaData.tableNamePattern?:#{null}}")
  private transient String tableNamePattern;

  @Value("${dataTransfer.dbMetaData.tableTypes}")
  private transient String [] tableTypes;
}
