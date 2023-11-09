package com.ivankatalenic.evaluator.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SyntaxValidatorBase.class)
public @interface CheckSyntax {
	String message() default "{com.ivankatalenic.evaluator.validator.CheckSyntax.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
