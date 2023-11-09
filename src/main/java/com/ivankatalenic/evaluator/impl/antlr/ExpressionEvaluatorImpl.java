package com.ivankatalenic.evaluator.impl.antlr;

import com.fasterxml.jackson.databind.JsonNode;
import com.ivankatalenic.evaluator.ExpressionEvaluator;
import com.ivankatalenic.evaluator.models.Expression;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ExpressionEvaluatorImpl implements ExpressionEvaluator {
	public ExpressionEvaluatorImpl() {
	}

	private static ExpressionParser getParser(final ANTLRErrorListener errorListener, final Expression expr) {
		final var charStream = CharStreams.fromString(expr.getValue());

		final var lexer = new ExpressionLexer(charStream);
		lexer.removeErrorListeners();
		lexer.addErrorListener(errorListener);

		final var tokenStream = new CommonTokenStream(lexer);

		final var parser = new ExpressionParser(tokenStream);
		parser.removeErrorListeners();
		parser.addErrorListener(errorListener);

		return parser;
	}

	private record ParseResult(ParseTree parseTree, List<String> errors) {
	}

	private static ParseResult parse(final Expression expr) {
		final var errorListener = new ErrorListener();
		final var parser = getParser(errorListener, expr);
		try {
			var parseTree = parser.start();
			return new ParseResult(parseTree, errorListener.getErrors());
		} catch (RecognitionException re) {
			var errors = errorListener.getErrors();
			errors.addFirst(re.getMessage());
			return new ParseResult(null, errors);
		}
	}

	@Override
	public Optional<String> validate(final Expression expr) {
		if (expr == null) {
			throw new NullPointerException("an expression for validation cannot be null");
		}

		ParseResult parseResult = parse(expr);
		if (!parseResult.errors().isEmpty()) {
			final var errorStr = String.join("; ", parseResult.errors());
			return Optional.of(errorStr);
		}

		return Optional.empty();
	}

	@Override
	public boolean evaluate(final Expression expr, final JsonNode jsonDocument) throws EvaluationException {
		if (expr == null) {
			throw new NullPointerException("an expression for evaluation cannot be null");
		}
		if (jsonDocument == null) {
			throw new NullPointerException("a JSON document with data cannot be null");
		}

		ParseResult parseResult = parse(expr);
		if (!parseResult.errors().isEmpty()) {
			final var errorStr = String.join("; ", parseResult.errors());
			throw new EvaluationException(String.format("encountered syntax errors while evaluating: %s", errorStr));
		}

		final var visitor = new SyntaxTreeVisitor(jsonDocument);
		final Object result = visitor.visit(parseResult.parseTree());
		if (!(result instanceof Boolean)) {
			throw new EvaluationException(String.format("evaluator returned a non-boolean result: %s", result));
		}

		return (Boolean) result;
	}

}
