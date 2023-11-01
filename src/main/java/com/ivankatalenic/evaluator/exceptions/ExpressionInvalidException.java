package com.ivankatalenic.evaluator.exceptions;

/**
 * Exception for errors that arise when validating an expression.
 */
public class ExpressionInvalidException extends RuntimeException {
	public ExpressionInvalidException(String message) {
		super(String.format("Cannot validate the expression: %s", message));
	}
}
