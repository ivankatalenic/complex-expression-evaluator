package com.ivankatalenic.evaluator;

import com.fasterxml.jackson.databind.JsonNode;
import com.ivankatalenic.evaluator.models.Expression;

public interface ExpressionEvaluator {

	/**
	 * Evaluates an expression with the given JSON document containing interpretation for the placeholders
	 * in the expression. Placeholders can take form str1.str2[1].str3[1][2].
	 *
	 * @param e            An expression to be evaluated.
	 * @param jsonDocument A JSON document against which the expression is evaluated.
	 * @return A boolean indicating the evaluation result.
	 * @throws EvaluationException When there are evaluation errors.
	 */
	boolean evaluate(Expression e, JsonNode jsonDocument) throws EvaluationException;

	/**
	 * Exception for errors that arise during evaluation of an expression.
	 */
	class EvaluationException extends RuntimeException {
		public EvaluationException(String msg) {
			super(msg);
		}
	}
}
