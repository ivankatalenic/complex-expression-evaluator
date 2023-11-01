package com.ivankatalenic.evaluator;

import com.fasterxml.jackson.databind.JsonNode;
import com.ivankatalenic.evaluator.exceptions.EvaluationException;
import com.ivankatalenic.evaluator.grammar.ExpressionLexer;
import com.ivankatalenic.evaluator.grammar.ExpressionParser;
import com.ivankatalenic.evaluator.models.Expression;
import com.ivankatalenic.evaluator.parser.ExpressionParserErrorListener;
import com.ivankatalenic.evaluator.parser.ExpressionSyntaxTreeVisitor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ExpressionEvaluatorImpl implements ExpressionEvaluator {
	public ExpressionEvaluatorImpl() {
	}

	private static ExpressionParser getExpressionParser(final Expression e) {
		final var charStream = CharStreams.fromString(e.getValue());
		final var lexer = new ExpressionLexer(charStream);
		final var tokenStream = new CommonTokenStream(lexer);
		return new ExpressionParser(tokenStream);
	}

	@Override
	public Optional<String> validate(final Expression e) {
		if (e == null) {
			throw new NullPointerException("an expression for validation cannot be null");
		}
		final var parser = getExpressionParser(e);
		final var errorListener = new ExpressionParserErrorListener();
		parser.removeErrorListeners();
		parser.addErrorListener(errorListener);
		try {
			parser.start();
		} catch (RecognitionException re) {
			return Optional.of(re.getMessage());
		}

		if (parser.getNumberOfSyntaxErrors() > 0) {
			final var errors = String.join("; ", errorListener.getErrors());
			return Optional.of(errors);
		}
		return Optional.empty();
	}

	@Override
	public boolean evaluate(final Expression e, final JsonNode jsonDocument) throws EvaluationException {
		if (e == null) {
			throw new NullPointerException("an expression for evaluation cannot be null");
		}
		if (jsonDocument == null) {
			throw new NullPointerException("a JSON document with data cannot be null");
		}
		final var parser = getExpressionParser(e);
		final var errorListener = new ExpressionParserErrorListener();
		parser.removeErrorListeners();
		parser.addErrorListener(errorListener);
		final var tree = parser.start();

		if (parser.getNumberOfSyntaxErrors() > 0) {
			final var errors = String.join("; ", errorListener.getErrors());
			throw new EvaluationException(String.format("encountered syntax errors while evaluating: %s", errors));
		}

		final var visitor = new ExpressionSyntaxTreeVisitor(jsonDocument);
		final Object result = visitor.visit(tree);
		if (!(result instanceof Boolean)) {
			throw new EvaluationException(String.format("evaluator returned a non-boolean result: %s", result));
		}

		return (Boolean) result;
	}

}
