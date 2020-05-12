package org.crow.movie.user.common.db.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.MemberCacheDao;
import org.crow.movie.user.common.db.dao.MemberClickAdvDao;
import org.crow.movie.user.common.db.dao.MemberCommentDao;
import org.crow.movie.user.common.db.dao.MemberCommentUpDao;
import org.crow.movie.user.common.db.dao.MemberExchangeDao;
import org.crow.movie.user.common.db.dao.MemberFeedbackDao;
import org.crow.movie.user.common.db.dao.MemberHistoryDao;
import org.crow.movie.user.common.db.dao.MemberInfoDao;
import org.crow.movie.user.common.db.dao.MemberLikeDao;
import org.crow.movie.user.common.db.dao.MemberMessageDao;
import org.crow.movie.user.common.db.dao.MemberMovieUpDownDao;
import org.crow.movie.user.common.db.dao.MemberPromoDao;
import org.crow.movie.user.common.db.dao.MemberSearchDao;
import org.crow.movie.user.common.db.dao.MemberSmsDao;
import org.crow.movie.user.common.db.entity.MemberInfo;
import org.crow.movie.user.common.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberInfoService extends AbstractBaseService<MemberInfo> {
	
	@Autowired
	MemberInfoDao mmInfoDao;
	
	@Autowired
	MemberCacheDao mmCacheDao;
	@Autowired
	MemberClickAdvDao mmClickAdvDao;
	@Autowired
	MemberCommentDao mmCommentDao;
	@Autowired
	MemberCommentUpDao mmCommentUpDao;
	@Autowired
	MemberExchangeDao mmExchangeDao;
	@Autowired
	MemberFeedbackDao mmFeedbackDao;
	@Autowired
	MemberHistoryDao mmHistoryDao;
	@Autowired
	MemberLikeDao	mmLikeDao;
	@Autowired
	MemberMessageDao mmMessageDao;
	@Autowired
	MemberMovieUpDownDao mmMovieUpDownDao;
	@Autowired
	MemberPromoDao mmPromoDao;
	@Autowired
	MemberSearchDao mmSearchDao;
	@Autowired
	MemberSmsDao mmSmsDao;
	
	@Autowired
	public void setBaseDao(MemberInfoDao dao){
		
		super.setBaseDao(dao);
	}

	public void delete(Integer id) {
		
		super.del(id);
		/**
		 * 还有关联表的删除
		 */
		mmCacheDao.del("memberId", id);
		mmClickAdvDao.del("memberId", id);
		mmCommentDao.del("memberId", id);
		mmCommentUpDao.del("memberId", id);
		mmExchangeDao.del("memberId", id);
		mmFeedbackDao.del("memberId", id);
		mmHistoryDao.del("memberId", id);
		mmLikeDao.del("memberId", id);
		mmMessageDao.del("memberId", id);
		mmMovieUpDownDao.del("memberId", id);
		mmPromoDao.del("memberId", id);
		mmPromoDao.del("toMemberId", id);
		mmSearchDao.del("memberId", id);
		mmSmsDao.del("memberId", id);

	}

	public int updateStatus(Integer id) {
		
		String sql = "update hg_member_info m set m.status = if(m.status=1,2,1) where id=?1";
		return mmInfoDao.executeNative(sql,id);
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, List<Map<String, Object>>> search(Integer page, Integer pageSize, Map<String, Object> allParams) {

		final StringBuilder where 	= new StringBuilder("where 1=1 ");
		final StringBuilder having 	= new StringBuilder();
		
		List<Object> params = new ArrayList<Object>(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				
				Object 
				o = allParams.get("id");
				if (StrUtil.notEmpty(o)){
					where.append("a.id = ?1 ");
					this.add(o);
				}
				
				o = allParams.get("account");
				if (StrUtil.notEmpty(o)){
					where.append("and a.account = ?2 ");
					this.add(o);
				}
				
				o = allParams.get("mobile");
				if (StrUtil.notEmpty(o)){
					where.append("and a.mobile like '%?3%' ");
					this.add(o);
				}
				
				o = allParams.get("level");
				if (StrUtil.notEmpty(o)){
					where.append("and a.level_id like '%?4%' ");
					this.add(o);
				}
				
				o = allParams.get("is_visitor");
				if (StrUtil.notEmpty(o)){
					where.append("and a.is_visitor = ?5 ");
					this.add(o);
				}
				
				o = allParams.get("sex");
				if (StrUtil.notEmpty(o)){
					where.append("and a.sex = ?6 ");
					this.add(o);
				}
				
				o = allParams.get("status");
				if (StrUtil.notEmpty(o)){
					where.append("and a.status = ?7  ");
					this.add(o);
				}
				
				o = allParams.get("begin_time");
				if (StrUtil.notEmpty(o)){
					try {
						this.add(fmt.parse(String.valueOf(o)));
						where.append("and a.create_time >= ?8  ");
					} catch (Exception e){
						logger.error("member info service.search>>>begin_time 转换失败"+e.getMessage());
					}
				}
				
				o = allParams.get("end_time");
				if (StrUtil.notEmpty(o)){
					try {
						this.add(fmt.parse(String.valueOf(o)));
						where.append("and a.create_time < ?9  ");
					} catch (Exception e){
						logger.error("member info service.search>>>end_time 转换失败"+e.getMessage());
					}
				}
				
				o = allParams.get("reg_promo_code");
				if (StrUtil.notEmpty(o)){
					where.append("and a.reg_promo_code like '%?10%' ");
					this.add(o);
				}
				
				o = allParams.get("promo_code");
				if (StrUtil.notEmpty(o)){
					where.append("and a.promo_code = ?11 ");
					this.add(o);
				}
				
				o = allParams.get("is_vip");
				if (StrUtil.notEmpty(o)){
					
					if ("1".equals(String.valueOf(o))){
						where.append("and a.expire_time <= ?12 ");
					} else {
						where.append("and a.expire_time > ?12 ");
					}					
					this.add(new Date());
				}
				
				o = allParams.get("device_id_isnull");
				if (StrUtil.notEmpty(o)){
					
					if ("1".equals(String.valueOf(o))){
						where.append("and a.device_id is null ");
					} else if ("2".equals(String.valueOf(o))){
						where.append("and a.device_id <> ''  ");
					}					
					this.add(new Date());
				}
				
				o = allParams.get("promo_num");
				if (StrUtil.notEmpty(o)){
					having.append("count(b.member_id) >= ?13 ");
					this.add(Integer.valueOf(String.valueOf(o)));
				}
			}
		};
		
		String 
		sql = 
			"select id,icon,code,grade,title from hg_app_level where status = 1 order by id desc ";
	
		List<Map<String,Object>> classList = super.getAllListMap(sql);
		
		sql = 
			"select a.*,count(b.member_id) promo_num from hg_member_info a "
			+ "left join hg_member_promo b on b.member_id=a.id "
			+ where.toString()
			+ "a.id desc "
			+ "group by a.id "
			+ having.toString();
		
		List<Map<String,Object>> list = super.getPageListMap(sql, page, pageSize, params);
		
		sql = 
			"select count(c.member_id) count,c.device_type from hg_member_info a "
			+ "left join hg_member_promo b on b.member_id=a.id "
			+ "(SELECT member_id, device_type FROM hg_member_open GROUP BY member_id, device_type) c on c.member_id=a.id "
			+ where.toString()
			+ "group by c.device_type "
			+ having.toString();
		
		List<Map<String,Object>> loginTypeList = super.getAllListMap(sql, params);
		
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		String today = fmt.format(new Date());
		sql = 
			"select count(DISTINCT c.member_id) count,c.device_type from hg_member_info a "
			+ "left join hg_member_promo b on b.member_id=a.id "
			+ "left join hg_member_open c onc.member_id=a.id "
			+ where
				.append(" and a.create_time >= '").append(today).append(" 00:00:00'")
				.append(" and a.create_time <= '").append(today).append(" 23:59:59'")
			+ "group by c.device_type "
			+ having.toString();
		
		List<Map<String,Object>> todayLoginTyleList = super.getAllListMap(sql, params);

		sql = 
			"select count(1) visitor_count from hg_member_info"
			+ new StringBuilder()
				.append("where is_visitor = 1 ")
				.append("and a.create_time >= '").append(today).append(" 00:00:00' ")
				.append("and a.create_time <= '").append(today).append(" 23:59:59' ")
				.toString();
		List<Map<String,Object>> visitorList = super.getAllListMap(sql);
		
		Map<String, List<Map<String, Object>>> result = new HashMap<String, List<Map<String, Object>>>();
		result.put("list", list);
		result.put("class_list", classList);
		result.put("login_type_list", loginTypeList);
		result.put("today_login_type_list", todayLoginTyleList);
		result.put("visitor_list", visitorList);
		
		return result;
	}
	
	

}
