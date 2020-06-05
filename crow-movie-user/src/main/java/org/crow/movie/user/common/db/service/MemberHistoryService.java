package org.crow.movie.user.common.db.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.MemberHistoryDao;
import org.crow.movie.user.common.db.entity.MemberHistory;
import org.crow.movie.user.common.util.CommUtil;
import org.crow.movie.user.common.util.Php2JavaUtil;
import org.crow.movie.user.common.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberHistoryService extends AbstractBaseService<MemberHistory> {
	
	@Autowired
	public void setBaseDao(MemberHistoryDao dao){
		
		super.setBaseDao(dao);
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, List<Map<String, Object>>> search(Integer page, Integer pageSize,
			Map<String, Object> allParams , Object...returnObj) throws ParseException {

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
				
				o = allParams.get("movie_id");
				if (StrUtil.notEmpty(o)){
					where.append("and a.movie_id = ?"+paramidx+" ");
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
			"select a.*,b.account,b.is_visitor,c.title from hg_member_history a "
			+ "left join hg_member_info b on a.member_id=b.id "
			+ "left join ht_app_movie c on a.movie_id=c.id "
			+ where 
			+ "order by id desc ";
		
		List<Map<String,Object>> list = super.getPageListMap(sql, page, pageSize, params.toArray());
		
		Map<String, List<Map<String, Object>>> result = new HashMap<String, List<Map<String, Object>>>();
		result.put("list", list);
		return result;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Map<String, Object>> history(
			Integer page, Integer pageSize, 
			Map<String, Object> allParams, Integer memberId) throws ParseException {
		
		Integer history_type =  CommUtil.o2i(allParams.getOrDefault("type", 1));
		
		SimpleDateFormat sft = new SimpleDateFormat("yyyy-MM-dd");
		long l = sft.parse(sft.format(new Date())).getTime();
		int now = Php2JavaUtil.transTimeJ2P(l);
		int now7before = now - 7*24*60*60;
		
		StringBuilder where = new StringBuilder("where member_id = ");
		where.append(memberId);
		
		switch (history_type) {
        case 2:
            where.append("and history.create_time < "+now+" ");
            where.append("and history.create_time >= "+now7before+" ");
            break;
        case 3:
            where.append("and history.create_time < "+now7before+" ");
            break;
        default:
            where.append("and history.create_time >= "+now+" ");
            break;
    }
		
		String 
		sql = 
			"select history.id,movie.id mid,movie.duration,movie.title,movie.cover "
			+ "from hg_member_history history "
			+ "join hg_app_movie movie on history.movie_id=movie.id "
			+ where 
			+ "history.create_time desc";
		
		List<Map<String,Object>> list = super.getPageListMap(sql, page, pageSize, new Object[]{});
		
		return list;
	}
}
