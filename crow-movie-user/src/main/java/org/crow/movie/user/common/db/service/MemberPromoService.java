package org.crow.movie.user.common.db.service;

import javax.transaction.Transactional;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.MemberPromoDao;
import org.crow.movie.user.common.db.entity.MemberPromo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MemberPromoService extends AbstractBaseService<MemberPromo> {
	
	@Autowired
	public void setBaseDao(MemberPromoDao dao){
		
		super.setBaseDao(dao);
	}

}
