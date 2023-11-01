package com.ivankatalenic.evaluator.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.ivankatalenic.evaluator.*;
import com.ivankatalenic.evaluator.controllers.exceptions.ExpressionInvalidException;
import com.ivankatalenic.evaluator.controllers.exceptions.ExpressionNotFoundException;
import com.ivankatalenic.evaluator.models.Expression;
import com.ivankatalenic.evaluator.repository.ExpressionRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ExpressionController {

	private final Log log = LogFactory.getLog(getClass());
	private final ExpressionEvaluator evaluator;
	private final ExpressionRepository repository;

	public ExpressionController(ExpressionEvaluator evaluator, ExpressionRepository repository) {
		this.evaluator = evaluator;
		this.repository = repository;
	}

	@GetMapping("/expressions")
	public List<Expression> all() {
		return repository.findAll();
	}

	@PostMapping("/expression")
	public Expression expression(@RequestBody Expression newExpression) {
		Optional<String> error = evaluator.validate(newExpression);
		if (error.isPresent()) {
			throw new ExpressionInvalidException(error.get());
		}
		log.debug(String.format("New expression: Name: %s, Value: %s\n", newExpression.getName(), newExpression.getValue()));
		return repository.save(newExpression);
	}

	@PostMapping("/evaluate")
	public boolean evaluate(@RequestParam Long id, @RequestBody JsonNode document) {
		Expression expression = repository.findById(id).orElseThrow(() -> new ExpressionNotFoundException(id));
		return evaluator.evaluate(expression, document);
	}

}
