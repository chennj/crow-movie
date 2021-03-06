package org.crow.movie.user.common.db.service;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.AppMovieDao;
import org.crow.movie.user.common.db.entity.AppMovie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppMovieService extends AbstractBaseService<AppMovie> {
	
	@Autowired
	public void setBaseDao(AppMovieDao dao){
		
		super.setBaseDao(dao);
	}

}
