package com.ivankatalenic.evaluator.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.ivankatalenic.evaluator.ExpressionEvaluator;
import com.ivankatalenic.evaluator.controllers.exceptions.ExpressionInvalidException;
import com.ivankatalenic.evaluator.controllers.exceptions.ExpressionNotFoundException;
import com.ivankatalenic.evaluator.dao.ExpressionDao;
import com.ivankatalenic.evaluator.mapper.ExpressionMapper;
import com.ivankatalenic.evaluator.models.Expression;
import com.ivankatalenic.evaluator.repository.ExpressionRepository;
import jakarta.validation.Valid;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Expression evaluator controller.
 */
@RestController
public class ExpressionController {

	private final Log log = LogFactory.getLog(getClass());
	private final ExpressionEvaluator evaluator;
	private final ExpressionMapper mapper;
	private final ExpressionRepository repository;

	public ExpressionController(ExpressionEvaluator evaluator, ExpressionMapper mapper, ExpressionRepository repository) {
		this.evaluator = evaluator;
		this.mapper = mapper;
		this.repository = repository;
	}

	/**
	 * Retrieves all stored expressions.
	 *
	 * @return All expressions.
	 */
	@GetMapping("/expressions")
	public List<ExpressionDao> all() {
		return modelsToDaos(repository.findAll());
	}

	/**
	 * Creates a new expression described in the JSON document.
	 *
	 * @param exprDao A new expression
	 * @return A created expression
	 */
	@PostMapping("/expression")
	public ExpressionDao addExpression(@Valid @RequestBody ExpressionDao exprDao) {
		final var expr = mapper.daoToModel(exprDao);
		Optional<String> error = evaluator.validate(expr);
		if (error.isPresent()) {
			throw new ExpressionInvalidException(error.get());
		}
		log.debug(String.format("New expression: Name: %s, Value: %s\n", exprDao.getName(),
		                        exprDao.getValue()));
		return mapper.modelToDao(repository.save(expr));
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

	private List<ExpressionDao> modelsToDaos(List<Expression> expressions) {
		List<ExpressionDao> list = new ArrayList<>(expressions.size());
		for (var e : expressions) {
			list.add(mapper.modelToDao(e));
		}
		return list;
	}

}
