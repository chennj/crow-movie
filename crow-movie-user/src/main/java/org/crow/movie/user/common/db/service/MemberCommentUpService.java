package org.crow.movie.user.common.db.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.MemberCommentUpDao;
import org.crow.movie.user.common.db.entity.MemberCommentUp;
import org.crow.movie.user.common.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MemberCommentUpService extends AbstractBaseService<MemberCommentUp> {
	
	@Autowired
	public void setBaseDao(MemberCommentUpDao dao){
		
		super.setBaseDao(dao);
	}

	public Map<String, List<Map<String, Object>>> search(Integer page, Integer pageSize,
			Map<String, Object> allParams,Object...returnObj) {
		
		final StringBuilder where 	= new StringBuilder("where 1=1 ");
		
		List<Object> params = new ArrayList<Object>(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				int paramidx = 1;
				
				Object 
				o = allParams.get("comment_id");
				if (StrUtil.notEmpty(o)){
					where.append("and a.comment_id like '?"+paramidx+"' ");
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
			"select a.*,b.account,b.is_visitor from hg_member_comment_up a "
			+ "left join hg_member_info b on a.member_id=b.id "
			+ "left join hg_member_comment c on a.comment_id=c.id "
			+ where 
			+ "order by id desc ";
		
		List<Map<String,Object>> list = super.getPageListMap(sql, page, pageSize, params.toArray());
		
		Map<String, List<Map<String, Object>>> result = new HashMap<String, List<Map<String, Object>>>();
		result.put("list", list);
		return result;

	}

}
