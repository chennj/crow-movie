package org.crow.movie.user.common.db.config;

import java.util.Optional;

import org.crow.movie.user.common.constant.Const;
import org.crow.movie.user.common.db.entity.MemberInfo;
import org.crow.movie.user.common.util.SessionUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class MemberInfoAuditor implements AuditorAware<Integer> {

	@Override
	public Optional<Integer> getCurrentAuditor() {
		MemberInfo user = (MemberInfo)SessionUtil.getSession(Const.SESSION_USER_INFO_KEY);
		if (null == user || user.getAccount() == null || user.getAccount().toString().trim().length()==0){
			return null;
		} else {
			return Optional.of(user.getId());
		}
	}
}
