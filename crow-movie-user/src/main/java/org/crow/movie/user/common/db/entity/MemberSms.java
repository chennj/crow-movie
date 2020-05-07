package org.crow.movie.user.common.db.entity;

import javax.persistence.Column;

import java.util.Date;
/**
 * table name:  hg_member_sms
 * author name: chennj
 * create time: 2020-05-06 09:52:16
 */ 
public class MemberSms{

	@Column(name="id")
	private Integer id;
	@Column(name="member_id")
	private Integer memberId;
	@Column(name="mobile")
	private String mobile;
	@Column(name="send_time")
	private Integer sendTime;

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
	public void setMobile(String mobile){
		this.mobile=mobile;
	}
	public String getMobile(){
		return mobile;
	}
	public void setSendTime(Integer sendTime){
		this.sendTime=sendTime;
	}
	public Integer getSendTime(){
		return sendTime;
	}
}

