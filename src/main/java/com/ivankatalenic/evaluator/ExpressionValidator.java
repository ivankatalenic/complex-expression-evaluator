package com.ivankatalenic.evaluator;

import com.ivankatalenic.evaluator.models.Expression;

import java.util.Optional;

public interface ExpressionValidator {
	/**
	 * Validates that an expression conforms to the grammar.
	 *
	 * @param e An expression to be validated.
	 * @return An error string describing encountered syntax errors.
	 */
	Optional<String> validate(Expression e);
}
