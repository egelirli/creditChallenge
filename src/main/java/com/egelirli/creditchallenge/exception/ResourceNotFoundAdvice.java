package com.egelirli.creditchallenge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@ControllerAdvice
public class ResourceNotFoundAdvice {

	@ResponseBody
	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String resourceNotFoundHandler(ResourceNotFoundException ex) {
	    return ex.getMessage();
	}	
	
}
