package com.ivankatalenic.evaluator.controllers.advice;

import com.ivankatalenic.evaluator.exceptions.ExpressionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExpressionNotFoundAdvice {
	@ResponseBody
	@ExceptionHandler(ExpressionNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String expressionNotFoundHandler(ExpressionNotFoundException ex) {
		return ex.getMessage();
	}
}
