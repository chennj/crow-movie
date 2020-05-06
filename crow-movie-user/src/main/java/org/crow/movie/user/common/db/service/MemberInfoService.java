package org.crow.movie.user.common.db.service;

import javax.transaction.Transactional;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.dao.MemberInfoDao;
import org.crow.movie.user.common.db.entity.MemberInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MemberInfoService extends AbstractBaseService<MemberInfo> {
	
	@Autowired
	public void setBaseDao(MemberInfoDao dao){
		
		super.setBaseDao(dao);
	}

}
