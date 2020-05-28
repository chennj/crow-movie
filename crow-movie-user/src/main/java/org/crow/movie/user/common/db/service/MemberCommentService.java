package org.crow.movie.user.common.db.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.AppMovieDao;
import org.crow.movie.user.common.db.dao.MemberCommentDao;
import org.crow.movie.user.common.db.entity.AppMovie;
import org.crow.movie.user.common.db.entity.MemberComment;
import org.crow.movie.user.common.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberCommentService extends AbstractBaseService<MemberComment> {
	
	@Autowired
	AppMovieDao appMovieDao;
	
	@Autowired
	public void setBaseDao(MemberCommentDao dao){
		
		super.setBaseDao(dao);
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
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
				
				o = allParams.get("title");
				if (StrUtil.notEmpty(o)){
					where.append("and c.title like '%?"+paramidx+"%' ");
					this.add(o);
					paramidx++;
				}
				
				o = allParams.get("content");
				if (StrUtil.notEmpty(o)){
					where.append("and a.content = ?"+paramidx+" ");
					this.add(o);
					paramidx++;
				}
				
				o = allParams.getOrDefault("fid",0);
				if (StrUtil.notEmpty(o)){
					where.append("and a.fid = ?"+paramidx+" ");
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
			logger.error(">>>有回传对象没有被处理");
		}
		
		String 
		sql = 
			"select a.*,b.account,b.is_visitor,c.title from hg_member_comment a "
			+ "left join hg_member_info b on a.member_id=b.id "
			+ "left join hg_app_movie c on a.movie_id=c.id "
			+ where 
			+ "order by id desc ";
		
		List<Map<String,Object>> list = super.getPageListMap(sql, page, pageSize, params.toArray());
		
		Map<String, List<Map<String, Object>>> result = new HashMap<String, List<Map<String, Object>>>();
		result.put("list", list);
		return result;
	}

	public void delCascade(MemberComment entity) {
		
		//评论数量减一
		AppMovie movie = appMovieDao.get(entity.getMovieId());
		movie.setCommentNum(movie.getCommentNum()-1);
		//删除下级评论
		this.del("fid",entity.getFid());
		//删除评论
		this.del(entity.getId());
	}
}
