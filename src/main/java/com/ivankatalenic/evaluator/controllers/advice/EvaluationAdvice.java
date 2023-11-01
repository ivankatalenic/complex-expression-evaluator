package com.ivankatalenic.evaluator.controllers.advice;

import com.ivankatalenic.evaluator.exceptions.EvaluationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class EvaluationAdvice {
	@ResponseBody
	@ExceptionHandler(EvaluationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public String evaluationExceptionHandler(EvaluationException ex) {
		return ex.getMessage();
	}
}
