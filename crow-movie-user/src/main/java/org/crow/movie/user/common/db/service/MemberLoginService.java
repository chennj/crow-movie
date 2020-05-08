package org.crow.movie.user.common.db.service;

import javax.transaction.Transactional;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.MemberLoginDao;
import org.crow.movie.user.common.db.entity.MemberLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MemberLoginService extends AbstractBaseService<MemberLogin> {
	
	@Autowired
	public void setBaseDao(MemberLoginDao dao){
		
		super.setBaseDao(dao);
	}

}
