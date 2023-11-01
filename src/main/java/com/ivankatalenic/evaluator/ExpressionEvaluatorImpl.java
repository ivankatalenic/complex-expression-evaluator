package com.ivankatalenic.evaluator;

import com.fasterxml.jackson.databind.JsonNode;
import com.ivankatalenic.evaluator.models.Expression;
import com.ivankatalenic.evaluator.parser.ExpressionSyntaxTreeVisitor;
import com.ivankatalenic.evaluator.parser.ExpressionParserErrorListener;
import com.ivankatalenic.evaluator.grammar.ExpressionLexer;
import com.ivankatalenic.evaluator.grammar.ExpressionParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ExpressionEvaluatorImpl implements ExpressionEvaluator {
	public ExpressionEvaluatorImpl() {
	}

	@Override
	public Optional<String> validate(final Expression e) {
		if (e == null) {
			throw new NullPointerException("an expression for validation cannot be null");
		}
		final CharStream charStream = CharStreams.fromString(e.getValue());
		final ExpressionLexer lexer = new ExpressionLexer(charStream);
		final TokenStream tokenStream = new CommonTokenStream(lexer);
		final ExpressionParser parser = new ExpressionParser(tokenStream);
		final ExpressionParserErrorListener errorListener = new ExpressionParserErrorListener();
		parser.removeErrorListeners();
		parser.addErrorListener(errorListener);
		try {
			parser.start();
		} catch (RecognitionException re) {
			return Optional.of(re.getMessage());
		}

		if (parser.getNumberOfSyntaxErrors() > 0) {
			return Optional.of(String.join("\n", errorListener.getErrors()));
		}
		return Optional.empty();
	}

	@Override
	public boolean evaluate(final Expression e, final JsonNode jsonDocument) throws RuntimeException {
		if (e == null) {
			throw new NullPointerException("an expression for evaluation cannot be null");
		}
		if (jsonDocument == null) {
			throw new NullPointerException("a JSON document with data cannot be null");
		}
		final CharStream charStream = CharStreams.fromString(e.getValue());
		final ExpressionLexer lexer = new ExpressionLexer(charStream);
		final TokenStream tokenStream = new CommonTokenStream(lexer);
		final ExpressionParser parser = new ExpressionParser(tokenStream);
		final ExpressionParserErrorListener errorListener = new ExpressionParserErrorListener();
		parser.removeErrorListeners();
		parser.addErrorListener(errorListener);
		final ParseTree tree = parser.start();

		if (parser.getNumberOfSyntaxErrors() > 0) {
			throw new RuntimeException("Encountered syntax errors while evaluating: " + String.join("; ", errorListener.getErrors()));
		}

		ExpressionSyntaxTreeVisitor visitor = new ExpressionSyntaxTreeVisitor(jsonDocument);
		Object res = visitor.visit(tree);
		if (!(res instanceof Boolean)) {
			throw new RuntimeException("evaluator returned a non-boolean result: " + res);
		}

		return (Boolean) res;
	}

}
