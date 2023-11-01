package com.ivankatalenic.evaluator.parser;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExpressionParserErrorListener extends BaseErrorListener {
	private final List<String> errors;

	public ExpressionParserErrorListener() {
		this.errors = new ArrayList<String>();
	}

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer,
	                        Object offendingSymbol, int line, int charPositionInLine,
	                        String msg, RecognitionException re) {
		List<String> stack = ((Parser) recognizer).getRuleInvocationStack();
		Collections.reverse(stack);
		errors.add(String.format("Syntax error: Rule stack %s: Line %d: Offset %d: %s",
				stack, line, charPositionInLine, msg));
	}

	public List<String> getErrors() {
		return errors;
	}
}
