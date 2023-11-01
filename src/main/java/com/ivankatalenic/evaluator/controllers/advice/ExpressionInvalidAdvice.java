package com.ivankatalenic.evaluator.controllers.advice;

import com.ivankatalenic.evaluator.controllers.exceptions.ExpressionInvalidException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExpressionInvalidAdvice {
	@ResponseBody
	@ExceptionHandler(ExpressionInvalidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public String expressionInvalidHandler(ExpressionInvalidException ex) {
		return ex.getMessage();
	}
}
