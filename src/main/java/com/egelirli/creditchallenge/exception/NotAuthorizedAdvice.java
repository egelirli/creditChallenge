package com.egelirli.creditchallenge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@ControllerAdvice
public class NotAuthorizedAdvice {

	@ResponseBody
	@ExceptionHandler(NotAuthorizedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	String resourceNotFoundHandler(NotAuthorizedException ex) {
	    return ex.getMessage();
	}	
	
}
