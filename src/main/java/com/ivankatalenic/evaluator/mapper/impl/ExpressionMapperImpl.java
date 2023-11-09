package com.ivankatalenic.evaluator.mapper.impl;

import com.ivankatalenic.evaluator.dao.ExpressionDao;
import com.ivankatalenic.evaluator.mapper.ExpressionMapper;
import com.ivankatalenic.evaluator.models.Expression;
import org.springframework.stereotype.Component;

@Component
public class ExpressionMapperImpl implements ExpressionMapper {
	public ExpressionMapperImpl() {}

	@Override
	public ExpressionDao modelToDao(Expression expr) {
		return new ExpressionDao(expr.getId(), expr.getName(), expr.getValue());
	}

	@Override
	public Expression daoToModel(ExpressionDao expr) {
		return new Expression(expr.getId(), expr.getName(), expr.getValue());
	}

}
