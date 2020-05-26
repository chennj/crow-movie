package org.crow.movie.user.web.api.member.outside;

import org.crow.movie.user.common.db.service.MemberPromoService;
import org.crow.movie.user.web.annotation.Permission;
import org.crow.movie.user.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * @author chenn
 *
 */
@Controller
@RequestMapping("/public/mbrpromo")
@Permission(managerLimit=false)
public class MemberPromoPublicApi extends BaseController{

	@Autowired
	MemberPromoService memberPromoService;
}
