package org.crow.movie.user.common.db.service;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.MemberSmsDao;
import org.crow.movie.user.common.db.entity.MemberSms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberSmsService extends AbstractBaseService<MemberSms> {
	
	@Autowired
	public void setBaseDao(MemberSmsDao dao){
		
		super.setBaseDao(dao);
	}

}
