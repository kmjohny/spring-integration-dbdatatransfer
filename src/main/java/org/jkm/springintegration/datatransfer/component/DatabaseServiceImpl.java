package org.jkm.springintegration.datatransfer.component;

import org.jkm.springintegration.datatransfer.model.DatabaseColumnMetadata;
import org.jkm.springintegration.datatransfer.model.DatabaseTableMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.DatabaseMetaDataCallback;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.sql.DataSource;

/**
 * Database service.
 */
@Component
public class DatabaseServiceImpl implements DatabaseService{
//  private static final String SELECT_DATA = "SELECT * FROM CDBUser.\"%s\";";
  private static final String SELECT_DATA = "SELECT * FROM \"%s\";";

  @Value("${dataTransfer.outputDataLocation}")
  private String outputDataLocation;

  @Autowired
  private transient org.jkm.springintegration.datatransfer.config.DatabaseMetaData databaseMetaData;

  @Autowired
  private DataSource datasource;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public List<String> getTables() {
    List<String> tables = null;
    try {
      tables = (List<String>) JdbcUtils.extractDatabaseMetaData(datasource,
          new DatabaseMetaDataCallback() {
            @Override
            public Object processMetaData(DatabaseMetaData dbmd) throws SQLException, MetaDataAccessException {
              ResultSet rs = dbmd.getTables(databaseMetaData.getCatalog(), databaseMetaData
                  .getSchemaPattern(), databaseMetaData.getTableNamePattern(), databaseMetaData.getTableTypes());

              List list = new ArrayList();
              while (rs.next()) {
                list.add(rs.getString("TABLE_NAME"));
              }
              return list;
            }
          });
    } catch (MetaDataAccessException e) {
      //TODO Handle exceptions
      e.printStackTrace();
    }
    return tables;
  }

  public DatabaseTableMetadata getDatabaseTableMetadata(String tableName){
    DatabaseTableMetadata databaseTableMetadata = null;
    try {
      databaseTableMetadata = (DatabaseTableMetadata) JdbcUtils.extractDatabaseMetaData(datasource,
          new DatabaseMetaDataCallback() {
            @Override
            public Object processMetaData(DatabaseMetaData dbmd) throws SQLException, MetaDataAccessException {
              DatabaseTableMetadata dbTableMetadata = new DatabaseTableMetadata();
              dbTableMetadata.setTableName(tableName);

              ResultSet rs = dbmd.getColumns(databaseMetaData.getCatalog(), null, tableName, null);

              List<DatabaseColumnMetadata> databaseColumnMetadatas = new
                  ArrayList<DatabaseColumnMetadata>();
              while (rs.next()) {
                DatabaseColumnMetadata databaseColumnMetadata = new DatabaseColumnMetadata();
                databaseColumnMetadata.setColumnName(rs.getString("COLUMN_NAME"));
                databaseColumnMetadata.setColumnType(rs.getString("TYPE_NAME"));
                databaseColumnMetadata.setColumnSize(rs.getInt("COLUMN_SIZE"));
                databaseColumnMetadata.setDecimalDigits(rs.getInt("DECIMAL_DIGITS"));
                databaseColumnMetadata.setNullable(rs.getBoolean("IS_NULLABLE"));

                databaseColumnMetadatas.add(databaseColumnMetadata);
              }
              dbTableMetadata.setDatabaseColumnMetadatas(databaseColumnMetadatas);
              return dbTableMetadata;
            }
          });

    } catch (MetaDataAccessException e) {
      //TODO Handle exceptions
      e.printStackTrace();
    }
    return databaseTableMetadata;
  }

  @Override
  public List<String> getColumns(String tableName) {
    List<String> columns = null;
    try {
      columns = (List<String>) JdbcUtils.extractDatabaseMetaData(datasource,
          new DatabaseMetaDataCallback() {
            @Override
            public Object processMetaData(DatabaseMetaData dbmd) throws SQLException, MetaDataAccessException {
              ResultSet rs = dbmd.getColumns(databaseMetaData.getCatalog(), null, tableName, null);

              List<String> list = new ArrayList<String>();
              while (rs.next()) {
                list.add(rs.getString("COLUMN_NAME"));
              }
              return list;
            }
          });

    } catch (MetaDataAccessException e) {
      //TODO Handle exceptions
      e.printStackTrace();
    }
    return columns;
  }

