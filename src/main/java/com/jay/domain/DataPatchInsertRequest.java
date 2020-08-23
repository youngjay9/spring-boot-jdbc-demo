package com.jay.domain;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;

public class DataPatchInsertRequest {

  @ApiModelProperty(notes = "tableName", value = "", example = "ob_config")
  private String tableName;

  @ApiModelProperty(notes = "whereConditions", value = "", example = "")
  private List<String> whereConditions;

  @ApiModelProperty(notes = "insertColumns", value = "", example = "")
  private List<String> insertColumns;

  @ApiModelProperty(notes = "sequneceColumnName", value = "", example = "")
  private String sequneceColumnName;


  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public List<String> getWhereConditions() {
    return whereConditions;
  }

  public void setWhereConditions(List<String> whereConditions) {
    this.whereConditions = whereConditions;
  }

  public List<String> getInsertColumns() {
    return insertColumns;
  }

  public void setInsertColumns(List<String> insertColumns) {
    this.insertColumns = insertColumns;
  }

  public String getSequneceColumnName() {
    return sequneceColumnName;
  }

  public void setSequneceColumnName(String sequneceColumnName) {
    this.sequneceColumnName = sequneceColumnName;
  }

  @Override
  public String toString() {
    return "DataPatchInsertRequest{" +
        "tableName='" + tableName + '\'' +
        ", whereConditions=" + whereConditions +
        ", insertColumns=" + insertColumns +
        ", sequneceColumnName='" + sequneceColumnName + '\'' +
        '}';
  }
}
