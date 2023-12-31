package com.ivankatalenic.evaluator.controllers.advice;

import com.ivankatalenic.evaluator.ExpressionEvaluator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class EvaluationAdvice {
	@ExceptionHandler(ExpressionEvaluator.EvaluationException.class)
	public ProblemDetail handler(ExpressionEvaluator.EvaluationException ex) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
	}
}
