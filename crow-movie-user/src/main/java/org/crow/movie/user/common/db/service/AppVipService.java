package org.crow.movie.user.common.db.service;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.AppVipDao;
import org.crow.movie.user.common.db.entity.AppVip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppVipService extends AbstractBaseService<AppVip> {
	
	@Autowired
	public void setBaseDao(AppVipDao dao){
		
		super.setBaseDao(dao);
	}

}
