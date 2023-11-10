package com.ivankatalenic.evaluator.impl.antlr;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.ivankatalenic.evaluator.ExpressionEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiPredicate;

/**
 * A syntax tree visitor that computes the final boolean value of a parsed expression using the given JSON document.
 */
public class SyntaxTreeVisitor extends ExpressionBaseVisitor<Object> {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final Object nullObject = new Object();
	private final JsonNode rootJsonNode;
	private JsonNode currentJsonNode;
	private StringBuilder currentJsonPath;

	public SyntaxTreeVisitor(final JsonNode rootJsonNode) {
		this.rootJsonNode = rootJsonNode;
	}

	private static boolean compareBoolExpr(Object left, Object right,
			BiPredicate<Long, Long> longPredicate,
			BiPredicate<Double, Double> doublePredicate) throws ExpressionEvaluator.EvaluationException {
		return switch (left) {
			case Long leftLong -> {
				if (right instanceof Long rightLong) {
					yield longPredicate.test(leftLong, rightLong);
				}
				throw new ExpressionEvaluator.EvaluationException(
						String.format("cannot compare an integer with a non-integer, that is %s", right.getClass()));
			}
			case Double leftDouble -> {
				if (right instanceof Double rightDouble) {
					yield doublePredicate.test(leftDouble, rightDouble);
				}
				throw new ExpressionEvaluator.EvaluationException(
						String.format("cannot compare a float with a non-float, that is %s", right.getClass()));
			}
			default -> throw new ExpressionEvaluator.EvaluationException(
					String.format("cannot use numerical relational operator on a non-numerical value of %s",
							left.getClass()));
		};
	}

	@Override
	public Object visitStartExpr(ExpressionParser.StartExprContext ctx) {
		return visit(ctx.expr());
	}

	@Override
	public Object visitExprAnd(ExpressionParser.ExprAndContext ctx) {
		final Boolean left = (Boolean) visit(ctx.expr());
		if (!left) {
			return false;
		}
		return visit(ctx.par_expr());
	}

	@Override
	public Object visitExprOr(ExpressionParser.ExprOrContext ctx) {
		final Boolean left = (Boolean) visit(ctx.expr());
		if (left) {
			return true;
		}
		return visit(ctx.par_expr());
	}

	@Override
	public Object visitExprPar(ExpressionParser.ExprParContext ctx) {
		return visit(ctx.par_expr());
	}

	@Override
	public Object visitParExprBool(ExpressionParser.ParExprBoolContext ctx) {
		return visit(ctx.bool_expr());
	}

	@Override
	public Object visitParExprExpr(ExpressionParser.ParExprExprContext ctx) {
		return visit(ctx.expr());
	}

	@Override
	public Object visitBoolExprTrue(ExpressionParser.BoolExprTrueContext ctx) {
		return true;
	}

	@Override
	public Object visitBoolExprFalse(ExpressionParser.BoolExprFalseContext ctx) {
		return false;
	}

	@Override
	public Object visitBoolExprEq(ExpressionParser.BoolExprEqContext ctx) {
		Object left = visit(ctx.eq_elem(0));
		Object right = visit(ctx.eq_elem(1));
		return left.equals(right);
	}

	@Override
	public Object visitBoolExprNeq(ExpressionParser.BoolExprNeqContext ctx) {
		Object left = visit(ctx.eq_elem(0));
		Object right = visit(ctx.eq_elem(1));
		return !left.equals(right);
	}

	@Override
	public Object visitBoolExprLt(ExpressionParser.BoolExprLtContext ctx) {
		Object left = visit(ctx.ineq_elem(0));
		Object right = visit(ctx.ineq_elem(1));
		return compareBoolExpr(left, right, (a, b) -> a < b, (a, b) -> a < b);
	}

	@Override
	public Object visitBoolExprGt(ExpressionParser.BoolExprGtContext ctx) {
		Object left = visit(ctx.ineq_elem(0));
		Object right = visit(ctx.ineq_elem(1));
		return compareBoolExpr(left, right, (a, b) -> a > b, (a, b) -> a > b);
	}

	@Override
	public Object visitBoolExprLe(ExpressionParser.BoolExprLeContext ctx) {
		Object left = visit(ctx.ineq_elem(0));
		Object right = visit(ctx.ineq_elem(1));
		return compareBoolExpr(left, right, (a, b) -> a <= b, (a, b) -> a <= b);
	}

