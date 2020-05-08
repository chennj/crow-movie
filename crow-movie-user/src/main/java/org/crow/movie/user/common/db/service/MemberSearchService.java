package org.crow.movie.user.common.db.service;

import javax.transaction.Transactional;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.MemberSearchDao;
import org.crow.movie.user.common.db.entity.MemberSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MemberSearchService extends AbstractBaseService<MemberSearch> {
	
	@Autowired
	public void setBaseDao(MemberSearchDao dao){
		
		super.setBaseDao(dao);
	}

}
