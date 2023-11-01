package com.ivankatalenic.evaluator.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.ivankatalenic.evaluator.grammar.ExpressionBaseVisitor;
import com.ivankatalenic.evaluator.grammar.ExpressionParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExpressionSyntaxTreeVisitor extends ExpressionBaseVisitor<Object> {
	private final Log log = LogFactory.getLog(getClass());
	private final Object nullObject = new Object();
	private final JsonNode root;
	private JsonNode current;

	public ExpressionSyntaxTreeVisitor(final JsonNode root) {
		this.root = root;
		this.log.debug("created expression evaluator visitor");
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
		switch (left) {
			case Boolean ignored -> {
				return false;
			}
			case Integer leftInt -> {
				if (right instanceof Integer rightInt) {
					return leftInt < rightInt;
				}
				throw new RuntimeException(String.format("cannot compare an integer with a non-integer, that is %s", right.getClass()));
			}
			case Double leftDouble -> {
				if (right instanceof Double rightDouble) {
					return leftDouble < rightDouble;
				}
				throw new RuntimeException(String.format("cannot compare a float with a non-float, that is %s", right.getClass()));
			}
			case String ignored -> {
				return false;
			}
			default -> throw new RuntimeException("unknown boolean inequality expression's left operand");
		}
	}
	@Override
	public Object visitBoolExprGt(ExpressionParser.BoolExprGtContext ctx) {
		Object left = visit(ctx.ineq_elem(0));
		Object right = visit(ctx.ineq_elem(1));
		switch (left) {
			case Boolean ignored -> {
				return false;
			}
			case Integer leftInt -> {
				if (right instanceof Integer rightInt) {
					return leftInt > rightInt;
				}
				throw new RuntimeException(String.format("cannot compare an integer with a non-integer, that is %s", right.getClass()));
			}
			case Double leftDouble -> {
				if (right instanceof Double rightDouble) {
					return leftDouble > rightDouble;
				}
				throw new RuntimeException(String.format("cannot compare a float with a non-float, that is %s", right.getClass()));
			}
			case String ignored -> {
				return false;
			}
			default -> throw new RuntimeException("unknown boolean inequality expression's left operand");
		}
	}
	@Override
	public Object visitBoolExprLe(ExpressionParser.BoolExprLeContext ctx) {
		Object left = visit(ctx.ineq_elem(0));
		Object right = visit(ctx.ineq_elem(1));
		switch (left) {
			case Boolean ignored -> {
				return false;
			}
			case Integer leftInt -> {
				if (right instanceof Integer rightInt) {
					return leftInt <= rightInt;
				}
				throw new RuntimeException(String.format("cannot compare an integer with a non-integer, that is %s", right.getClass()));
			}
			case Double leftDouble -> {
				if (right instanceof Double rightDouble) {
					return leftDouble <= rightDouble;
				}
				throw new RuntimeException(String.format("cannot compare a float with a non-float, that is %s", right.getClass()));
			}
			case String ignored -> {
				return false;
			}
			default -> throw new RuntimeException("unknown boolean inequality expression's left operand");
		}
	}
	@Override
	public Object visitBoolExprGe(ExpressionParser.BoolExprGeContext ctx) {
		Object left = visit(ctx.ineq_elem(0));
		Object right = visit(ctx.ineq_elem(1));
		switch (left) {
			case Boolean ignored -> {
				return false;
			}
			case Integer leftInt -> {
				if (right instanceof Integer rightInt) {
					return leftInt >= rightInt;
				}
				throw new RuntimeException(String.format("cannot compare an integer with a non-integer, that is %s", right.getClass()));
			}
			case Double leftDouble -> {
				if (right instanceof Double rightDouble) {
					return leftDouble >= rightDouble;
				}
				throw new RuntimeException(String.format("cannot compare a float with a non-float, that is %s", right.getClass()));
			}
			case String ignored -> {
				return false;
			}
			default -> throw new RuntimeException("unknown boolean inequality expression's left operand");
		}
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
		switch (jsonNode.getNodeType()) {
			case JsonNodeType.BOOLEAN -> { return jsonNode.asBoolean(); }
			case JsonNodeType.MISSING -> throw new RuntimeException("JSON path leads to nothing");
			case JsonNodeType.NULL -> { return nullObject; }
			case JsonNodeType.NUMBER -> {
				if (jsonNode.isIntegralNumber()) {
					return jsonNode.asInt();
				}
				if (jsonNode.isFloatingPointNumber()) {
					return jsonNode.asDouble();
				}
				return jsonNode;
			}
			case JsonNodeType.STRING -> { return jsonNode.asText(); }
			default -> { return jsonNode; }
		}
	}
	@Override
	public Object visitLiteralInt(ExpressionParser.LiteralIntContext ctx) {
		return Integer.parseInt(ctx.INT().getText());
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
	 * @return Final JsonNode of the entire expression
	 */
	@Override
	public Object visitPathRootId(ExpressionParser.PathRootIdContext ctx) {
		current = root.path(ctx.ID().getText());
		if (current.isMissingNode()) {
			throw new RuntimeException("cannot find the node in the provided JSON document");
		}
		log.debug(String.format("ID: %s, node: %s\n", ctx.ID().getText(), current));
		return visit(ctx.path());
	}
	@Override
	public Object visitPathId(ExpressionParser.PathIdContext ctx) {
		current = current.path(ctx.ID().getText());
		if (current.isMissingNode()) {
			throw new RuntimeException("cannot find the node in the provided JSON document");
		}
		log.debug(String.format("ID: %s, node: %s\n", ctx.ID().getText(), current));
		return visit(ctx.path());
	}
	@Override
	public Object visitPathInd(ExpressionParser.PathIndContext ctx) {
		current = current.path(Integer.parseInt(ctx.UINT().getText()));
		if (current.isMissingNode()) {
			throw new RuntimeException("cannot find the node in the provided JSON document");
		}
		return visit(ctx.path());
	}
	@Override
	public Object visitPathEmpty(ExpressionParser.PathEmptyContext ctx) {
		return current;
	}
}
