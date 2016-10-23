package org.jkm.springintegration.datatransfer.flow;

import org.jkm.springintegration.datatransfer.component.DatabaseService;
import org.jkm.springintegration.datatransfer.component.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.dsl.core.Pollers;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.json.ObjectToJsonTransformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import java.io.File;
import java.util.concurrent.Executors;

/**
 * Configuration class defining the CDRIS data transfer flow for the system.
 */
@Configuration (value = "DatabaseConfig")
public class Database {
  @Autowired
  private transient DatabaseService databaseServiceImpl;

  @Autowired
  private transient S3Service s3ServiceImpl;

  @Value("${dataTransfer.outputDataLocation}")
  private String outputDataLocation;


  @Bean
  public IntegrationFlow databaseChannelFlow() {
    return IntegrationFlows.from("database.channel")
        .split(databaseServiceImpl, "getTables")
        .enrichHeaders( h -> h.headerExpression("tableName", "payload"))
        .channel(MessageChannels.executor(Executors.newFixedThreadPool(10)))
        .handle(databaseServiceImpl, "getData")
        .handle(s3ServiceImpl, "writeData")
        .handle(databaseServiceImpl, "getDatabaseTableMetadata")
        .transform(new ObjectToJsonTransformer())
        .enrichHeaders(h -> h.headerExpression(FileHeaders.FILENAME,
            "'spec-' + headers[tableName] + '.json'")
            .header("directory", new File(outputDataLocation)))
        .handleWithAdapter(a -> a.fileGateway(m -> m.getHeaders().get("directory")))
        .handle(s3ServiceImpl, "writeSpec")
        .aggregate()
//        .channel("logger.channel")
//        .channel("exit.channel")
        .get();
  }

  @Bean
  public IntegrationFlow exitFlow() {
    return IntegrationFlows.from("exit.channel")
        .handle(new MessageHandler() {
          @Override
          public void handleMessage(Message<?> message) throws MessagingException {
            System.exit(0);
          }
        })
        .get();
  }
}