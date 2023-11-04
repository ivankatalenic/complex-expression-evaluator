package com.ivankatalenic.evaluator.parser;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Error listener for the expression parser that aggregates encountered syntax errors in a list.
 */
public class ExpressionParserErrorListener extends BaseErrorListener {
	private final List<String> errors;

	public ExpressionParserErrorListener() {
		errors = new ArrayList<>();
	}

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer,
	                        Object offendingSymbol, int line, int charPositionInLine,
	                        String msg, RecognitionException re) {
		errors.add(String.format("Syntax error: Line %d: Offset %d: %s", line, charPositionInLine, msg));
	}

	public List<String> getErrors() {
		return errors;
	}
}
