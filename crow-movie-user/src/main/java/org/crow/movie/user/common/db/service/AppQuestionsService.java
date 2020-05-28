package org.crow.movie.user.common.db.service;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.AppQuestionsDao;
import org.crow.movie.user.common.db.entity.AppQuestions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppQuestionsService extends AbstractBaseService<AppQuestions> {
	
	@Autowired
	public void setBaseDao(AppQuestionsDao dao){
		
		super.setBaseDao(dao);
	}

}
