package com.ivankatalenic.evaluator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.ivankatalenic.evaluator.models.Expression;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ExpressionValidatorTests {

	final ObjectMapper mapper = new JsonMapper();

	@Autowired
	private ExpressionValidator validator;

	@Test
	void success() {
		final var exprValue = """
				first_name == "Ivan" && (last_name == "Katalenic" OR address[0].address == "127.0.0.1")
				""";
		final var expr = new Expression("first", exprValue);
		assertThat(validator.validate(expr)).isEmpty();
	}

	@Test
	void incorrectAndToken() {
		final var exprValue = """
				first_name == "Ivan" & (last_name == "Katalenic" OR address[0].address == "127.0.0.1")
				""";
		final var expr = new Expression("first", exprValue);
		assertThat(validator.validate(expr)).isNotEmpty();
	}

	@Test
	void redundantParentheses() {
		final var exprValue = """
				first_name == "Ivan" && (last_name == "Katalenic" OR address[0].address == "127.0.0.1"
				""";
		final var expr = new Expression("first", exprValue);
		assertThat(validator.validate(expr)).isNotEmpty();
	}


}
