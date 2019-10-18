package com.ppdai.infrastructure.mq.biz.entity;

public class TableInfoEntity {
	private String tableSchema;
	private String tbName;
	private Long autoIncrement;

	public String getTableSchema() {
		return tableSchema;
	}

	public void setTableSchema(String tableSchema) {
		this.tableSchema = tableSchema;
	}

	public String getTbName() {
		return tbName;
	}

	public void setTbName(String tbName) {
		this.tbName = tbName;
	}

	public Long getAutoIncrement() {
		return autoIncrement;
	}

	public void setAutoIncrement(Long autoIncrement) {
		this.autoIncrement = autoIncrement;
	}
}
