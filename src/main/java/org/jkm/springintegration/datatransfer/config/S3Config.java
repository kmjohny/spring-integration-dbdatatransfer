package org.jkm.springintegration.datatransfer.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class defining LoggingHandlers for the system.
 */
@Configuration
public class S3Config {

  @Value("${aws.s3.accessKey}")
  private String accessKey;

  @Value("${aws.s3.secretKey}")
  private String secretKey;

  @Bean
  public AWSCredentials awsCredentials() {
    return new BasicAWSCredentials(accessKey, secretKey);
  }

  @Bean
  public AmazonS3 amazonS3(AWSCredentials awsCredentials) {
    AmazonS3 amazonS3 = new AmazonS3Client(awsCredentials);
    amazonS3.setRegion(Region.getRegion(Regions.US_EAST_1));
    return amazonS3;
  }
}