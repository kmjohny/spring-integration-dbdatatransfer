package org.jkm.springintegration.datatransfer.component;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Paths;

/**
 * Database service.
 */
@Component
public class S3ServiceImpl implements S3Service {
  @Value("${aws.s3.s3Bucket}")
  private String s3Bucket;

  @Value("${dataTransfer.outputDataLocation}")
  private String outputDataLocation;

  @Autowired
  private transient DatabaseService databaseServiceImpl;

  @Autowired
  private transient AmazonS3 amazonS3;

  public String writeData(String tableName) {
    writeToS3(getS3KeyData(tableName),
        databaseServiceImpl.getDataPath (tableName).toFile());
    return tableName;
  }

  public String writeSpec(String fileName) {

    System.out.println(getS3KeySpec(fileName));


    writeToS3(getS3KeySpec(fileName),
        Paths.get (fileName).toFile());
    return fileName;
  }

  public String getS3KeyData(String tableName) {
    return tableName.toLowerCase();
  }

  public String getS3KeySpec(String fileName) {
    return "spec/" + fileName.substring(fileName.indexOf("spec-"))
        .replaceFirst("spec-", "")
        .replaceFirst(".json", "")
        .toLowerCase();
  }

  private void writeToS3(String keyName, File fileName) {
    PutObjectRequest putObjectRequest = new PutObjectRequest(s3Bucket,
        keyName, fileName);
    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setSSEAlgorithm(
        ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
    putObjectRequest.withMetadata(objectMetadata);

    amazonS3.putObject(putObjectRequest);
  }
}
