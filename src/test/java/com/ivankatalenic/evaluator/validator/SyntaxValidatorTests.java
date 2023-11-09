package com.ivankatalenic.evaluator.validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.ivankatalenic.evaluator.validator.SyntaxValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class SyntaxValidatorTests {

	final ObjectMapper mapper = new JsonMapper();

	@Autowired
	private SyntaxValidator validator;

	@Test
	void success() {
		final var expr = """
				first_name == "Ivan" && (last_name == "Katalenic" OR address[0].address == "127.0.0.1")
				""";
		assertThat(validator.validate(expr)).isEmpty();
	}

	@Test
	void incorrectAndToken() {
		final var expr = """
				first_name == "Ivan" & (last_name == "Katalenic" OR address[0].address == "127.0.0.1")
				""";
		assertThat(validator.validate(expr)).isNotEmpty();
	}

	@Test
	void redundantParentheses() {
		final var expr = """
				first_name == "Ivan" && (last_name == "Katalenic" OR address[0].address == "127.0.0.1"
				""";
		assertThat(validator.validate(expr)).isNotEmpty();
	}

}
