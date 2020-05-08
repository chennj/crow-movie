package org.crow.movie.user.common.db.service;

import javax.transaction.Transactional;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.MemberMessageDao;
import org.crow.movie.user.common.db.entity.MemberMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MemberMessageService extends AbstractBaseService<MemberMessage> {
	
	@Autowired
	public void setBaseDao(MemberMessageDao dao){
		
		super.setBaseDao(dao);
	}

}
