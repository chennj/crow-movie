package org.crow.movie.user.web.api.member.pc;

import java.util.List;
import java.util.Map;

import org.crow.movie.user.common.db.model.ReturnT;
import org.crow.movie.user.common.db.service.MemberLikeService;
import org.crow.movie.user.common.util.RegexUtil;
import org.crow.movie.user.web.annotation.Permission;
import org.crow.movie.user.web.controller.BasePcController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="all")
@Permission(managerLimit=false)
public class MemberPcApi extends BasePcController{

	@Autowired
	private MemberLikeService memberLikeService;
	
	@RequestMapping(value="info", method=RequestMethod.POST)
	public ReturnT<?> info(){
		
		return success(this.getJUser());
	}
	
	@RequestMapping(value="like", method=RequestMethod.POST)
	public ReturnT<?> like(
			@RequestParam(defaultValue="1") String page,
			@RequestParam(defaultValue="20") String pageSize){
		
		//param valid
		if (RegexUtil.isNotNum(page) || RegexUtil.isNotNum(pageSize)){
			return fail("页码尺寸只能时数字");
		}
		
		String
		sql = "select movie.id,movie.title,movie.duration,movie.cover,movie.click_num,movie.like_num "
			+ "from hg_member_like mlike "
			+ "join hg_app_movie movie on mlike.movie_id=movie.id "
			+ "where movie.status = 1 and member_id="+this.getUser().getId()+" "
			+ "mlike.create_time desc";
		
		List<Map<String, Object>> list = memberLikeService.getPageListMap(sql, 
				Integer.valueOf(page), 
				Integer.valueOf(pageSize), new Object[]{});
		
		return success(list);
	}
}
