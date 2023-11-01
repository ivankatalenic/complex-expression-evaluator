package com.ivankatalenic.evaluator.controllers.exceptions;

public class ExpressionNotFoundException extends RuntimeException {
	public ExpressionNotFoundException(Long id) {
		super(String.format("Could not find the expression with ID: %d", id));
	}
}