  @Override
  public String getData(String tableName) {
    final BufferedWriter bufferedWriter = createWriter(tableName);
    final Date before = new Date();
    final AtomicLong count = new AtomicLong();

    try{
      List<String> columns = getColumns(tableName);

      bufferedWriter.write(columns.stream().collect(Collectors.joining(",")));
      bufferedWriter.newLine();

      jdbcTemplate.query(String.format(SELECT_DATA, tableName), new RowCallbackHandler(){
        public void processRow(ResultSet resultSet) throws SQLException {
          try {

            bufferedWriter.write(mapRow(resultSet));
            bufferedWriter.newLine();
            final long i = count.incrementAndGet();
            if (i % 500000 == 0) {
              System.out.println(tableName + " (" + new Date() + ") :- Processed " + i + " rows");
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
    finally {
      try {
        bufferedWriter.flush();
        bufferedWriter.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    final Date after = new Date();
    final long duration = after.getTime() - before.getTime();
    System.out.println(tableName + " (" + new Date() + ") :- Processing '" + count.get() + "' records took " +
        duration + " ms.");

    return tableName;
  }

  private BufferedWriter createWriter (String tableName) {
    BufferedWriter bufferedWriter = null;
    try {
      bufferedWriter = Files.newBufferedWriter(getDataPath(tableName));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bufferedWriter;
  }

  public Path getDataPath(String tableName) {
    return Paths.get(outputDataLocation, tableName + ".csv");
  }

  public Path getSpecPath(String fileName) {
    return Paths.get(outputDataLocation, fileName);
  }

  private String mapRow(ResultSet resultSet) throws SQLException {
    StringBuffer stringBuffer = new StringBuffer();
    ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
    int columnCount = resultSetMetaData.getColumnCount();

//    System.out.print(System.lineSeparator());
//    System.out.println("ColumnName,ColumnLabel,ColumnClassName,CatalogName,ColumnType," +
//        "ColumnTypeName,SchemaName,TableName,ColumnDisplaySize,Precision,Scale");

//    System.out.println("ColumnName,ColumnTypeName,TableName,ColumnDisplaySize,Precision,Scale");

    for (int column = 1; column <= columnCount; column++){



//      System.out.print("ColumnName :- " + resultSetMetaData.getColumnName(column) + ",");
//      System.out.print("ColumnLabel :- " + resultSetMetaData.getColumnLabel(column) + ",");
//      System.out.print("ColumnClassName :- " + resultSetMetaData.getColumnClassName(column) + ",");
//      System.out.print("CatalogName :- " + resultSetMetaData.getCatalogName(column) + ",");
//      System.out.print("ColumnType :- " + resultSetMetaData.getColumnType(column) + ",");
//      System.out.print("ColumnTypeName :- " + resultSetMetaData.getColumnTypeName(column) + ",");
//      System.out.print("SchemaName :- " + resultSetMetaData.getSchemaName(column) + ",");
//      System.out.print("TableName :- " + resultSetMetaData.getTableName(column) + ",");
//      System.out.print("ColumnDisplaySize :- " + resultSetMetaData.getColumnDisplaySize(column) + ",");
//      System.out.print("Precision :- " + resultSetMetaData.getPrecision(column) + ",");
//      System.out.print("Scale :- " + resultSetMetaData.getScale(column) + ",");




//      System.out.print(resultSetMetaData.getColumnName(column) + ",");
//      System.out.print(resultSetMetaData.getColumnLabel(column) + ",");
//      System.out.print(resultSetMetaData.getColumnClassName(column) + ",");
//      System.out.print(resultSetMetaData.getCatalogName(column) + ",");
//      System.out.print(resultSetMetaData.getColumnType(column) + ",");
//      System.out.print(resultSetMetaData.getColumnTypeName(column) + ",");
//      System.out.print(resultSetMetaData.getSchemaName(column) + ",");
//      System.out.print(resultSetMetaData.getTableName(column) + ",");
//      System.out.print(resultSetMetaData.getColumnDisplaySize(column) + ",");
//      System.out.print(resultSetMetaData.getPrecision(column) + ",");
//
//      System.out.print(resultSetMetaData.getScale(column));
//
//      System.out.print(System.lineSeparator());



      stringBuffer.append(resultSet.getString(column));
      if (column != columnCount) {
        stringBuffer.append(",");
      }
    }
    return stringBuffer.toString();
  }
}
