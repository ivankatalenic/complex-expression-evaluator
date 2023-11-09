package com.ivankatalenic.evaluator.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivankatalenic.evaluator.ExpressionEvaluator;
import com.ivankatalenic.evaluator.controllers.ExpressionController;
import com.ivankatalenic.evaluator.dao.ExpressionDao;
import com.ivankatalenic.evaluator.mapper.ExpressionMapper;
import com.ivankatalenic.evaluator.models.Expression;
import com.ivankatalenic.evaluator.repository.ExpressionRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.ErrorResponseException;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExpressionController.class)
public class ExpressionControllerTests {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper jsonMapper;

	@MockBean
	private ExpressionEvaluator evaluator;
	@MockBean
	private ExpressionMapper mapper;
	@MockBean
	private ExpressionRepository repository;

	@Nested
	class addExpression {

		@Test
		void success() throws Exception {
			final var exprDaoJson = """
					{"name": "first", "value": "true"}""";
			final var exprDao = jsonMapper.readValue(exprDaoJson, ExpressionDao.class);
			final var exprModel = new Expression("first", "true");
			final var expectedExpr = new Expression(1L, "first", "true");
			final var expectedExprDao = new ExpressionDao(1L, "first", "true");
			final var expectedExprJson = jsonMapper.writeValueAsString(expectedExpr);

			when(mapper.daoToModel(exprDao)).thenReturn(exprModel);
			when(evaluator.validate(exprModel)).thenReturn(Optional.empty());
			when(repository.save(exprModel)).thenReturn(expectedExpr);
			when(mapper.modelToDao(expectedExpr)).thenReturn(expectedExprDao);

			mockMvc.perform(post("/expression").contentType(MediaType.APPLICATION_JSON).content(exprDaoJson))
			       .andExpect(status().isOk())
			       .andExpect(content().json(expectedExprJson));

			verify(mapper).daoToModel(exprDao);
			verify(evaluator).validate(exprModel);
			verify(repository).save(exprModel);
			verify(mapper).modelToDao(expectedExpr);
			verifyNoMoreInteractions(mapper, evaluator, repository, mapper);
		}

		@Test
		void missingMediaTypeJSON() throws Exception {
			final var exprJson = """
					{"name": "first", "value": "true"}""";

			mockMvc.perform(post("/expression").content(exprJson))
			       .andExpect(status().isUnsupportedMediaType());

			verifyNoInteractions(mapper, mapper, evaluator, repository);
		}

		@Test
		void invalidJsonFormat() throws Exception {
			final var exprJson = """
					{"name": "failed format", "value": "missing last brace\"""";

			mockMvc.perform(post("/expression").contentType(MediaType.APPLICATION_JSON).content(exprJson))
			       .andExpect(status().isBadRequest());

			verifyNoInteractions(mapper, mapper, evaluator, repository);
		}

		@Test
		void missingExpressionValue() throws Exception {
			final var exprDaoJson = """
					{"name": "expr name"}""";

			mockMvc.perform(post("/expression").contentType(MediaType.APPLICATION_JSON).content(exprDaoJson))
			       .andExpect(status().isBadRequest());

			verifyNoInteractions(mapper, mapper, evaluator, repository);
		}

		@Test
		void missingExpressionName() throws Exception {
			final var exprDaoJson = """
					{"value": "true"}""";

			mockMvc.perform(post("/expression").contentType(MediaType.APPLICATION_JSON).content(exprDaoJson))
			       .andExpect(status().isBadRequest());

			verifyNoInteractions(mapper, mapper, evaluator, repository);
		}

