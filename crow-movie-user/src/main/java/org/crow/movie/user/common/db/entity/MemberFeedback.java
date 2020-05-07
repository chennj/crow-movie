package org.crow.movie.user.common.db.entity;

import javax.persistence.Column;

import java.util.Date;
/**
 * table name:  hg_member_feedback
 * author name: chennj
 * create time: 2020-05-06 09:52:16
 */ 
public class MemberFeedback{

	@Column(name="id")
	private Integer id;
	@Column(name="member_id")
	private Integer memberId;
	@Column(name="feedback_type")
	private Integer feedbackType;
	@Column(name="content")
	private String content;
	@Column(name="pic")
	private String pic;
	@Column(name="is_read")
	private Integer isRead;
	@Column(name="admin_is_read")
	private Integer adminIsRead;
	@Column(name="create_time")
	private Integer createTime;
	@Column(name="update_time")
	private Integer updateTime;

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
	public void setFeedbackType(Integer feedbackType){
		this.feedbackType=feedbackType;
	}
	public Integer getFeedbackType(){
		return feedbackType;
	}
	public void setContent(String content){
		this.content=content;
	}
	public String getContent(){
		return content;
	}
	public void setPic(String pic){
		this.pic=pic;
	}
	public String getPic(){
		return pic;
	}
	public void setIsRead(Integer isRead){
		this.isRead=isRead;
	}
	public Integer getIsRead(){
		return isRead;
	}
	public void setAdminIsRead(Integer adminIsRead){
		this.adminIsRead=adminIsRead;
	}
	public Integer getAdminIsRead(){
		return adminIsRead;
	}
	public void setCreateTime(Integer createTime){
		this.createTime=createTime;
	}
	public Integer getCreateTime(){
		return createTime;
	}
	public void setUpdateTime(Integer updateTime){
		this.updateTime=updateTime;
	}
	public Integer getUpdateTime(){
		return updateTime;
	}
}

