package com.ppdai.infrastructure.mq.biz.entity;

import java.util.Date;

public class TableInfoEntity {
	private String dbName;
	private String tbName;
	private Long maxId = -1L;
	private Long tbRows = -1L;
	private float dataSize = -1f;
	private Date min;
	private Date max;
	private long minId;
	//private long maxId;


	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getTbName() {
		return tbName;
	}

	public void setTbName(String tbName) {
		this.tbName = tbName;
	}

	public Long getMaxId() {
		return maxId;
	}

	public void setMaxId(Long maxId) {
		this.maxId = maxId;
	}


	public long getMinId() {
		return minId;
	}

	public void setMinId(long minId) {
		this.minId = minId;
	}

//	public long getMaxId() {
//		return maxId;
//	}
//
//	public void setMaxId(long maxId) {
//		this.maxId = maxId;
//	}


	public Date getMin() {
		return min;
	}

	public void setMin(Date min) {
		this.min = min;
	}

	public Date getMax() {
		return max;
	}

	public void setMax(Date max) {
		this.max = max;
	}

	public Long getTbRows() {
		return tbRows;
	}

	public void setTbRows(Long tbRows) {
		this.tbRows = tbRows;
	}

	public float getDataSize() {
		return dataSize;
	}

	public void setDataSize(float dataSize) {
		this.dataSize = dataSize;
	}

}
