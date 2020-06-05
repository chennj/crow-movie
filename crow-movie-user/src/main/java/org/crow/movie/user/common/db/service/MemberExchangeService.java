package org.crow.movie.user.common.db.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.crow.movie.user.common.cache.FixedCache;
import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.AppExchangeDao;
import org.crow.movie.user.common.db.dao.AppVipDao;
import org.crow.movie.user.common.db.dao.MemberExchangeDao;
import org.crow.movie.user.common.db.dao.MemberInfoDao;
import org.crow.movie.user.common.db.dao.MemberVipDao;
import org.crow.movie.user.common.db.entity.AppExchange;
import org.crow.movie.user.common.db.entity.AppLevel;
import org.crow.movie.user.common.db.entity.AppVip;
import org.crow.movie.user.common.db.entity.MemberExchange;
import org.crow.movie.user.common.db.entity.MemberInfo;
import org.crow.movie.user.common.db.entity.MemberVip;
import org.crow.movie.user.common.util.CommUtil;
import org.crow.movie.user.common.util.Php2JavaUtil;
import org.crow.movie.user.common.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;

@Service
public class MemberExchangeService extends AbstractBaseService<MemberExchange> {
	
	@Autowired
	AppVipDao appVipDao;
	
	@Autowired
	MemberVipDao memberVipDao;
	
	@Autowired
	MemberInfoDao memberInfoDao;
	
	@Autowired
	AppExchangeDao appExchangeDao;
	
	@Autowired
	MemberExchangeDao memberExchangeDao;
	
	@Autowired
	public void setBaseDao(MemberExchangeDao dao){
		
		super.setBaseDao(dao);
	}

	@Transactional(propagation=Propagation.NOT_SUPPORTED)
	public Map<String, List<Map<String, Object>>> search(Integer page, Integer pageSize,
			Map<String, Object> allParams, Object...returnObj) throws ParseException {

		final StringBuilder where 	= new StringBuilder("where 1=1 ");
		
		List<Object> params = new ArrayList<Object>(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				int paramidx = 1;
				
				Object 
				o = allParams.get("account");
				if (StrUtil.notEmpty(o)){
					where.append("and b.account like '%?"+paramidx+"%' ");
					this.add(o);
					paramidx++;
				}
				
				o = allParams.get("title");
				if (StrUtil.notEmpty(o)){
					where.append("and c.title like '%?"+paramidx+"%' ");
					this.add(o);
					paramidx++;
				}
				
				o = allParams.get("is_visitor");
				if (StrUtil.notEmpty(o)){
					where.append("and b.is_visitor = ?"+paramidx+" ");
					this.add(o);
					paramidx++;
				}
				
				o = allParams.get("begin_time");
				if (StrUtil.notEmpty(o)){
					int oi = Php2JavaUtil.transTimeJ2P(String.valueOf(o));
					where.append("and a.create_time >= ?"+paramidx+" ");
					this.add(oi);
					paramidx++;
				}
				
				o = allParams.get("end_time");
				if (StrUtil.notEmpty(o)){
					int oi = Php2JavaUtil.transTimeJ2P(String.valueOf(o));
					where.append("and a.create_time < ?"+paramidx+" ");
					this.add(oi);
					paramidx++;
				}
				
			}
		};
		
		if (returnObj.length>0){
			logger.error(">>>有回传对象没有被处理");
		}
		
		String 
		sql = 
			"select a.*,b.account,b.is_visitor,c.title from hg_member_exchange a "
			+ "left join hg_member_info b on a.member_id=b.id "
			+ "left join ht_app_exchange c on a.exchange_id=c.id "
			+ where 
			+ "order by id desc ";
		
		List<Map<String,Object>> list = super.getPageListMap(sql, page, pageSize, params.toArray());
		
		Map<String, List<Map<String, Object>>> result = new HashMap<String, List<Map<String, Object>>>();
		result.put("list", list);
		return result;
	}

	@Transactional
	public void exchange(AppVip vip, Map<String, Object> exchgMap, JSONObject juser, MemberInfo user) {
		
		int now = Php2JavaUtil.transTimeJ2P(System.currentTimeMillis());

		if (null != vip){
			Integer day = vip.getDay();
			Integer expire_time = now + day * 60 * 60 * 24;
			if (juser.getInteger("is_vip") == 1){
				expire_time = juser.getInteger("expire_time1") + day * 60 * 60 * 24;
			}
			vip.setUpdateTime(now);
			vip.setStatus(2);
			appVipDao.update(vip);
			
			MemberVip mmvip = new MemberVip();
			mmvip.setCreateTime(now);
			mmvip.setVipId(vip.getId());
			mmvip.setMemberId(juser.getInteger("id"));
			memberVipDao.add(mmvip);
			
			user.setUpdateTime(now);
			user.setExpireTime(expire_time);
			user = memberInfoDao.update(user);
			if (null == user){
				throw new RuntimeException("兑换失败：用户更新失败");
			}
		}
		
		AppExchange aexchange = null;
		try {
			aexchange = appExchangeDao.get(CommUtil.o2i(exchgMap.get("id")));
		} catch (Exception e){
			throw new RuntimeException("兑换失败：app exchange 无效");
		}
		aexchange.setUpdateTime(now);
		aexchange.setStatus(2);
		appExchangeDao.update(aexchange);
		
		MemberExchange mexchange = new MemberExchange();
		mexchange.setCreateTime(now);
		mexchange.setExchangeId(CommUtil.o2i(exchgMap.get("id")));
		mexchange.setMemberId(juser.getInteger("id"));
		memberExchangeDao.add(mexchange);
		
		Map<Integer, AppLevel> app_level = FixedCache.appLevelCache();
		AppLevel current_level = app_level.get(user.getLevelId());
		if (current_level.getGrade() < CommUtil.o2i(exchgMap.get("grade"))){
			user.setLevelId(CommUtil.o2i(exchgMap.get("lid")));
		}
		user.setUpdateTime(now);
		user.setDayViewTimes(user.getDayViewTimes()+CommUtil.o2i(exchgMap.get("day_view_times")));
		user.setTodayViewTimes(user.getTodayViewTimes()+CommUtil.o2i(exchgMap.get("day_view_times")));
		user.setReTodayViewTimes(user.getReTodayViewTimes()+CommUtil.o2i(exchgMap.get("day_view_times")));
		user.setDayCacheTimes(user.getDayCacheTimes()+CommUtil.o2i(exchgMap.get("day_cache_times")));
		user.setTodayCacheTimes(user.getTodayCacheTimes()+CommUtil.o2i(exchgMap.get("day_cache_times")));
		user.setReTodayCacheTimes(user.getReTodayCacheTimes()+CommUtil.o2i(exchgMap.get("day_cache_times")));
		memberInfoDao.update(user);
	}

}
