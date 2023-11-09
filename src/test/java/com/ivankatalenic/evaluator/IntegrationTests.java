package com.ivankatalenic.evaluator;

import com.ivankatalenic.evaluator.dao.ExpressionDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Nested
	@DirtiesContext
	class addExpression {
		@Test
		void success() throws URISyntaxException {
			final var url = new URI(String.format("http://localhost:%d/expression", port));
			final var inputExpr = new ExpressionDao("first", "true");
			final var expectedExpr = new ExpressionDao(1L, "first", "true");
			assertThat(restTemplate.postForObject(url, inputExpr, ExpressionDao.class)).isEqualTo(expectedExpr);
		}
	}

	@Nested
	@DirtiesContext
	class evaluate {
		@BeforeEach
		void setupExpression() throws URISyntaxException {
			final var url = new URI(String.format("http://localhost:%d/expression", port));
			final var exprValue = """
					first_name == "Ivan" && (last_name == "Katalenic" OR address[0].address == "127.0.0.1")
					""";
			final var inputExpr = new ExpressionDao("first", exprValue);
			final var expectedExpr = new ExpressionDao(1L, "first", exprValue);
			assertThat(restTemplate.postForObject(url, inputExpr, ExpressionDao.class)).isEqualTo(expectedExpr);
		}

		@Test
		void success() throws URISyntaxException {
			final var exprId = 1L;
			final var url = new URI(String.format("http://localhost:%d/evaluate?expressionId=%d", port, exprId));
			final var jsonDocument = """
					{
						"first_name": "Ivan",
						"last_name": "Katalenic",
						"address": [
							{
								"label": "Home",
								"address": "127.0.0.1"
							}
						]
					}
					""";
			final var headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			final var httpEntity = new HttpEntity<>(jsonDocument, headers);
			final var response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(response.getBody()).isEqualTo("true");
		}
	}

}
