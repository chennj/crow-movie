package org.crow.movie.user.common.db.service;

import java.util.Map;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.AppExchangeDao;
import org.crow.movie.user.common.db.entity.AppExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppExchangeService extends AbstractBaseService<AppExchange> {
	
	@Autowired
	AppExchangeDao appExchangeDao;
	
	@Autowired
	public void setBaseDao(AppExchangeDao dao){
		
		super.setBaseDao(dao);
	}

	public Map<String, Object> unique(String code) {

		String 
		sql =
			"select exchange.id,level.id lid,level.grade,level.day_view_times,level.day_cache_times "
			+ "from hg_app_exchange exchange "
			+ "join hg_app_level level on exchange.level_id=level.id "
			+ "where exchange.code = ?1 and exchange.status = 1";
		
		return appExchangeDao.findUnique(sql, code);
	}

}
