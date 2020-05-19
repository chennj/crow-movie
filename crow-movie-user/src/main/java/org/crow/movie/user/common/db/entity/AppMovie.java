package org.crow.movie.user.common.db.entity;

import javax.persistence.Column;

import java.util.Date;
/**
 * table name:  hg_app_movie
 * author name: chennj
 * create time: 2020-05-06 09:52:16
 */ 
public class AppMovie{

	@Column(name="id")
	private Integer id;
	@Column(name="index")
	private Integer index;
	@Column(name="category_ids")
	private String categoryIds;
	@Column(name="tag_ids")
	private String tagIds;
	@Column(name="star_ids")
	private String starIds;
	@Column(name="theme_ids")
	private String themeIds;
	@Column(name="title")
	private String title;
	@Column(name="en_title")
	private String enTitle;
	@Column(name="cover")
	private String cover;
	@Column(name="cover_vertical")
	private String coverVertical;
	@Column(name="url")
	private String url;
	@Column(name="url2")
	private String url2;
	@Column(name="score")
	private String score;
	@Column(name="duration")
	private String duration;
	@Column(name="click_num")
	private Integer clickNum;
	@Column(name="real_click_num")
	private Integer realClickNum;
	@Column(name="like_num")
	private Integer likeNum;
	@Column(name="real_like_num")
	private Integer realLikeNum;
	@Column(name="up_num")
	private Integer upNum;
	@Column(name="down_num")
	private Integer downNum;
	@Column(name="comment_num")
	private Integer commentNum;
	@Column(name="detail")
	private String detail;
	@Column(name="en_detail")
	private String enDetail;
	@Column(name="sort")
	private Integer sort;
	@Column(name="status")
	private Integer status;
	@Column(name="cj")
	private String cj;
	@Column(name="create_time")
	private Integer createTime;
	@Column(name="create_date")
	private Date createDate;
	@Column(name="create_user")
	private Integer createUser;
	@Column(name="update_time")
	private Integer updateTime;
	@Column(name="update_date")
	private Date updateDate;
	@Column(name="update_user")
	private Integer updateUser;
	@Column(name="is_url2_cdn")
	private byte isUrl2Cdn;
	@Column(name="is_cdn")
	private byte isCdn;

	public void setId(Integer id){
		this.id=id;
	}
	public Integer getId(){
		return id;
	}
	public void setIndex(Integer index){
		this.index=index;
	}
	public Integer getIndex(){
		return index;
	}
	public void setCategoryIds(String categoryIds){
		this.categoryIds=categoryIds;
	}
	public String getCategoryIds(){
		return categoryIds;
	}
	public void setTagIds(String tagIds){
		this.tagIds=tagIds;
	}
	public String getTagIds(){
		return tagIds;
	}
	public void setStarIds(String starIds){
		this.starIds=starIds;
	}
	public String getStarIds(){
		return starIds;
	}
	public void setThemeIds(String themeIds){
		this.themeIds=themeIds;
	}
	public String getThemeIds(){
		return themeIds;
	}
	public void setTitle(String title){
		this.title=title;
	}
	public String getTitle(){
		return title;
	}
	public void setEnTitle(String enTitle){
		this.enTitle=enTitle;
	}
	public String getEnTitle(){
		return enTitle;
	}
	public void setCover(String cover){
		this.cover=cover;
	}
	public String getCover(){
		return cover;
	}
	public void setCoverVertical(String coverVertical){
		this.coverVertical=coverVertical;
	}
	public String getCoverVertical(){
		return coverVertical;
	}
	public void setUrl(String url){
		this.url=url;
	}
	public String getUrl(){
		return url;
	}
	public void setUrl2(String url2){
		this.url2=url2;
	}
	public String getUrl2(){
		return url2;
	}
	public void setScore(String score){
		this.score=score;
	}
	public String getScore(){
		return score;
	}
	public void setDuration(String duration){
		this.duration=duration;
	}
	public String getDuration(){
		return duration;
	}
	public void setClickNum(Integer clickNum){
		this.clickNum=clickNum;
	}
	public Integer getClickNum(){
		return clickNum;
	}
	public void setRealClickNum(Integer realClickNum){
		this.realClickNum=realClickNum;
	}
	public Integer getRealClickNum(){
		return realClickNum;
	}
	public void setLikeNum(Integer likeNum){
		this.likeNum=likeNum;
	}
	public Integer getLikeNum(){
		return likeNum;
	}
	public void setRealLikeNum(Integer realLikeNum){
		this.realLikeNum=realLikeNum;
	}
	public Integer getRealLikeNum(){
		return realLikeNum;
	}
	public void setUpNum(Integer upNum){
		this.upNum=upNum;
	}
	public Integer getUpNum(){
		return upNum;
	}
	public void setDownNum(Integer downNum){
		this.downNum=downNum;
	}
	public Integer getDownNum(){
		return downNum;
	}
	public void setCommentNum(Integer commentNum){
		this.commentNum=commentNum;
	}
	public Integer getCommentNum(){
		return commentNum;
	}
	public void setDetail(String detail){
		this.detail=detail;
	}
	public String getDetail(){
		return detail;
	}
	public void setEnDetail(String enDetail){
		this.enDetail=enDetail;
	}
	public String getEnDetail(){
		return enDetail;
	}
	public void setSort(Integer sort){
		this.sort=sort;
	}
	public Integer getSort(){
		return sort;
	}
	public void setStatus(Integer status){
		this.status=status;
	}
	public Integer getStatus(){
		return status;
	}
	public void setCj(String cj){
		this.cj=cj;
	}
	public String getCj(){
		return cj;
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
	public void setUpdateDate(Date updateDate){
		this.updateDate=updateDate;
	}
	public Date getUpdateDate(){
		return updateDate;
	}
	public void setUpdateUser(Integer updateUser){
		this.updateUser=updateUser;
	}
	public Integer getUpdateUser(){
		return updateUser;
	}
	public void setIsUrl2Cdn(byte isUrl2Cdn){
		this.isUrl2Cdn=isUrl2Cdn;
	}
	public byte getIsUrl2Cdn(){
		return isUrl2Cdn;
	}
	public void setIsCdn(byte isCdn){
		this.isCdn=isCdn;
	}
	public byte getIsCdn(){
		return isCdn;
	}
}

