package com.ppdai.infrastructure.mq.biz.ui.dto.request;



/**
 * @author zhangxiao04
 * @date  2018/05/11
 */
public class DbNodeCreateRequest {
    private Long id;
    private String ip;
    private Integer port;
    private String dbName;
    private String dbUserName;
    private String dbPass;
    private String ipBak;
    private Integer portBak;
    private String dbUserNameBak;
    private String dbPassBak;
    /**
     * 节点的连接串
     */
    private String conStr;
    private Integer readOnly;
    private Integer nodeTypes;
    private Byte normalFlag;
    private String remark;
    private String insertBy;
    private String updateBy;
    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public String getDbUserName() {
		return dbUserName;
	}
	public void setDbUserName(String dbUserName) {
		this.dbUserName = dbUserName;
	}
	public String getDbPass() {
		return dbPass;
	}
	public void setDbPass(String dbPass) {
		this.dbPass = dbPass;
	}
	public String getIpBak() {
		return ipBak;
	}
	public void setIpBak(String ipBak) {
		this.ipBak = ipBak;
	}
	public Integer getPortBak() {
		return portBak;
	}
	public void setPortBak(Integer portBak) {
		this.portBak = portBak;
	}
	public String getDbUserNameBak() {
		return dbUserNameBak;
	}
	public void setDbUserNameBak(String dbUserNameBak) {
		this.dbUserNameBak = dbUserNameBak;
	}
	public String getDbPassBak() {
		return dbPassBak;
	}
	public void setDbPassBak(String dbPassBak) {
		this.dbPassBak = dbPassBak;
	}
	public String getConStr() {
		return conStr;
	}
	public void setConStr(String conStr) {
		this.conStr = conStr;
	}
	public Integer getReadOnly() {
		return readOnly;
	}
	public void setReadOnly(Integer readOnly) {
		this.readOnly = readOnly;
	}
	public Integer getNodeTypes() {
		return nodeTypes;
	}
	public void setNodeTypes(Integer nodeTypes) {
		this.nodeTypes = nodeTypes;
	}
	public Byte getNormalFlag() {
		return normalFlag;
	}
	public void setNormalFlag(Byte normalFlag) {
		this.normalFlag = normalFlag;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getInsertBy() {
		return insertBy;
	}
	public void setInsertBy(String insertBy) {
		this.insertBy = insertBy;
	}
	public String getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}
	
}
