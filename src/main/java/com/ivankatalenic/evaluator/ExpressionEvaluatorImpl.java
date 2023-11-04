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
import org.antlr.v4.runtime.tree.ParseTree;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ExpressionEvaluatorImpl implements ExpressionEvaluator {
	public ExpressionEvaluatorImpl() {
	}

	private static ExpressionParser getParser(final Expression expr) {
		final var charStream = CharStreams.fromString(expr.getValue());
		final var lexer = new ExpressionLexer(charStream);
		final var tokenStream = new CommonTokenStream(lexer);
		return new ExpressionParser(tokenStream);
	}

	private static ExpressionParserErrorListener getParserErrorListener(final ExpressionParser parser) {
		final var errorListener = new ExpressionParserErrorListener();
		parser.removeErrorListeners();
		parser.addErrorListener(errorListener);
		return errorListener;
	}

	private record ParseResult(ParseTree parseTree, List<String> errors) {
	}

	private static ParseResult parse(final Expression expr) {
		final var parser = getParser(expr);
		final var parserErrorListener = getParserErrorListener(parser);
		try {
			var parseTree = parser.start();
			return new ParseResult(parseTree, parserErrorListener.getErrors());
		} catch (RecognitionException re) {
			var errors = parserErrorListener.getErrors();
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

		final var visitor = new ExpressionSyntaxTreeVisitor(jsonDocument);
		final Object result = visitor.visit(parseResult.parseTree());
		if (!(result instanceof Boolean)) {
			throw new EvaluationException(String.format("evaluator returned a non-boolean result: %s", result));
		}

		return (Boolean) result;
	}

}
