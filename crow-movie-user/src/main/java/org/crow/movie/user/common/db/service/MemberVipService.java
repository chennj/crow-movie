package org.crow.movie.user.common.db.service;

import javax.transaction.Transactional;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.MemberVipDao;
import org.crow.movie.user.common.db.entity.MemberVip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MemberVipService extends AbstractBaseService<MemberVip> {
	
	@Autowired
	public void setBaseDao(MemberVipDao dao){
		
		super.setBaseDao(dao);
	}

}
