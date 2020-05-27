package org.crow.movie.user.common.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * table name:  hg_app_exchange
 * author name: chennj
 * create time: 2020-05-06 09:52:16
 */ 
@Entity
@Table(name="hg_app_exchange",catalog="movie")
public class AppExchange{

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id",unique=true,nullable=false)
	private Integer id;
	@Column(name="level_id")
	private Integer levelId;
	@Column(name="code")
	private String code;
	@Column(name="title")
	private String title;
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
	public void setLevelId(Integer levelId){
		this.levelId=levelId;
	}
	public Integer getLevelId(){
		return levelId;
	}
	public void setCode(String code){
		this.code=code;
	}
	public String getCode(){
		return code;
	}
	public void setTitle(String title){
		this.title=title;
	}
	public String getTitle(){
		return title;
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

