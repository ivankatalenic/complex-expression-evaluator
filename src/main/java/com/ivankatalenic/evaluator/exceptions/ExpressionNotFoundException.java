package com.ivankatalenic.evaluator.exceptions;

/**
 * Exception for a request for a nonexistent expression.
 */
public class ExpressionNotFoundException extends RuntimeException {
	public ExpressionNotFoundException(Long id) {
		super(String.format("Could not find the expression with ID: %d", id));
	}
}