	@Override
	public Object visitBoolExprGe(ExpressionParser.BoolExprGeContext ctx) {
		Object left = visit(ctx.ineq_elem(0));
		Object right = visit(ctx.ineq_elem(1));
		return compareBoolExpr(left, right, (a, b) -> a >= b, (a, b) -> a >= b);
	}

	@Override
	public Object visitEqElemFactor(ExpressionParser.EqElemFactorContext ctx) {
		return visit(ctx.factor());
	}

	@Override
	public Object visitEqElemNull(ExpressionParser.EqElemNullContext ctx) {
		return nullObject;
	}

	@Override
	public Object visitEqElemTrue(ExpressionParser.EqElemTrueContext ctx) {
		return true;
	}

	@Override
	public Object visitEqElemFalse(ExpressionParser.EqElemFalseContext ctx) {
		return false;
	}

	@Override
	public Object visitIneqElemFactor(ExpressionParser.IneqElemFactorContext ctx) {
		return visit(ctx.factor());
	}

	@Override
	public Object visitFactorLiteral(ExpressionParser.FactorLiteralContext ctx) {
		return visit(ctx.literal());
	}

	@Override
	public Object visitFactorPathRoot(ExpressionParser.FactorPathRootContext ctx) {
		JsonNode jsonNode = (JsonNode) visit(ctx.path_root());
		return switch (jsonNode.getNodeType()) {
			case JsonNodeType.NUMBER -> {
				if (jsonNode.isIntegralNumber()) yield jsonNode.asLong();
				if (jsonNode.isFloatingPointNumber()) yield jsonNode.asDouble();
				yield jsonNode;
			}
			case JsonNodeType.BOOLEAN -> jsonNode.asBoolean();
			case JsonNodeType.STRING -> jsonNode.asText();
			case JsonNodeType.NULL -> nullObject;
			case JsonNodeType.MISSING ->
					throw new ExpressionEvaluator.EvaluationException("JSON path leads to a missing node");
			default -> jsonNode;
		};
	}

	@Override
	public Object visitLiteralInt(ExpressionParser.LiteralIntContext ctx) {
		return Long.parseLong(ctx.INT().getText());
	}

	@Override
	public Object visitLiteralFloat(ExpressionParser.LiteralFloatContext ctx) {
		return Double.parseDouble(ctx.FLOAT().getText());
	}

	@Override
	public Object visitLiteralString(ExpressionParser.LiteralStringContext ctx) {
		String quoted = ctx.STRING().getText();
		return quoted.substring(1, quoted.length() - 1);
	}

	/**
	 * @param ctx the parse tree
	 * @return Final JsonNode of the path expression
	 */
	@Override
	public Object visitPathRootId(ExpressionParser.PathRootIdContext ctx) {
		currentJsonNode = rootJsonNode.path(ctx.ID().getText());
		currentJsonPath = new StringBuilder(ctx.ID().getText());
		if (currentJsonNode.isMissingNode()) {
			throw new ExpressionEvaluator.EvaluationException(
					String.format("cannot find the object \"%s\" in the provided JSON document", currentJsonPath));
		}
		log.debug("Path: {}, node: {}", currentJsonPath, currentJsonNode);
		return visit(ctx.path());
	}

	@Override
	public Object visitPathId(ExpressionParser.PathIdContext ctx) {
		currentJsonNode = currentJsonNode.path(ctx.ID().getText());
		currentJsonPath.append('.').append(ctx.ID().getText());
		if (currentJsonNode.isMissingNode()) {
			throw new ExpressionEvaluator.EvaluationException(
					String.format("cannot find the object \"%s\" in the provided JSON document", currentJsonPath));
		}
		log.debug("Path: {}, node: {}", currentJsonPath, currentJsonNode);
		return visit(ctx.path());
	}

	@Override
	public Object visitPathInd(ExpressionParser.PathIndContext ctx) {
		final var index = Integer.parseInt(ctx.INT().getText());
		currentJsonNode = currentJsonNode.path(index);
		currentJsonPath.append('[').append(index).append(']');
		if (currentJsonNode.isMissingNode()) {
			throw new ExpressionEvaluator.EvaluationException(
					String.format("cannot find the object \"%s\" in the provided JSON document", currentJsonPath));
		}
		log.debug("Path: {}, node: {}", currentJsonPath, currentJsonNode);
		return visit(ctx.path());
	}

	@Override
	public Object visitPathEmpty(ExpressionParser.PathEmptyContext ctx) {
		return currentJsonNode;
	}
}
