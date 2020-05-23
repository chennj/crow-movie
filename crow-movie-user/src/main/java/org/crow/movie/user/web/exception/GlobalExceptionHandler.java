package org.crow.movie.user.web.exception;

import org.crow.movie.user.common.db.model.ReturnT;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MultipartException.class)
	@ResponseBody
    public ReturnT<String> handleError1(MultipartException e) {
        return new ReturnT<String>(ReturnT.FAIL_CODE, e.getMessage());
    }
}
