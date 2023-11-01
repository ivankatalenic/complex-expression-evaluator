package com.ivankatalenic.evaluator;

import com.fasterxml.jackson.databind.JsonNode;
import com.ivankatalenic.evaluator.models.Expression;

import java.util.Optional;

public interface ExpressionEvaluator {
	Optional<String> validate(Expression e);
	boolean evaluate(Expression e, final JsonNode jsonDocument) throws RuntimeException;
}
