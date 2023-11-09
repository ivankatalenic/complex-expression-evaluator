package com.ivankatalenic.evaluator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.ivankatalenic.evaluator.models.Expression;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ExpressionEvaluatorTests {

	final ObjectMapper mapper = new JsonMapper();

	@Autowired
	private ExpressionEvaluator evaluator;

	@Nested
	class validate {
		@Test
		void success() {
			final var exprValue = """
					first_name == "Ivan" && (last_name == "Katalenic" OR address[0].address == "127.0.0.1")
					""";
			final var expr = new Expression("first", exprValue);
			assertThat(evaluator.validate(expr)).isEmpty();
		}

		@Test
		void incorrectAndToken() {
			final var exprValue = """
					first_name == "Ivan" & (last_name == "Katalenic" OR address[0].address == "127.0.0.1")
					""";
			final var expr = new Expression("first", exprValue);
			assertThat(evaluator.validate(expr)).isNotEmpty();
		}

		@Test
		void redundantParentheses() {
			final var exprValue = """
					first_name == "Ivan" && (last_name == "Katalenic" OR address[0].address == "127.0.0.1"
					""";
			final var expr = new Expression("first", exprValue);
			assertThat(evaluator.validate(expr)).isNotEmpty();
		}
	}

	@Nested
	class evaluate {
		@Test
		void successPositive() throws JsonProcessingException {
			final var exprValue = """
					first_name == "Ivan" && (last_name == "Katalenic" OR address[0].address == "127.0.0.1")
					""";
			final var expr = new Expression("first", exprValue);
			final var json = mapper.readTree("""
					{
						"first_name": "Ivan",
						"last_name": "Katalenic",
						"address": [
							{
								"label": "home",
								"address": "127.0.0.1"
							}
						]
					}
					""");
			assertThat(evaluator.evaluate(expr, json)).isTrue();
		}

		@Test
		void successNegative() throws JsonProcessingException {
			final var exprValue = """
					first_name == "Ivan" && (last_name == "Katalenic" OR address[0].address == "127.0.0.1")
					""";
			final var expr = new Expression("first", exprValue);
			final var json = mapper.readTree("""
					{
						"first_name": "John",
						"last_name": "Katalenic",
						"address": [
							{
								"label": "home",
								"address": "127.0.0.1"
							}
						]
					}
					""");
			assertThat(evaluator.evaluate(expr, json)).isFalse();
		}

		@Test
		void syntaxError() throws JsonProcessingException {
			final var exprValue = """
					first_name = "Ivan" && (last_name == "Katalenic" OR address[0].address == "127.0.0.1")
					""";
			final var expr = new Expression("first", exprValue);
			final var json = mapper.readTree("""
					{
						"first_name": "Ivan",
						"last_name": "Katalenic",
						"address": [
							{
								"label": "home",
								"address": "127.0.0.1"
							}
						]
					}
					""");
			assertThatExceptionOfType(ExpressionEvaluator.EvaluationException.class).isThrownBy(() -> {
				evaluator.evaluate(expr, json);
			});
		}

		@Test
		void missingValue() throws JsonProcessingException {
			final var exprValue = """
					first_name == "Ivan" && (last_name == "Katalenic" OR address[0].address == "127.0.0.1")
					""";
			final var expr = new Expression("first", exprValue);
			final var json = mapper.readTree("""
					{
						"first_name": "Ivan",
						"address": [
							{
								"label": "home",
								"address": "127.0.0.1"
							}
						]
					}
					""");
			assertThatExceptionOfType(ExpressionEvaluator.EvaluationException.class).isThrownBy(() -> {
				evaluator.evaluate(expr, json);
			});
		}
	}

}
