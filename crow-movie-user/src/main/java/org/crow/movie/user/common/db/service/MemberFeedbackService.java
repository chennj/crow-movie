package org.crow.movie.user.common.db.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.MemberFeedbackDao;
import org.crow.movie.user.common.db.entity.MemberFeedback;
import org.crow.movie.user.common.util.Php2JavaUtil;
import org.crow.movie.user.common.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberFeedbackService extends AbstractBaseService<MemberFeedback> {
	
	@Autowired
	public void setBaseDao(MemberFeedbackDao dao){
		
		super.setBaseDao(dao);
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
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
				
				o = allParams.get("is_read");
				if (StrUtil.notEmpty(o)){
					where.append("and a.is_read = ?"+paramidx+" ");
					this.add(o);
					paramidx++;
				}
				
				o = allParams.get("admin_is_read");
				if (StrUtil.notEmpty(o)){
					where.append("and a.admin_is_read = ?"+paramidx+" ");
					this.add(o);
					paramidx++;
				}
				
				o = allParams.get("is_visitor");
				if (StrUtil.notEmpty(o)){
					where.append("and b.is_visitor = ?"+paramidx+" ");
					this.add(o);
					paramidx++;
				}
				
				o = allParams.get("feedback_type");
				if (StrUtil.notEmpty(o)){
					where.append("and a.feedback_type = ?"+paramidx+" ");
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
			"select a.*,b.account,b.is_visitor from hg_member_exchange a "
			+ "left join hg_member_info b on a.member_id=b.id "
			+ where 
			+ "order by id desc ";
		
		List<Map<String,Object>> list = super.getPageListMap(sql, page, pageSize, params.toArray());
		
		Map<String, List<Map<String, Object>>> result = new HashMap<String, List<Map<String, Object>>>();
		result.put("list", list);
		return result;
	}

}
