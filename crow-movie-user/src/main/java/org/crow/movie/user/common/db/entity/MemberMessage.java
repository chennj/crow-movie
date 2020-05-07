package org.crow.movie.user.common.db.entity;

import javax.persistence.Column;

import java.util.Date;
/**
 * table name:  hg_member_message
 * author name: chennj
 * create time: 2020-05-06 09:52:16
 */ 
public class MemberMessage{

	@Column(name="id")
	private Integer id;
	@Column(name="message_type")
	private Integer messageType;
	@Column(name="member_id")
	private Integer memberId;
	@Column(name="title")
	private String title;
	@Column(name="content")
	private String content;
	@Column(name="is_read")
	private Integer isRead;
	@Column(name="notice_id")
	private Integer noticeId;
	@Column(name="create_time")
	private Integer createTime;
	@Column(name="create_user")
	private Integer createUser;
	@Column(name="update_time")
	private Integer updateTime;
	@Column(name="update_user")
	private Integer updateUser;

	public void setId(Integer id){
		this.id=id;
	}
	public Integer getId(){
		return id;
	}
	public void setMessageType(Integer messageType){
		this.messageType=messageType;
	}
	public Integer getMessageType(){
		return messageType;
	}
	public void setMemberId(Integer memberId){
		this.memberId=memberId;
	}
	public Integer getMemberId(){
		return memberId;
	}
	public void setTitle(String title){
		this.title=title;
	}
	public String getTitle(){
		return title;
	}
	public void setContent(String content){
		this.content=content;
	}
	public String getContent(){
		return content;
	}
	public void setIsRead(Integer isRead){
		this.isRead=isRead;
	}
	public Integer getIsRead(){
		return isRead;
	}
	public void setNoticeId(Integer noticeId){
		this.noticeId=noticeId;
	}
	public Integer getNoticeId(){
		return noticeId;
	}
	public void setCreateTime(Integer createTime){
		this.createTime=createTime;
	}
	public Integer getCreateTime(){
		return createTime;
	}
	public void setCreateUser(Integer createUser){
		this.createUser=createUser;
	}
	public Integer getCreateUser(){
		return createUser;
	}
	public void setUpdateTime(Integer updateTime){
		this.updateTime=updateTime;
	}
	public Integer getUpdateTime(){
		return updateTime;
	}
	public void setUpdateUser(Integer updateUser){
		this.updateUser=updateUser;
	}
	public Integer getUpdateUser(){
		return updateUser;
	}
}

