package com.jay.presentation;

import com.jay.domain.DataPatchInsertRequest;
import com.jay.domain.DataPatchInsertResponse;
import io.swagger.annotations.ApiOperation;
import java.sql.DatabaseMetaData;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/sqlDataPatch")
public class SqlDataPatchController {

  private final Logger logger = LoggerFactory.getLogger(SqlDataPatchController.class);

  @Autowired
  private JdbcTemplate jdbcTemplate;

  /**
   * 產生 dataPatch insert SQL 字串
   *
   * @param
   * @return
   */
  @ApiOperation(value = "genInsertSql", notes = "")
  @RequestMapping(value = "/getInsertSql", method = RequestMethod.POST)
  DataPatchInsertResponse getInsertSql(@RequestBody DataPatchInsertRequest dataPatchInsertRequest)
      throws SQLException {

    String tableName = dataPatchInsertRequest.getTableName();

    return  null;
  }

  /**
   * 產生 dataPatch insert SQL 字串
   *
   * @param
   * @return
   */
  @ApiOperation(value = "genInsertSql", notes = "")
  @RequestMapping(value = "/genInsertSql", method = RequestMethod.POST)
  DataPatchInsertResponse genInsertSql(@RequestBody DataPatchInsertRequest dataPatchInsertRequest)
      throws SQLException {

    logger.info("dataPatchInsertRequest:{}", dataPatchInsertRequest);


    DataPatchInsertResponse response = new DataPatchInsertResponse();


    String tableName = dataPatchInsertRequest.getTableName();

    List<String> insertedColumns = dataPatchInsertRequest.getInsertColumns();

    String sequenceColumnName = dataPatchInsertRequest.getSequneceColumnName();

    List<String> whereConditionsColumns = dataPatchInsertRequest.getWhereConditions();

    // 先撈該筆資料
    StringBuffer columnsSb = new StringBuffer();

    IntStream.range(0, insertedColumns.size() - 1).forEach(i->{
      columnsSb.append(insertedColumns.get(i)).append(" ,");
    });

    columnsSb.append(insertedColumns.get(insertedColumns.size() -1));

    logger.info("inserted columns:{}", columnsSb.toString());

    StringBuffer whereConditionsSb = new StringBuffer();

    whereConditionsSb.append("where ");

    IntStream.range(0, whereConditionsColumns.size() -1).forEach(i->{
      whereConditionsSb.append(whereConditionsColumns.get(i)).append(" and");
    });

    whereConditionsSb.append(whereConditionsColumns.get(whereConditionsColumns.size()-1));

    // insert sql
    StringBuffer insertSb = new StringBuffer();
    insertSb.append("Insert into ").append(tableName);
    insertSb.append("(").append(columnsSb.toString()).append(")");
    insertSb.append("\n");
    insertSb.append("values(");

    IntStream.range(0, insertedColumns.size() - 1).forEach(i->{
      if(insertedColumns.get(i).equals(sequenceColumnName)){
        insertSb.append("Seq.nextValue");
      }
      else{
        insertSb.append("?");
      }
      insertSb.append(",");
    });

    if(insertedColumns.get(insertedColumns.size() -1).equals(sequenceColumnName) ){
      insertSb.append("Seq.nextValue");
    }
    else {
      insertSb.append("?");
    }

    insertSb.append(")");

    logger.info("insertSQL:{}", insertSb.toString());

    String executingSql = "select * from " +  tableName +" "+ whereConditionsSb.toString();

    logger.info("executingSql:{}", executingSql);

    Map<String, Object> data = jdbcTemplate.queryForMap(executingSql);

    //params
    StringBuffer paramsSb = new StringBuffer();
    paramsSb.append("/").append("\n");
    paramsSb.append("params:");

    IntStream.range(0, insertedColumns.size()-1).forEach(i->{
      if(!insertedColumns.get(i).equals(sequenceColumnName)){
    	  String columnName = insertedColumns.get(i);
        paramsSb.append(data.get(columnName)).append("@@").append("\n");
      }

    });

    paramsSb.append(data.get(insertedColumns.get(insertedColumns.size() - 1)));

    paramsSb.append("/");

    logger.info("params:{}", paramsSb.toString());

    DatabaseMetaData databaseMetaData = jdbcTemplate.getDataSource().getConnection().getMetaData();

    ResultSet columns = databaseMetaData.getColumns(null,null, tableName, null);


    while(columns.next())
    {
      String columnName = columns.getString("COLUMN_NAME");
      if(columnName.equals(sequenceColumnName)){
        continue;
      }

      int datatype = Integer.valueOf(columns.getString("DATA_TYPE"));

      if(datatype == Types.VARCHAR){
        logger.info("columnName:{}, dataType is Varchar", columnName);
      }
      else if(datatype == Types.CHAR){
        logger.info("columnName:{}, dataType is Char", columnName);
      }
      else if(datatype == Types.DATE){
        logger.info("columnName:{}, dataType is Date", columnName);
      }
      else if(datatype == Types.TIMESTAMP){
        logger.info("columnName:{}, dataType is TIMESTAMP", columnName);
      }
      else if(datatype == Types.INTEGER){
        logger.info("columnName:{}, dataType is {}}", columnName, datatype);
      }
      else{
    	  logger.info("columnName:{}, unknown dataType: {}}", columnName, datatype);
      }
      

    }

    return response;
  }
}
