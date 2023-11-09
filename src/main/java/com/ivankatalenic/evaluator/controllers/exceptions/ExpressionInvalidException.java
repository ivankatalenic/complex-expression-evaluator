package com.ivankatalenic.evaluator.controllers.exceptions;

public class ExpressionInvalidException extends RuntimeException {
	public ExpressionInvalidException(String detail) {
		super(detail);
	}
}
