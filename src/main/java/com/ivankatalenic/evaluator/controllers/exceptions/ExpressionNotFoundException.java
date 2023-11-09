package com.ivankatalenic.evaluator.controllers.exceptions;

public class ExpressionNotFoundException extends RuntimeException {
	private final Long id;
	public ExpressionNotFoundException(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}
}
