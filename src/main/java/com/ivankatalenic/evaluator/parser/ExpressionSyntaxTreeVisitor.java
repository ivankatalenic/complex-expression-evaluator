package com.ivankatalenic.evaluator.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.ivankatalenic.evaluator.exceptions.EvaluationException;
import com.ivankatalenic.evaluator.grammar.ExpressionBaseVisitor;
import com.ivankatalenic.evaluator.grammar.ExpressionParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A syntax tree visitor that computes the final boolean value of a parsed expression using the given JSON document.
 */
public class ExpressionSyntaxTreeVisitor extends ExpressionBaseVisitor<Object> {
	private final Log log = LogFactory.getLog(getClass());
	private final Object nullObject = new Object();
	private final JsonNode root;
	private JsonNode current;
	private StringBuilder currentJsonPath;

	public ExpressionSyntaxTreeVisitor(final JsonNode root) {
		this.root = root;
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
		return switch (left) {
			case Boolean ignored -> false;
			case Long leftInt -> {
				if (right instanceof Long rightInt) {
					yield leftInt < rightInt;
				}
				throw new EvaluationException(
						String.format("cannot compare an integer with a non-integer, that is %s", right.getClass()));
			}
			case Double leftDouble -> {
				if (right instanceof Double rightDouble) {
					yield leftDouble < rightDouble;
				}
				throw new EvaluationException(
						String.format("cannot compare a float with a non-float, that is %s", right.getClass()));
			}
			case String ignored -> false;
			default -> throw new EvaluationException("unknown boolean comparison expression's left operand");
		};
	}

	@Override
	public Object visitBoolExprGt(ExpressionParser.BoolExprGtContext ctx) {
		Object left = visit(ctx.ineq_elem(0));
		Object right = visit(ctx.ineq_elem(1));
		return switch (left) {
			case Boolean ignored -> false;
			case Long leftLong -> {
				if (right instanceof Long rightLong) {
					yield leftLong > rightLong;
				}
				throw new EvaluationException(
						String.format("cannot compare an integer with a non-integer, that is %s", right.getClass()));
			}
			case Double leftDouble -> {
				if (right instanceof Double rightDouble) {
					yield leftDouble > rightDouble;
				}
				throw new EvaluationException(
						String.format("cannot compare a float with a non-float, that is %s", right.getClass()));
			}
			case String ignored -> false;
			default -> throw new EvaluationException("unknown boolean comparison expression's left operand");
		};
	}

	@Override
	public Object visitBoolExprLe(ExpressionParser.BoolExprLeContext ctx) {
		Object left = visit(ctx.ineq_elem(0));
		Object right = visit(ctx.ineq_elem(1));
		return switch (left) {
			case Boolean ignored -> false;
			case Long leftLong -> {
				if (right instanceof Long rightLong) {
					yield leftLong <= rightLong;
				}
				throw new EvaluationException(
						String.format("cannot compare an integer with a non-integer, that is %s", right.getClass()));
			}
			case Double leftDouble -> {
				if (right instanceof Double rightDouble) {
					yield leftDouble <= rightDouble;
				}
				throw new EvaluationException(
						String.format("cannot compare a float with a non-float, that is %s", right.getClass()));
			}
			case String ignored -> false;
			default -> throw new EvaluationException("unknown boolean comparison expression's left operand");
		};
	}

	@Override
	public Object visitBoolExprGe(ExpressionParser.BoolExprGeContext ctx) {
		Object left = visit(ctx.ineq_elem(0));
		Object right = visit(ctx.ineq_elem(1));
		return switch (left) {
			case Boolean ignored -> false;
			case Long leftLong -> {
				if (right instanceof Long rightLong) {
					yield leftLong >= rightLong;
				}
				throw new EvaluationException(
						String.format("cannot compare an integer with a non-integer, that is %s", right.getClass()));
			}
			case Double leftDouble -> {
				if (right instanceof Double rightDouble) {
					yield leftDouble >= rightDouble;
				}
				throw new EvaluationException(
						String.format("cannot compare a float with a non-float, that is %s", right.getClass()));
			}
			case String ignored -> false;
			default -> throw new EvaluationException("unknown boolean comparison expression's left operand");
		};
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
			case JsonNodeType.BOOLEAN -> jsonNode.asBoolean();
			case JsonNodeType.MISSING -> throw new EvaluationException("JSON path leads to a missing node");
			case JsonNodeType.NULL -> nullObject;
			case JsonNodeType.NUMBER -> {
				if (jsonNode.isIntegralNumber()) yield jsonNode.asLong();
				if (jsonNode.isFloatingPointNumber()) yield jsonNode.asDouble();
				yield jsonNode;
			}
			case JsonNodeType.STRING -> jsonNode.asText();
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
		current = root.path(ctx.ID().getText());
		currentJsonPath = new StringBuilder(ctx.ID().getText());
		if (current.isMissingNode()) {
			throw new EvaluationException(
					String.format("cannot find the object \"%s\" in the provided JSON document", currentJsonPath));
		}
		log.debug(String.format("Path: %s, node: %s\n", currentJsonPath, current));
		return visit(ctx.path());
	}

	@Override
	public Object visitPathId(ExpressionParser.PathIdContext ctx) {
		current = current.path(ctx.ID().getText());
		currentJsonPath.append('.').append(ctx.ID().getText());
		if (current.isMissingNode()) {
			throw new EvaluationException(
					String.format("cannot find the object \"%s\" in the provided JSON document", currentJsonPath));
		}
		log.debug(String.format("Path: %s, node: %s\n", currentJsonPath, current));
		return visit(ctx.path());
	}

	@Override
	public Object visitPathInd(ExpressionParser.PathIndContext ctx) {
		final var index = Integer.parseInt(ctx.INT().getText());
		current = current.path(index);
		currentJsonPath.append('[').append(index).append(']');
		if (current.isMissingNode()) {
			throw new EvaluationException(
					String.format("cannot find the object \"%s\" in the provided JSON document", currentJsonPath));
		}
		return visit(ctx.path());
	}

	@Override
	public Object visitPathEmpty(ExpressionParser.PathEmptyContext ctx) {
		return current;
	}
}
