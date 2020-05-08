package org.crow.movie.user.common.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * table name:  hg_member_movie_up_down
 * author name: chennj
 * create time: 2020-05-06 09:52:16
 */ 
@Entity
@Table(name="hg_member_movie_up_down",catalog="movie")
public class MemberMovieUpDown{

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id",unique=true,nullable=false)
	private Integer id;
	@Column(name="member_id")
	private Integer memberId;
	@Column(name="movie_id")
	private Integer movieId;
	@Column(name="up_down")
	private Integer upDown;
	@Column(name="create_time")
	private Integer createTime;

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
	public void setMovieId(Integer movieId){
		this.movieId=movieId;
	}
	public Integer getMovieId(){
		return movieId;
	}
	public void setUpDown(Integer upDown){
		this.upDown=upDown;
	}
	public Integer getUpDown(){
		return upDown;
	}
	public void setCreateTime(Integer createTime){
		this.createTime=createTime;
	}
	public Integer getCreateTime(){
		return createTime;
	}
}

