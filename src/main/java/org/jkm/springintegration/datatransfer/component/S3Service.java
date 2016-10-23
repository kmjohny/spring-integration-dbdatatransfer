package org.jkm.springintegration.datatransfer.component;

/**
 * Database service.
 */
public interface S3Service {
  String writeData(String tableName);
  String writeSpec(String tableName);
  String getS3KeyData(String tableName);
  String getS3KeySpec(String tableName);
}
