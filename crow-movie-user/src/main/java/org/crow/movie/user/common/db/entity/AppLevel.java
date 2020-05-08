package org.crow.movie.user.common.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * table name:  hg_app_level
 * author name: chennj
 * create time: 2020-05-06 09:52:16
 */ 
@Entity
@Table(name="hg_app_level",catalog="movie")
public class AppLevel{

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id",unique=true,nullable=false)
	private Integer id;
	@Column(name="title")
	private String title;
	@Column(name="code")
	private String code;
	@Column(name="grade")
	private Integer grade;
	@Column(name="icon")
	private String icon;
	@Column(name="promo_limit")
	private Integer promoLimit;
	@Column(name="day_view_times")
	private Integer dayViewTimes;
	@Column(name="day_cache_times")
	private Integer dayCacheTimes;
	@Column(name="status")
	private Integer status;
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
	public void setTitle(String title){
		this.title=title;
	}
	public String getTitle(){
		return title;
	}
	public void setCode(String code){
		this.code=code;
	}
	public String getCode(){
		return code;
	}
	public void setGrade(Integer grade){
		this.grade=grade;
	}
	public Integer getGrade(){
		return grade;
	}
	public void setIcon(String icon){
		this.icon=icon;
	}
	public String getIcon(){
		return icon;
	}
	public void setPromoLimit(Integer promoLimit){
		this.promoLimit=promoLimit;
	}
	public Integer getPromoLimit(){
		return promoLimit;
	}
	public void setDayViewTimes(Integer dayViewTimes){
		this.dayViewTimes=dayViewTimes;
	}
	public Integer getDayViewTimes(){
		return dayViewTimes;
	}
	public void setDayCacheTimes(Integer dayCacheTimes){
		this.dayCacheTimes=dayCacheTimes;
	}
	public Integer getDayCacheTimes(){
		return dayCacheTimes;
	}
	public void setStatus(Integer status){
		this.status=status;
	}
	public Integer getStatus(){
		return status;
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

