package org.crow.movie.user.common.db.service;

import javax.transaction.Transactional;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.MemberFeedbackDao;
import org.crow.movie.user.common.db.entity.MemberFeedback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MemberFeedbackService extends AbstractBaseService<MemberFeedback> {
	
	@Autowired
	public void setBaseDao(MemberFeedbackDao dao){
		
		super.setBaseDao(dao);
	}

}
