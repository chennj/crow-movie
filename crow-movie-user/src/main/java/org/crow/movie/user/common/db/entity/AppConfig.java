package org.crow.movie.user.common.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * table name:  hg_app_config
 * author name: chennj
 * create time: 2020-05-06 09:52:16
 */ 
@Entity
@Table(name="hg_app_config",catalog="movie")
public class AppConfig{

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id",unique=true,nullable=false)
	private Integer id;
	@Column(name="title")
	private String title;
	@Column(name="start_page_auto")
	private Integer startPageAuto;
	@Column(name="start_page_time")
	private Integer startPageTime;
	@Column(name="chart_number")
	private String chartNumber;
	@Column(name="chart_url")
	private String chartUrl;
	@Column(name="adv_auto")
	private Integer advAuto;
	@Column(name="click_adv_view_times")
	private Integer clickAdvViewTimes;
	@Column(name="visitor_reg_view_times")
	private Integer visitorRegViewTimes;
	@Column(name="username_reg_view_times")
	private Integer usernameRegViewTimes;
	@Column(name="bind_phone_view_times")
	private Integer bindPhoneViewTimes;
	@Column(name="reg_promo_view_times")
	private Integer regPromoViewTimes;
	@Column(name="save_promo_qrcode_view_times")
	private Integer savePromoQrcodeViewTimes;
	@Column(name="hot_star_num")
	private Integer hotStarNum;
	@Column(name="hot_theme_num")
	private Integer hotThemeNum;
	@Column(name="home_like_num")
	private Integer homeLikeNum;
	@Column(name="home_hot_num")
	private Integer homeHotNum;
	@Column(name="home_new_num")
	private Integer homeNewNum;
	@Column(name="home_theme_num")
	private Integer homeThemeNum;
	@Column(name="movie_like_num")
	private Integer movieLikeNum;
	@Column(name="promo_top")
	private String promoTop;
	@Column(name="promo_tips")
	private String promoTips;
	@Column(name="promo_links")
	private String promoLinks;
	@Column(name="user_agreement")
	private String userAgreement;
	@Column(name="cdn_type")
	private Integer cdnType;
	@Column(name="status")
	private Integer status;
	@Column(name="app_status")
	private Integer appStatus;
	@Column(name="lottery_url")
	private String lotteryUrl;
	@Column(name="promo_url")
	private String promoUrl;
	@Column(name="pc_download_url")
	private String pcDownloadUrl;
	@Column(name="pc_title")
	private String pcTitle;
	@Column(name="pc_keywords")
	private String pcKeywords;
	@Column(name="pc_desc")
	private String pcDesc;
	@Column(name="pc_mail")
	private String pcMail;
	@Column(name="pc_qq")
	private String pcQq;
	@Column(name="comment_rep")
	private String commentRep;
	@Column(name="cache_time")
	private Integer cacheTime;
	@Column(name="is_auto_comment")
	private Integer isAutoComment;
	@Column(name="auto_comment_time")
	private Integer autoCommentTime;
	@Column(name="auto_comment")
	private String autoComment;
	@Column(name="create_time")
	private Integer createTime;
	@Column(name="create_user")
	private Integer createUser;
	@Column(name="update_time")
	private Integer updateTime;
	@Column(name="update_user")
	private Integer updateUser;
	@Column(name="pc_visitor_reg_view_times")
	private byte pcVisitorRegViewTimes;
	@Column(name="static_url")
	private String staticUrl;

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
	public void setStartPageAuto(Integer startPageAuto){
		this.startPageAuto=startPageAuto;
	}
	public Integer getStartPageAuto(){
		return startPageAuto;
	}
	public void setStartPageTime(Integer startPageTime){
		this.startPageTime=startPageTime;
	}
	public Integer getStartPageTime(){
		return startPageTime;
	}
	public void setChartNumber(String chartNumber){
		this.chartNumber=chartNumber;
	}
	public String getChartNumber(){
		return chartNumber;
	}
	public void setChartUrl(String chartUrl){
		this.chartUrl=chartUrl;
	}
	public String getChartUrl(){
		return chartUrl;
	}
	public void setAdvAuto(Integer advAuto){
		this.advAuto=advAuto;
	}
	public Integer getAdvAuto(){
		return advAuto;
	}
	public void setClickAdvViewTimes(Integer clickAdvViewTimes){
		this.clickAdvViewTimes=clickAdvViewTimes;
	}
	public Integer getClickAdvViewTimes(){
		return clickAdvViewTimes;
	}
	public void setVisitorRegViewTimes(Integer visitorRegViewTimes){
		this.visitorRegViewTimes=visitorRegViewTimes;
	}
	public Integer getVisitorRegViewTimes(){
		return visitorRegViewTimes;
	}
	public void setUsernameRegViewTimes(Integer usernameRegViewTimes){
		this.usernameRegViewTimes=usernameRegViewTimes;
	}
	public Integer getUsernameRegViewTimes(){
		return usernameRegViewTimes;
	}
	public void setBindPhoneViewTimes(Integer bindPhoneViewTimes){
		this.bindPhoneViewTimes=bindPhoneViewTimes;
	}
	public Integer getBindPhoneViewTimes(){
		return bindPhoneViewTimes;
	}
	public void setRegPromoViewTimes(Integer regPromoViewTimes){
		this.regPromoViewTimes=regPromoViewTimes;
	}
	public Integer getRegPromoViewTimes(){
		return regPromoViewTimes;
	}
	public void setSavePromoQrcodeViewTimes(Integer savePromoQrcodeViewTimes){
		this.savePromoQrcodeViewTimes=savePromoQrcodeViewTimes;
	}
	public Integer getSavePromoQrcodeViewTimes(){
		return savePromoQrcodeViewTimes;
	}
	public void setHotStarNum(Integer hotStarNum){
		this.hotStarNum=hotStarNum;
	}
	public Integer getHotStarNum(){
		return hotStarNum;
	}
	public void setHotThemeNum(Integer hotThemeNum){
		this.hotThemeNum=hotThemeNum;
	}
	public Integer getHotThemeNum(){
		return hotThemeNum;
	}
	public void setHomeLikeNum(Integer homeLikeNum){
		this.homeLikeNum=homeLikeNum;
	}
	public Integer getHomeLikeNum(){
		return homeLikeNum;
	}
	public void setHomeHotNum(Integer homeHotNum){
		this.homeHotNum=homeHotNum;
	}
	public Integer getHomeHotNum(){
		return homeHotNum;
	}
	public void setHomeNewNum(Integer homeNewNum){
		this.homeNewNum=homeNewNum;
	}
	public Integer getHomeNewNum(){
		return homeNewNum;
	}
	public void setHomeThemeNum(Integer homeThemeNum){
		this.homeThemeNum=homeThemeNum;
	}
	public Integer getHomeThemeNum(){
		return homeThemeNum;
	}
	public void setMovieLikeNum(Integer movieLikeNum){
		this.movieLikeNum=movieLikeNum;
	}
	public Integer getMovieLikeNum(){
		return movieLikeNum;
	}
	public void setPromoTop(String promoTop){
		this.promoTop=promoTop;
	}
	public String getPromoTop(){
		return promoTop;
	}
	public void setPromoTips(String promoTips){
		this.promoTips=promoTips;
	}
	public String getPromoTips(){
		return promoTips;
	}
	public void setPromoLinks(String promoLinks){
		this.promoLinks=promoLinks;
	}
	public String getPromoLinks(){
		return promoLinks;
	}
	public void setUserAgreement(String userAgreement){
		this.userAgreement=userAgreement;
	}
	public String getUserAgreement(){
		return userAgreement;
	}
	public void setCdnType(Integer cdnType){
		this.cdnType=cdnType;
	}
	public Integer getCdnType(){
		return cdnType;
	}
	public void setStatus(Integer status){
		this.status=status;
	}
	public Integer getStatus(){
		return status;
	}
	public void setAppStatus(Integer appStatus){
		this.appStatus=appStatus;
	}
	public Integer getAppStatus(){
		return appStatus;
	}
	public void setLotteryUrl(String lotteryUrl){
		this.lotteryUrl=lotteryUrl;
	}
	public String getLotteryUrl(){
		return lotteryUrl;
	}
	public void setPromoUrl(String promoUrl){
		this.promoUrl=promoUrl;
	}
	public String getPromoUrl(){
		return promoUrl;
	}
	public void setPcDownloadUrl(String pcDownloadUrl){
		this.pcDownloadUrl=pcDownloadUrl;
	}
	public String getPcDownloadUrl(){
		return pcDownloadUrl;
	}
	public void setPcTitle(String pcTitle){
		this.pcTitle=pcTitle;
	}
	public String getPcTitle(){
		return pcTitle;
	}
	public void setPcKeywords(String pcKeywords){
		this.pcKeywords=pcKeywords;
	}
	public String getPcKeywords(){
		return pcKeywords;
	}
	public void setPcDesc(String pcDesc){
		this.pcDesc=pcDesc;
	}
	public String getPcDesc(){
		return pcDesc;
	}
	public void setPcMail(String pcMail){
		this.pcMail=pcMail;
	}
	public String getPcMail(){
		return pcMail;
	}
	public void setPcQq(String pcQq){
		this.pcQq=pcQq;
	}
	public String getPcQq(){
		return pcQq;
	}
	public void setCommentRep(String commentRep){
		this.commentRep=commentRep;
	}
	public String getCommentRep(){
		return commentRep;
	}
	public void setCacheTime(Integer cacheTime){
		this.cacheTime=cacheTime;
	}
	public Integer getCacheTime(){
		return cacheTime;
	}
	public void setIsAutoComment(Integer isAutoComment){
		this.isAutoComment=isAutoComment;
	}
	public Integer getIsAutoComment(){
		return isAutoComment;
	}
	public void setAutoCommentTime(Integer autoCommentTime){
		this.autoCommentTime=autoCommentTime;
	}
	public Integer getAutoCommentTime(){
		return autoCommentTime;
	}
	public void setAutoComment(String autoComment){
		this.autoComment=autoComment;
	}
	public String getAutoComment(){
		return autoComment;
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
	public void setPcVisitorRegViewTimes(byte pcVisitorRegViewTimes){
		this.pcVisitorRegViewTimes=pcVisitorRegViewTimes;
	}
	public byte getPcVisitorRegViewTimes(){
		return pcVisitorRegViewTimes;
	}
	public void setStaticUrl(String staticUrl){
		this.staticUrl=staticUrl;
	}
	public String getStaticUrl(){
		return staticUrl;
	}
}

