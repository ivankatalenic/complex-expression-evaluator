package com.ivankatalenic.evaluator.validator;

import java.util.Optional;

public interface SyntaxValidator {
	Optional<String> validate(String expression);
}
