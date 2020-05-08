package org.crow.movie.user.common.db.service;

import javax.transaction.Transactional;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.MemberMovieUpDownDao;
import org.crow.movie.user.common.db.entity.MemberMovieUpDown;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MemberMovieUpDownService extends AbstractBaseService<MemberMovieUpDown> {
	
	@Autowired
	public void setBaseDao(MemberMovieUpDownDao dao){
		
		super.setBaseDao(dao);
	}

}
