package org.crow.movie.user.common.db.service;

import javax.transaction.Transactional;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.MemberCommentUpDao;
import org.crow.movie.user.common.db.entity.MemberCommentUp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MemberCommentUpService extends AbstractBaseService<MemberCommentUp> {
	
	@Autowired
	public void setBaseDao(MemberCommentUpDao dao){
		
		super.setBaseDao(dao);
	}

}
