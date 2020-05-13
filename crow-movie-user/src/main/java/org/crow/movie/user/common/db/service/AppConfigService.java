package org.crow.movie.user.common.db.service;

import javax.transaction.Transactional;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.AppConfigDao;
import org.crow.movie.user.common.db.entity.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AppConfigService extends AbstractBaseService<AppConfig> {
	
	@Autowired
	public void setBaseDao(AppConfigDao dao){
		
		super.setBaseDao(dao);
	}

}
