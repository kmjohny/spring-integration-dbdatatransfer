package org.jkm.springintegration.datatransfer.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.messaging.MessageHandler;

/**
 * Configuration class defining the logger flow for the system.
 */
@Configuration
public class Logger {
  /** Full message logger handler. */
  @Autowired
  private transient MessageHandler fullMessageLogger;

  /**
   * The logger flow logic for the system.
   *
   * @return IntegrationFlow object.
   */
  @Bean
  public IntegrationFlow loggerFlow() {
    return IntegrationFlows.from("logger.channel")
        .handle(fullMessageLogger).get();
  }
}
