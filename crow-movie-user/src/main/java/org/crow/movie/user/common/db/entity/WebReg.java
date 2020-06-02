package org.crow.movie.user.common.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.Date;
/**
 * table name:  hg_web_reg
 * author name: chennj
 * create time: 2020-05-06 09:52:16
 */ 
@Entity
@Table(name="hg_web_reg",catalog="movie")
public class WebReg{

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id",unique=true,nullable=false)
	private Integer id;
	@Column(name="ip")
	private String ip;
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
	public void setIp(String ip){
		this.ip=ip;
	}
	public String getIp(){
		return ip;
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

