package org.crow.movie.user.common.db.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
//import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

//import org.springframework.data.annotation.CreatedDate;
//import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name="hg_member_info",catalog="movie")
//@EntityListeners(AuditingEntityListener.class)
public class MemberInfo {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id",unique=true,nullable=false)
	private Integer id;
	@Column(name="up_qr")
	private Integer upQr;
	@Column(name="is_visitor")
	private Integer isVisitor;
	@Column(name="token")
	private String token;
	@Column(name="account",unique=true,nullable=false)
	private String account;
	@Column(name="password")
	private String password;
	@Column(name="is_nick_name")
	private Integer isNickName;
	@Column(name="nick_name")
	private String nickName;
	@Column(name="global_area_code")
	private String globalAreaCode;
	@Column(name="mobile")
	private String mobile;
	@Column(name="is_avatar")
	private Integer isAvatar;
	@Column(name="avatar")
	private String avatar;
	@Column(name="is_sex")
	private Integer isSex;
	@Column(name="sex")
	private Integer sex;
	@Column(name="level_id")
	private Integer levelId;
	@Column(name="device_id")
	private String deviceId;
	@Column(name="reg_promo_code")
	private String regPromoCode;
	@Column(name="promo_code")
	private String promoCode;
	@Column(name="promo_qrcode")
	private String promoQrcode;
	@Column(name="day_view_times")
	private Integer dayViewTimes;
	@Column(name="today_view_times")
	private Integer todayViewTimes;
	@Column(name="re_today_view_times")
	private Integer reTodayViewTimes;
	@Column(name="day_cache_times")
	private Integer dayCacheTimes;
	@Column(name="today_cache_times")
	private Integer todayCacheTimes;
	@Column(name="re_today_cache_times")
	private Integer reTodayCacheTimes;
	@Column(name="is_today_click_adv")
	private Integer isTodayClickAdv;
	@Column(name="is_save_qrcode")
	private Integer isSaveQrcode;
	@Column(name="is_comment")
	private Integer isComment;
	@Column(name="save_qrcode_time")
	private Integer saveQrcodeTime;
	@Column(name="expire_time")
	private Integer expireTime;
	@Column(name="status")
	private Integer status;
	@Column(name="create_ip")
	private String createIp;
	@Column(name="create_addr")
	private String createAddr;
	@Column(name="create_time")
	private Integer createTime;
	@Column(name="create_user")
	private Integer createUser;
	@Column(name="create_date")
	//@CreatedDate
	private Date createDate;
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
	public void setUpQr(Integer upQr){
		this.upQr=upQr;
	}
	public Integer getUpQr(){
		return upQr;
	}
	public void setIsVisitor(Integer isVisitor){
		this.isVisitor=isVisitor;
	}
	public Integer getIsVisitor(){
		return isVisitor;
	}
	public void setToken(String token){
		this.token=token;
	}
	public String getToken(){
		return token;
	}
	public void setAccount(String account){
		this.account=account;
	}
	public String getAccount(){
		return account;
	}
	public void setPassword(String password){
		this.password=password;
	}
	public String getPassword(){
		return password;
	}
	public void setIsNickName(Integer isNickName){
		this.isNickName=isNickName;
	}
	public Integer getIsNickName(){
		return isNickName;
	}
	public void setNickName(String nickName){
		this.nickName=nickName;
	}
	public String getNickName(){
		return nickName;
	}
	public void setGlobalAreaCode(String globalAreaCode){
		this.globalAreaCode=globalAreaCode;
	}
	public String getGlobalAreaCode(){
		return globalAreaCode;
	}
	public void setMobile(String mobile){
		this.mobile=mobile;
	}
	public String getMobile(){
		return mobile;
	}
	public void setIsAvatar(Integer isAvatar){
		this.isAvatar=isAvatar;
	}
	public Integer getIsAvatar(){
		return isAvatar;
	}
	public void setAvatar(String avatar){
		this.avatar=avatar;
	}
	public String getAvatar(){
		return avatar;
	}
	public void setIsSex(Integer isSex){
		this.isSex=isSex;
	}
	public Integer getIsSex(){
		return isSex;
	}
	public void setSex(Integer sex){
		this.sex=sex;
	}
	public Integer getSex(){
		return sex;
	}
	public void setLevelId(Integer levelId){
		this.levelId=levelId;
	}
	public Integer getLevelId(){
		return levelId;
	}
	public void setDeviceId(String deviceId){
		this.deviceId=deviceId;
	}
	public String getDeviceId(){
		return deviceId;
	}
	public void setRegPromoCode(String regPromoCode){
		this.regPromoCode=regPromoCode;
	}
	public String getRegPromoCode(){
		return regPromoCode;
	}
	public void setPromoCode(String promoCode){
		this.promoCode=promoCode;
	}
	public String getPromoCode(){
		return promoCode;
	}
	public void setPromoQrcode(String promoQrcode){
		this.promoQrcode=promoQrcode;
	}
	public String getPromoQrcode(){
		return promoQrcode;
	}
	public void setDayViewTimes(Integer dayViewTimes){
		this.dayViewTimes=dayViewTimes;
	}
	public Integer getDayViewTimes(){
		return dayViewTimes;
	}
	public void setTodayViewTimes(Integer todayViewTimes){
		this.todayViewTimes=todayViewTimes;
	}
	public Integer getTodayViewTimes(){
		return todayViewTimes;
	}
	public void setReTodayViewTimes(Integer reTodayViewTimes){
		this.reTodayViewTimes=reTodayViewTimes;
	}
	public Integer getReTodayViewTimes(){
		return reTodayViewTimes;
	}
	public void setDayCacheTimes(Integer dayCacheTimes){
		this.dayCacheTimes=dayCacheTimes;
	}
	public Integer getDayCacheTimes(){
		return dayCacheTimes;
	}
	public void setTodayCacheTimes(Integer todayCacheTimes){
		this.todayCacheTimes=todayCacheTimes;
	}
	public Integer getTodayCacheTimes(){
		return todayCacheTimes;
	}
	public void setReTodayCacheTimes(Integer reTodayCacheTimes){
		this.reTodayCacheTimes=reTodayCacheTimes;
	}
	public Integer getReTodayCacheTimes(){
		return reTodayCacheTimes;
	}
	public void setIsTodayClickAdv(Integer isTodayClickAdv){
		this.isTodayClickAdv=isTodayClickAdv;
	}
	public Integer getIsTodayClickAdv(){
		return isTodayClickAdv;
	}
	public void setIsSaveQrcode(Integer isSaveQrcode){
		this.isSaveQrcode=isSaveQrcode;
	}
	public Integer getIsSaveQrcode(){
		return isSaveQrcode;
	}
	public void setIsComment(Integer isComment){
		this.isComment=isComment;
	}
	public Integer getIsComment(){
		return isComment;
	}
	public void setSaveQrcodeTime(Integer saveQrcodeTime){
		this.saveQrcodeTime=saveQrcodeTime;
	}
	public Integer getSaveQrcodeTime(){
		return saveQrcodeTime;
	}
	public void setExpireTime(Integer expireTime){
		this.expireTime=expireTime;
	}
	public Integer getExpireTime(){
		return expireTime;
	}
	public void setStatus(Integer status){
		this.status=status;
	}
	public Integer getStatus(){
		return status;
	}
	public void setCreateIp(String createIp){
		this.createIp=createIp;
	}
	public String getCreateIp(){
		return createIp;
	}
	public void setCreateAddr(String createAddr){
		this.createAddr=createAddr;
	}
	public String getCreateAddr(){
		return createAddr;
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
	public void setCreateDate(Date createDate){
		this.createDate=createDate;
	}
	public Date getCreateDate(){
		return createDate;
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
	}}
