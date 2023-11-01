package com.ivankatalenic.evaluator.exceptions;

/**
 * Exception for errors that arise during evaluation of an expression.
 */
public class EvaluationException extends RuntimeException {
	public EvaluationException(String msg) {
		super(msg);
	}
}
