package org.jkm.springintegration.datatransfer.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.jdbc.JdbcPollingChannelAdapter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;

import javax.sql.DataSource;

/**
 * Configuration class for the data transfer system.
 */
@Configuration
public class Database {
  /**
   * DB url.
   */
  @Value("${spring.datasource.url}")
  private transient String url;

  /**
   * DB username.
   */
  @Value("${spring.datasource.username}")
  private transient String username;

  /**
   * DB Password.
   */
  @Value("${spring.datasource.password}")
  private transient String password;

  /**
   * DB driver class name.
   */
  @Value("${spring.datasource.driver-class-name}")
  private transient String driverClassName;

  /**
   * Bean representing the datasource
   *
   * @return DataSource DB datasource
   */
  @Bean
  public DataSource datasource() {
    final BasicDataSource datasource = new BasicDataSource();
    datasource.setUrl(url);
    datasource.setUsername(username);
    datasource.setPassword(password);
    datasource.setDriverClassName(driverClassName);
    return datasource;
  }

  /**
   * Bean representing the jdbcTemplate
   *
   * @param datasource DB Datasource object.
   * @return JdbcTemplate DB JdbcTemplate object.
   */
  @Bean
  public JdbcTemplate jdbcTemplate(DataSource datasource) {
    final JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
    jdbcTemplate.setFetchSize(10000);
    return jdbcTemplate;
  }
}