		@Test
		void mappingProblem() throws Exception {
			final var exprDao = new ExpressionDao("first", "true");
			final String exprDaoJson = """
					{"name": "first", "value": "true"}""";
			final var exprModel = new Expression("first", "true");
			final var expectedExpr = new Expression(1L, "first", "true");
			final var expectedExprDao = new ExpressionDao(1L, "first", "true");

			when(mapper.daoToModel(exprDao)).thenThrow(
					new ExpressionMapper.MappingException("err", ExpressionMapper.MappingDirection.DAO_TO_MODEL));
			when(evaluator.validate(exprModel)).thenReturn(Optional.empty());
			when(repository.save(exprModel)).thenReturn(expectedExpr);
			when(mapper.modelToDao(expectedExpr)).thenReturn(expectedExprDao);

			mockMvc.perform(post("/expression").contentType(MediaType.APPLICATION_JSON).content(exprDaoJson))
			       .andExpect(status().isBadRequest());

			verify(mapper).daoToModel(exprDao);
			verifyNoMoreInteractions(mapper);
			verifyNoInteractions(evaluator, repository);
		}

		@Test
		void evaluationProblem() throws Exception {
			final String exprDaoJson = """
					{"name": "first", "value": "t"}""";
			final var exprDao = jsonMapper.readValue(exprDaoJson, ExpressionDao.class);
			final var exprModel = new Expression("first", "t");

			when(mapper.daoToModel(exprDao)).thenReturn(exprModel);
			when(evaluator.validate(exprModel)).thenReturn(Optional.of("error"));

			mockMvc.perform(post("/expression").contentType(MediaType.APPLICATION_JSON).content(exprDaoJson))
			       .andExpect(status().isBadRequest());

			verify(mapper).daoToModel(exprDao);
			verify(evaluator).validate(exprModel);
			verifyNoInteractions(repository);
			verifyNoMoreInteractions(mapper, evaluator);
		}

		@Test
		void repoSavingProblem() throws Exception {
			final var exprDaoJson = """
					{"name": "first", "value": "true"}""";
			final var exprDao = jsonMapper.readValue(exprDaoJson, ExpressionDao.class);
			final var exprModel = new Expression("first", "true");

			when(mapper.daoToModel(exprDao)).thenReturn(exprModel);
			when(evaluator.validate(exprModel)).thenReturn(Optional.empty());
			when(repository.save(exprModel)).thenThrow(new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR));

			mockMvc.perform(post("/expression").contentType(MediaType.APPLICATION_JSON).content(exprDaoJson))
			       .andExpect(status().isInternalServerError());

			verify(mapper).daoToModel(exprDao);
			verify(evaluator).validate(exprModel);
			verify(repository).save(exprModel);
			verifyNoMoreInteractions(mapper, evaluator, repository);
		}

