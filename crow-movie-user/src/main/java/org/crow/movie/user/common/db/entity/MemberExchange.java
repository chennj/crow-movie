package org.crow.movie.user.common.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * table name:  hg_member_exchange
 * author name: chennj
 * create time: 2020-05-06 09:52:16
 */ 
@Entity
@Table(name="hg_member_exchange",catalog="movie")
public class MemberExchange{

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id",unique=true,nullable=false)
	private Integer id;
	@Column(name="member_id")
	private Integer memberId;
	@Column(name="exchange_id")
	private Integer exchangeId;
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
	public void setExchangeId(Integer exchangeId){
		this.exchangeId=exchangeId;
	}
	public Integer getExchangeId(){
		return exchangeId;
	}
	public void setCreateTime(Integer createTime){
		this.createTime=createTime;
	}
	public Integer getCreateTime(){
		return createTime;
	}
}

