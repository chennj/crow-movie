package org.crow.movie.user.common.db.service;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.AppAdvDao;
import org.crow.movie.user.common.db.entity.AppAdv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppAdvService extends AbstractBaseService<AppAdv> {
	
	@Autowired
	public void setBaseDao(AppAdvDao dao){
		
		super.setBaseDao(dao);
	}

}
