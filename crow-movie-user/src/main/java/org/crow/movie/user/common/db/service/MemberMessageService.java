package org.crow.movie.user.common.db.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.AppLevelDao;
import org.crow.movie.user.common.db.dao.MemberMessageDao;
import org.crow.movie.user.common.db.entity.MemberMessage;
import org.crow.movie.user.common.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MemberMessageService extends AbstractBaseService<MemberMessage> {
	
	@Autowired
	AppLevelDao appLevelDao;
	
	@Autowired
	public void setBaseDao(MemberMessageDao dao){
		
		super.setBaseDao(dao);
	}

	public Map<String, List<Map<String, Object>>> search(Integer page, Integer pageSize,
			Map<String, Object> allParams , Object...returnObj) {

		final StringBuilder where = new StringBuilder("where 1=1 ");
		
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
				
				o = allParams.get("is_read");
				if (StrUtil.notEmpty(o)){
					where.append("and a.is_read = '?"+paramidx+"' ");
					this.add(o);
					paramidx++;
				}
				
				o = allParams.get("message_type");
				if (StrUtil.notEmpty(o)){
					where.append("and a.message_type = ?"+paramidx+" ");
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
					where.append("and a.create_time >= UNIX_TIMESTAMP(?"+paramidx+") ");
					this.add(o);
					paramidx++;
				}
				
				o = allParams.get("end_time");
				if (StrUtil.notEmpty(o)){
					where.append("and a.create_time < UNIX_TIMESTAMP(?"+paramidx+") ");
					this.add(o);
					paramidx++;
				}
				
			}
		};
		
		if (returnObj.length>0){
			logger.warn(">>>有回传对象没有被处理");
		}
		
		String 
		sql = 
			"select a.*,b.account,b.is_visitor,c.title from hg_member_history a "
			+ "left join hg_member_info b on a.member_id=b.id "
			+ "left join ht_app_movie c on a.movie_id=c.id "
			+ where 
			+ "order by id desc ";
		
		List<Map<String,Object>> list = super.getPageListMap(sql, page, pageSize, params.toArray());
		
		sql = 
			"select id,title from hg_app_level order by id desc";
		List<Map<String,Object>> levelList = appLevelDao.findAllListMap(sql);
		
		Map<String, List<Map<String, Object>>> result = new HashMap<String, List<Map<String, Object>>>();
		result.put("list", list);
		result.put("level_list", levelList);
		return result;
	}
	
	public Map<String, List<Map<String, Object>>> notice(Integer page, Integer pageSize,
			Map<String, Object> allParams , Object...returnObj) {

		final StringBuilder where = new StringBuilder("where message_type = 2 ");
		
		List<Object> params = new ArrayList<Object>(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				int paramidx = 1;
				
				Object 
				o = allParams.get("member_id");
				if (StrUtil.notEmpty(o)){
					where.append("and member_id = '?"+paramidx+"' ");
					this.add(o);
					paramidx++;
				}
				
				o = allParams.get("begin_time");
				if (StrUtil.notEmpty(o)){
					where.append("and create_time >= UNIX_TIMESTAMP(?"+paramidx+") ");
					this.add(o);
					paramidx++;
				}
				
				o = allParams.get("end_time");
				if (StrUtil.notEmpty(o)){
					where.append("and create_time < UNIX_TIMESTAMP(?"+paramidx+") ");
					this.add(o);
					paramidx++;
				}
				
			}
		};
		
		if (returnObj.length>0){
			logger.warn(">>>有回传对象没有被处理");
		}
		
		String 
		sql = 
			"select id,title,content,is_read,create_time from hg_member_message "
			+ where 
			+ "order by is_read asc,create_time desc ";
		
		List<Map<String,Object>> list = super.getPageListMap(sql, page, pageSize, params.toArray());
				
		Map<String, List<Map<String, Object>>> result = new HashMap<String, List<Map<String, Object>>>();
		result.put("list", list);
		return result;
	}
}
