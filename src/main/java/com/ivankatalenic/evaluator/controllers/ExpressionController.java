package com.ivankatalenic.evaluator.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.ivankatalenic.evaluator.ExpressionEvaluator;
import com.ivankatalenic.evaluator.exceptions.ExpressionInvalidException;
import com.ivankatalenic.evaluator.exceptions.ExpressionNotFoundException;
import com.ivankatalenic.evaluator.models.Expression;
import com.ivankatalenic.evaluator.repository.ExpressionRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Expression evaluator controller.
 */
@RestController
public class ExpressionController {

	private final Log log = LogFactory.getLog(getClass());
	private final ExpressionEvaluator evaluator;
	private final ExpressionRepository repository;

	public ExpressionController(ExpressionEvaluator evaluator, ExpressionRepository repository) {
		this.evaluator = evaluator;
		this.repository = repository;
	}

	/**
	 * Retrieves all stored expressions.
	 *
	 * @return All expressions.
	 */
	@GetMapping("/expressions")
	public List<Expression> all() {
		return repository.findAll();
	}

	/**
	 * Creates a new expression described in the JSON document.
	 *
	 * @param newExpression A new expression
	 * @return A created expression
	 */
	@PostMapping("/expression")
	public Expression expression(@RequestBody Expression newExpression) {
		Optional<String> error = evaluator.validate(newExpression);
		if (error.isPresent()) {
			throw new ExpressionInvalidException(error.get());
		}
		log.debug(String.format("New expression: Name: %s, Value: %s\n", newExpression.getName(),
		                        newExpression.getValue()));
		return repository.save(newExpression);
	}

	/**
	 * Evaluates the expression specified with an ID against the provided JSON document.
	 *
	 * @param expressionId An ID of the expression to be evaluated.
	 * @param document     A JSON document containing values for placeholders in the expression.
	 * @return A boolean describing evaluation result.
	 */
	@PostMapping("/evaluate")
	public boolean evaluate(@RequestParam Long expressionId, @RequestBody JsonNode document) {
		Expression expression = repository.findById(expressionId)
		                                  .orElseThrow(() -> new ExpressionNotFoundException(expressionId));
		return evaluator.evaluate(expression, document);
	}

}
