package com.ivankatalenic.evaluator.controllers.exceptions;

public class ExpressionInvalidException extends RuntimeException {
	public ExpressionInvalidException(String message) {
		super(String.format("Cannot validate the expression: %s", message));
	}
}
