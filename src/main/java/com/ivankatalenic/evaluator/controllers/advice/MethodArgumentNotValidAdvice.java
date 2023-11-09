package com.ivankatalenic.evaluator.controllers.advice;

import com.ivankatalenic.evaluator.ExpressionEvaluator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MethodArgumentNotValidAdvice {
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ProblemDetail handler(MethodArgumentNotValidException ex) {
		// TODO Replace the error report for syntax errors and similar instead of generic "Invalid request" message.
		return ex.getBody();
	}
}
