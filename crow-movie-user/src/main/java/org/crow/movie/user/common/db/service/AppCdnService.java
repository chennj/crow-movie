package org.crow.movie.user.common.db.service;

import javax.transaction.Transactional;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.AppCdnDao;
import org.crow.movie.user.common.db.entity.AppCdn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AppCdnService extends AbstractBaseService<AppCdn> {
	
	@Autowired
	public void setBaseDao(AppCdnDao dao){
		
		super.setBaseDao(dao);
	}

}
