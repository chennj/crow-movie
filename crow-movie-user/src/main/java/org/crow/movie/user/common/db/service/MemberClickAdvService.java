package org.crow.movie.user.common.db.service;

import javax.transaction.Transactional;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.MemberClickAdvDao;
import org.crow.movie.user.common.db.entity.MemberClickAdv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MemberClickAdvService extends AbstractBaseService<MemberClickAdv> {
	
	@Autowired
	public void setBaseDao(MemberClickAdvDao dao){
		
		super.setBaseDao(dao);
	}

}
