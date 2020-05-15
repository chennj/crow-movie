package org.crow.movie.user.common.db.service;

import javax.transaction.Transactional;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.MemberExchangeDao;
import org.crow.movie.user.common.db.entity.MemberExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MemberExchangeService extends AbstractBaseService<MemberExchange> {
	
	@Autowired
	public void setBaseDao(MemberExchangeDao dao){
		
		super.setBaseDao(dao);
	}

}