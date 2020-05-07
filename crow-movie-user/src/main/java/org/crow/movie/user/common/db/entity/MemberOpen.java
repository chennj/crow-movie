package org.crow.movie.user.common.db.entity;

import javax.persistence.Column;

import java.util.Date;
/**
 * table name:  hg_member_open
 * author name: chennj
 * create time: 2020-05-06 09:52:16
 */ 
public class MemberOpen{

	@Column(name="id")
	private Integer id;
	@Column(name="member_id")
	private Integer memberId;
	@Column(name="device_id")
	private String deviceId;
	@Column(name="device_type")
	private String deviceType;
	@Column(name="create_ip")
	private String createIp;
	@Column(name="create_addr")
	private String createAddr;
	@Column(name="create_time")
	private Integer createTime;
	@Column(name="create_date")
	private Date createDate;

	public void setId(Integer id){
		this.id=id;
	}
	public Integer getId(){
		return id;
	}
	public void setMemberId(Integer memberId){
		this.memberId=memberId;
	}
	public Integer getMemberId(){
		return memberId;
	}
	public void setDeviceId(String deviceId){
		this.deviceId=deviceId;
	}
	public String getDeviceId(){
		return deviceId;
	}
	public void setDeviceType(String deviceType){
		this.deviceType=deviceType;
	}
	public String getDeviceType(){
		return deviceType;
	}
	public void setCreateIp(String createIp){
		this.createIp=createIp;
	}
	public String getCreateIp(){
		return createIp;
	}
	public void setCreateAddr(String createAddr){
		this.createAddr=createAddr;
	}
	public String getCreateAddr(){
		return createAddr;
	}
	public void setCreateTime(Integer createTime){
		this.createTime=createTime;
	}
	public Integer getCreateTime(){
		return createTime;
	}
	public void setCreateDate(Date createDate){
		this.createDate=createDate;
	}
	public Date getCreateDate(){
		return createDate;
	}
}