		@Test
		void returnMappingProblem() throws Exception {
			final var exprDaoJson = """
					{"name": "first", "value": "true"}""";
			final var exprDao = jsonMapper.readValue(exprDaoJson, ExpressionDao.class);
			final var exprModel = new Expression("first", "true");
			final var expectedExpr = new Expression(1L, "first", "true");

			when(mapper.daoToModel(exprDao)).thenReturn(exprModel);
			when(evaluator.validate(exprModel)).thenReturn(Optional.empty());
			when(repository.save(exprModel)).thenReturn(expectedExpr);
			when(mapper.modelToDao(expectedExpr)).thenThrow(
					new ExpressionMapper.MappingException("cannot map",
					                                      ExpressionMapper.MappingDirection.MODEL_TO_DAO));

			mockMvc.perform(post("/expression").contentType(MediaType.APPLICATION_JSON).content(exprDaoJson))
			       .andExpect(status().isInternalServerError());

			verify(mapper).daoToModel(exprDao);
			verify(evaluator).validate(exprModel);
			verify(repository).save(exprModel);
			verify(mapper).modelToDao(expectedExpr);
			verifyNoMoreInteractions(mapper, evaluator, repository);
		}
	}

	@Nested
	class evaluate {

		@Test
		void success() throws Exception {
			final var inputJson = """
					{
						"first_name": "Ivan",
						"last_name": "Katalenic",
						"address": [
							{
								"label": "home",
								"address": "127.0.0.1"
							}
						]
					}""";
			final var document = jsonMapper.readTree(inputJson);
			final var expr = new Expression(1L, "first", "some complex expression");

			when(repository.findById(expr.getId())).thenReturn(Optional.of(expr));
			when(evaluator.evaluate(expr, document)).thenReturn(true);

			mockMvc.perform(
					       post("/evaluate")
							       .queryParam("expressionId", "1")
							       .contentType(MediaType.APPLICATION_JSON)
							       .content(inputJson)
			       )
			       .andExpect(status().isOk())
			       .andExpect(content().string("true"));

			verify(repository).findById(expr.getId());
			verify(evaluator).evaluate(expr, document);
			verifyNoMoreInteractions(repository, evaluator);
		}

		@Test
		void invalidContentType() throws Exception {
			final var inputJson = """
					{
						"first_name": "Ivan",
						"last_name": "Katalenic",
						"address": [
							{
								"label": "home",
								"address": "127.0.0.1"
							}
						]
					}""";

			mockMvc.perform(
					       post("/evaluate")
							       .queryParam("expressionId", "1")
							       .content(inputJson)
			       )
			       .andExpect(status().isUnsupportedMediaType());

			verifyNoInteractions(repository, evaluator);
		}

		@Test
		void missingExpressionID() throws Exception {
			final var inputJson = """
					{
						"first_name": "Ivan",
						"last_name": "Katalenic",
						"address": [
							{
								"label": "home",
								"address": "127.0.0.1"
							}
						]
					}""";

			mockMvc.perform(
					       post("/evaluate")
							       .contentType(MediaType.APPLICATION_JSON)
							       .content(inputJson)
			       )
			       .andExpect(status().isBadRequest());

			verifyNoInteractions(repository, evaluator);
		}

		@Test
		void missingJsonBody() throws Exception {
			mockMvc.perform(
					       post("/evaluate")
							       .queryParam("expressionId", "1")
							       .contentType(MediaType.APPLICATION_JSON)
			       )
			       .andExpect(status().isBadRequest());

			verifyNoInteractions(repository, evaluator);
		}

		@Test
		void missingExpression() throws Exception {
			final var inputJson = """
					{
						"first_name": "Ivan",
						"last_name": "Katalenic",
						"address": [
							{
								"label": "home",
								"address": "127.0.0.1"
							}
						]
					}""";
			final var expr = new Expression(1L, "first", "some complex expression");

			when(repository.findById(expr.getId())).thenReturn(Optional.empty());

			mockMvc.perform(
					       post("/evaluate")
							       .queryParam("expressionId", "1")
							       .contentType(MediaType.APPLICATION_JSON)
							       .content(inputJson)
			       )
			       .andExpect(status().isNotFound());

			verify(repository).findById(expr.getId());
			verifyNoMoreInteractions(repository);
			verifyNoInteractions(evaluator);
		}

		@Test
		void invalidJson() throws Exception {
			final var inputJson = """
					{
						"first_name": "Ivan",
						"last_name": "Katalenic",
						"address": [
							{
								"label": "home",
								"address": "127.0.0.1"
							
						]
					}""";

			mockMvc.perform(
					       post("/evaluate")
							       .queryParam("expressionId", "1")
							       .contentType(MediaType.APPLICATION_JSON)
							       .content(inputJson)
			       )
			       .andExpect(status().isBadRequest());

			verifyNoInteractions(repository, evaluator);
		}

		@Test
		void evaluationError() throws Exception {
			final var inputJson = """
					{
						"first_name": "Ivan",
						"last_name": "Katalenic",
						"address": [
							{
								"label": "home",
								"address": "127.0.0.1"
							}
						]
					}""";
			final var document = jsonMapper.readTree(inputJson);
			final var expr = new Expression(1L, "first", "some complex expression");

			when(repository.findById(expr.getId())).thenReturn(Optional.of(expr));
			when(evaluator.evaluate(expr, document)).thenThrow(
					new ExpressionEvaluator.EvaluationException("syntax error"));

			mockMvc.perform(
					       post("/evaluate")
							       .queryParam("expressionId", "1")
							       .contentType(MediaType.APPLICATION_JSON)
							       .content(inputJson)
			       )
			       .andExpect(status().isBadRequest());

			verify(repository).findById(expr.getId());
			verify(evaluator).evaluate(expr, document);
			verifyNoMoreInteractions(repository, evaluator);
		}

	}

}
