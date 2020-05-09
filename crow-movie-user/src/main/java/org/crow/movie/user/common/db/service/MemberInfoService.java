package org.crow.movie.user.common.db.service;

import java.util.List;

import org.crow.movie.user.common.db.AbstractBaseService;
import org.crow.movie.user.common.db.Page;
import org.crow.movie.user.common.db.dao.MemberCacheDao;
import org.crow.movie.user.common.db.dao.MemberClickAdvDao;
import org.crow.movie.user.common.db.dao.MemberCommentDao;
import org.crow.movie.user.common.db.dao.MemberCommentUpDao;
import org.crow.movie.user.common.db.dao.MemberExchangeDao;
import org.crow.movie.user.common.db.dao.MemberFeedbackDao;
import org.crow.movie.user.common.db.dao.MemberHistoryDao;
import org.crow.movie.user.common.db.dao.MemberInfoDao;
import org.crow.movie.user.common.db.dao.MemberLikeDao;
import org.crow.movie.user.common.db.dao.MemberMessageDao;
import org.crow.movie.user.common.db.dao.MemberMovieUpDownDao;
import org.crow.movie.user.common.db.dao.MemberPromoDao;
import org.crow.movie.user.common.db.dao.MemberSearchDao;
import org.crow.movie.user.common.db.dao.MemberSmsDao;
import org.crow.movie.user.common.db.entity.MemberInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberInfoService extends AbstractBaseService<MemberInfo> {
	
	@Autowired
	MemberInfoDao mmInfoDao;
	
	@Autowired
	MemberCacheDao mmCacheDao;
	@Autowired
	MemberClickAdvDao mmClickAdvDao;
	@Autowired
	MemberCommentDao mmCommentDao;
	@Autowired
	MemberCommentUpDao mmCommentUpDao;
	@Autowired
	MemberExchangeDao mmExchangeDao;
	@Autowired
	MemberFeedbackDao mmFeedbackDao;
	@Autowired
	MemberHistoryDao mmHistoryDao;
	@Autowired
	MemberLikeDao	mmLikeDao;
	@Autowired
	MemberMessageDao mmMessageDao;
	@Autowired
	MemberMovieUpDownDao mmMovieUpDownDao;
	@Autowired
	MemberPromoDao mmPromoDao;
	@Autowired
	MemberSearchDao mmSearchDao;
	@Autowired
	MemberSmsDao mmSmsDao;
	
	@Autowired
	public void setBaseDao(MemberInfoDao dao){
		
		super.setBaseDao(dao);
	}

	public void delete(Integer id) {
		
		super.del(id);
		/**
		 * 还有关联表的删除
		 */
		mmCacheDao.del("memberId", id);
		mmClickAdvDao.del("memberId", id);
		mmCommentDao.del("memberId", id);
		mmCommentUpDao.del("memberId", id);
		mmExchangeDao.del("memberId", id);
		mmFeedbackDao.del("memberId", id);
		mmHistoryDao.del("memberId", id);
		mmLikeDao.del("memberId", id);
		mmMessageDao.del("memberId", id);
		mmMovieUpDownDao.del("memberId", id);
		mmPromoDao.del("memberId", id);
		mmPromoDao.del("toMemberId", id);
		mmSearchDao.del("memberId", id);
		mmSmsDao.del("memberId", id);

	}

	public int updateStatus(Integer id) {
		
		String sql = "update hg_member_info m set m.status = if(m.status=1,2,1) where id=?1";
		return mmInfoDao.executeNative(sql,id);
	}

	@Transactional(readOnly=true)
	public Page<?> page(String nativeSql, int page, int pageSize, List<Object> params) {
		// TODO Auto-generated method stub
		return super.page(nativeSql, page, pageSize, params);
	}
	
	

}
