package org.crow.movie.user.common.db.service;

import javax.transaction.Transactional;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.MemberCommentDao;
import org.crow.movie.user.common.db.entity.MemberComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MemberCommentService extends AbstractBaseService<MemberComment> {
	
	@Autowired
	public void setBaseDao(MemberCommentDao dao){
		
		super.setBaseDao(dao);
	}

}
