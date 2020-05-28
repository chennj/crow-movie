package org.crow.movie.user.common.db.service;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.AppLevelDao;
import org.crow.movie.user.common.db.entity.AppLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppLevelService extends AbstractBaseService<AppLevel> {
	
	@Autowired
	public void setBaseDao(AppLevelDao dao){
		
		super.setBaseDao(dao);
	}

}
