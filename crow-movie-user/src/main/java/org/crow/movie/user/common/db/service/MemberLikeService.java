package org.crow.movie.user.common.db.service;

import javax.transaction.Transactional;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.MemberLikeDao;
import org.crow.movie.user.common.db.entity.MemberLike;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MemberLikeService extends AbstractBaseService<MemberLike> {
	
	@Autowired
	public void setBaseDao(MemberLikeDao dao){
		
		super.setBaseDao(dao);
	}

}
