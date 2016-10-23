package org.jkm.springintegration.datatransfer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.messaging.MessageHandler;

/**
 * Configuration class defining LoggingHandlers for the system.
 */
@Configuration
public class Logging {
  /** Logger name for message logger. */
  private static final String MESSAGE_LOGGER_NAME
      = "org.jkm.springintegration.datatransfer.messageLogger";

  /** Logger name for full message logger. */
  private static final String FULL_MESSAGE_LOGGER_NAME
      = "org.jkm.springintegration.datatransfer.fullMessageLogger";


  /**
   * Log message at INFO level.
   *
   * @return MessageHandler
   */
  @Bean
  public MessageHandler infoLogger() {
    final LoggingHandler loggingHandler = new LoggingHandler(LoggingHandler.Level.INFO.name());
    loggingHandler.setLoggerName(MESSAGE_LOGGER_NAME);
    loggingHandler.setShouldLogFullMessage(true);
    return loggingHandler;
  }


  /**
   * Log full message at TRACE level.
   *
   * @return MessageHandler
   */
  @Bean
  public MessageHandler fullMessageLogger() {
    final LoggingHandler loggingHandler = new LoggingHandler(LoggingHandler.Level.TRACE.name());
    loggingHandler.setLoggerName(FULL_MESSAGE_LOGGER_NAME);
    loggingHandler.setShouldLogFullMessage(true);
    return loggingHandler;
  }
}
