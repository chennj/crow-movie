package org.crow.movie.user.common.db.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.MemberPromoDao;
import org.crow.movie.user.common.db.entity.MemberPromo;
import org.crow.movie.user.common.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MemberPromoService extends AbstractBaseService<MemberPromo> {
	
	@Autowired
	public void setBaseDao(MemberPromoDao dao){
		
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
			"select a.*,b.account,b.is_visitor,c.account account2,c.is_visitor is_visitor2 from hg_member_promo a "
			+ "left join hg_member_info b on a.member_id=b.id "
			+ "left join hg_member_info c on a.to_member_id=c.id "
			+ where 
			+ "order by id desc ";
		
		List<Map<String,Object>> list = super.getPageListMap(sql, page, pageSize, params.toArray());
		
		Map<String, List<Map<String, Object>>> result = new HashMap<String, List<Map<String, Object>>>();
		result.put("list", list);
		return result;
	}

	public List<Map<String, Object>> promoList(Integer page, Integer pageSize, Map<String, Object> allParams) {
		
		final StringBuilder where = new StringBuilder("where 1=1 ");
		
		List<Object> params = new ArrayList<Object>(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				int paramidx = 1;
				
				Object 
				o = allParams.get("id");
				if (StrUtil.notEmpty(o)){
					where.append("and promo.member_id = ?"+paramidx+" ");
					this.add(o);
					paramidx++;
				}
				
			}
		};
		
		String 
		sql = 
			"select info.account,info.mobile,info.create_time from hg_member_promo promo "
			+ "left join hg_member_info info on promo.to_member_id=info.id "
			+ where 
			+ "order by promo.create_time desc ";
		
		List<Map<String,Object>> list = super.getPageListMap(sql, page, pageSize, params.toArray());
		
		return list;
	}

}
