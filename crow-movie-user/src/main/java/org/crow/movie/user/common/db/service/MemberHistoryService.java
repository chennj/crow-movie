package org.crow.movie.user.common.db.service;

import javax.transaction.Transactional;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.MemberHistoryDao;
import org.crow.movie.user.common.db.entity.MemberHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MemberHistoryService extends AbstractBaseService<MemberHistory> {
	
	@Autowired
	public void setBaseDao(MemberHistoryDao dao){
		
		super.setBaseDao(dao);
	}

}
