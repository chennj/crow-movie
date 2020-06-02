package org.crow.movie.user.common.db.service;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.WebRegDao;
import org.crow.movie.user.common.db.entity.WebReg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebRegService extends AbstractBaseService<WebReg> {
	
	@Autowired
	public void setBaseDao(WebRegDao dao){
		
		super.setBaseDao(dao);
	}

}
