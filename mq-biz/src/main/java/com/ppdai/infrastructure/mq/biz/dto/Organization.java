package com.ppdai.infrastructure.mq.biz.dto;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class Organization {
  private String orgId;
  private String orgName;

  public String getOrgId() {
    return orgId;
  }

  public void setOrgId(String orgId) {
    this.orgId = orgId;
  }

  public String getOrgName() {
    return orgName;
  }

  public void setOrgName(String orgName) {
    this.orgName = orgName;
  }
}
