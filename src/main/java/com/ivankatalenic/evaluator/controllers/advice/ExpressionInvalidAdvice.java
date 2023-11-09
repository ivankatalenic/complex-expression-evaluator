package com.ivankatalenic.evaluator.controllers.advice;

import com.ivankatalenic.evaluator.controllers.exceptions.ExpressionInvalidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExpressionInvalidAdvice {
	@ExceptionHandler(ExpressionInvalidException.class)
	public ProblemDetail handler(ExpressionInvalidException ex) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
	}
}
