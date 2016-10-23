package org.jkm.springintegration.datatransfer.component;

import org.jkm.springintegration.datatransfer.model.DatabaseTableMetadata;

import java.nio.file.Path;
import java.util.List;

/**
 * Database service.
 */
public interface DatabaseService {
  List<String> getTables();

  DatabaseTableMetadata getDatabaseTableMetadata(String tableName);

  List<String> getColumns(String tableName);

  String getData(String tableName);

  Path getDataPath(String tableName);

  Path getSpecPath(String fileName);
}
