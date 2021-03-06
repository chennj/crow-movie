package org.crow.movie.user.common.db.service;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.AdminInfoDao;
import org.crow.movie.user.common.db.entity.AdminInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminInfoService extends AbstractBaseService<AdminInfo> {
	
	@Autowired
	public void setBaseDao(AdminInfoDao dao){
		
		super.setBaseDao(dao);
	}

}
