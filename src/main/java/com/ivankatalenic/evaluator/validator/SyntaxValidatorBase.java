package com.ivankatalenic.evaluator.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class SyntaxValidatorBase implements ConstraintValidator<CheckSyntax, String> {

	private final SyntaxValidator syntaxValidator;

	public SyntaxValidatorBase(SyntaxValidator syntaxValidator) {
		this.syntaxValidator = syntaxValidator;
	}

	@Override
	public void initialize(CheckSyntax constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
	}

	@Override
	public boolean isValid(String s, ConstraintValidatorContext ctx) {
		if (s == null) {
			return true;
		}
		final var error = syntaxValidator.validate(s);
		if (error.isPresent()) {
			ctx.disableDefaultConstraintViolation();
			ctx.buildConstraintViolationWithTemplate(error.get()).addConstraintViolation();
			return false;
		}
		return true;
	}
}
