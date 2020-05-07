package org.crow.movie.user.common.db.entity;

import javax.persistence.Column;

import java.util.Date;
/**
 * table name:  hg_member_comment_up
 * author name: chennj
 * create time: 2020-05-06 09:52:16
 */ 
public class MemberCommentUp{

	@Column(name="id")
	private Integer id;
	@Column(name="member_id")
	private Integer memberId;
	@Column(name="comment_id")
	private Integer commentId;
	@Column(name="movie_id")
	private Integer movieId;
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
	public void setCommentId(Integer commentId){
		this.commentId=commentId;
	}
	public Integer getCommentId(){
		return commentId;
	}
	public void setMovieId(Integer movieId){
		this.movieId=movieId;
	}
	public Integer getMovieId(){
		return movieId;
	}
	public void setCreateTime(Integer createTime){
		this.createTime=createTime;
	}
	public Integer getCreateTime(){
		return createTime;
	}
}

