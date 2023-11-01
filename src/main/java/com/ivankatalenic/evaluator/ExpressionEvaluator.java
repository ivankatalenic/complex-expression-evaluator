package com.ivankatalenic.evaluator;

import com.fasterxml.jackson.databind.JsonNode;
import com.ivankatalenic.evaluator.exceptions.EvaluationException;
import com.ivankatalenic.evaluator.models.Expression;

import java.util.Optional;

public interface ExpressionEvaluator {
	/**
	 * Validates that an expression conforms to the grammar.
	 * @param e An expression to be validated.
	 * @return An error string describing encountered syntax errors.
	 */
	Optional<String> validate(Expression e);

	/**
	 * Evaluates an expression with the given JSON document containing interpretation for the placeholders
	 * in the expression. Placeholders can take form str1.str2[1].str3[1][2].
	 * @param e An expression to be evaluated.
	 * @param jsonDocument A JSON document against which the expression is evaluated.
	 * @return A boolean indicating the evaluation result.
	 * @throws EvaluationException When there are evaluation errors.
	 */
	boolean evaluate(Expression e, JsonNode jsonDocument) throws EvaluationException;
}
