package com.ivankatalenic.evaluator.controllers.advice;

import com.ivankatalenic.evaluator.mapper.ExpressionMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExpressionMappingAdvice {
	@ExceptionHandler(ExpressionMapper.MappingException.class)
	public ProblemDetail handler(ExpressionMapper.MappingException ex) {
		return switch (ex.getMappingDirection()) {
			case ExpressionMapper.MappingDirection.MODEL_TO_DAO -> ProblemDetail.forStatusAndDetail(
					HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
			case ExpressionMapper.MappingDirection.DAO_TO_MODEL ->
					ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
		};
	}
}
