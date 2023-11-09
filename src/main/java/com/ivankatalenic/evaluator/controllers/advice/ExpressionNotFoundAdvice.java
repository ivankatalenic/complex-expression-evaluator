package com.ivankatalenic.evaluator.controllers.advice;

import com.ivankatalenic.evaluator.controllers.exceptions.ExpressionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExpressionNotFoundAdvice {
	@ExceptionHandler(ExpressionNotFoundException.class)
	public ProblemDetail handler(ExpressionNotFoundException ex) {
		final var problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
		problemDetail.setDetail(String.format("Expression with ID %d is not found", ex.getId()));
		return problemDetail;
	}
}
