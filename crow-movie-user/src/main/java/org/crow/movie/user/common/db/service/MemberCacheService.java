package org.crow.movie.user.common.db.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.MemberCacheDao;
import org.crow.movie.user.common.db.entity.MemberCache;
import org.crow.movie.user.common.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberCacheService extends AbstractBaseService<MemberCache> {
	
	@Autowired
	public void setBaseDao(MemberCacheDao dao){
		
		super.setBaseDao(dao);
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, List<Map<String, Object>>> search(Integer page, Integer pageSize,
			Map<String, Object> allParams, Object...returnObj) {
		
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
					where.append("and b.is_visitor = '?"+paramidx+"' ");
					this.add(o);
					paramidx++;
				}
				
				o = allParams.get("begin_time");
				if (StrUtil.notEmpty(o)){
					where.append("and a.create_time >= UNIX_TIMESTAMP(?4) ");
					this.add(o);
					paramidx++;
				}
				
				o = allParams.get("end_time");
				if (StrUtil.notEmpty(o)){
					where.append("and a.create_time < UNIX_TIMESTAMP(?5) ");
					this.add(o);
					paramidx++;
				}
			}
		};
		
		if (returnObj.length>0){
			logger.error(">>>有回传对象没有被处理");
		}
		
		String 
		sql = 
			"select a.*,b.account,b.is_visitor,c.title from hg_member_cache a "
			+ "left join hg_member_info b on a.member_id=b.id "
			+ "left join hg_app_movie c on a.movie_id=c.id"
			+ where
			+ "order by id desc";

		List<Map<String,Object>> list = super.getPageListMap(sql, page, pageSize, params.toArray());
		
		Map<String, List<Map<String, Object>>> result = new HashMap<String, List<Map<String, Object>>>();
		result.put("list", list);
		return result;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, List<Map<String, Object>>> cache(Integer page, Integer pageSize,
			Map<String, Object> allParams, Object...returnObj) {
		
		final StringBuilder where 	= new StringBuilder("where 1=1 ");
		
		List<Object> params = new ArrayList<Object>(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				Object 
				o = allParams.get("member_id");
				if (StrUtil.notEmpty(o)){
					where.append("and member_id = ?1 ");
					this.add(o);
				}
				
				o = allParams.get("device_id");
				if (StrUtil.notEmpty(o)){
					where.append("and device_id = ?2 ");
					this.add(o);
				}
				
			}
		};
		
		if (returnObj.length>0){
			logger.error(">>>有回传对象没有被处理");
		}
		
		String 
		sql = 
			"select cache.id,movie.url2,movie.id mid,movie.duration,movie.title,movie.cover "
			+ "from hg_member_cache cache "
			+ "left join hg_app_movie movie on cache.movie_id=movie.id "
			+ where
			+ "order by cache.create_time desc";

		List<Map<String,Object>> list = super.getPageListMap(sql, page, pageSize, params.toArray());
		
		Map<String, List<Map<String, Object>>> result = new HashMap<String, List<Map<String, Object>>>();
		result.put("cache_list", list);
		
		return result;
	}

}
