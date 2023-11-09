package com.ivankatalenic.evaluator.mapper;

import com.ivankatalenic.evaluator.dao.ExpressionDao;
import com.ivankatalenic.evaluator.models.Expression;

public interface ExpressionMapper {
	ExpressionDao modelToDao(Expression expr) throws MappingException;
	Expression daoToModel(ExpressionDao expr) throws MappingException;

	enum MappingDirection {
		DAO_TO_MODEL, MODEL_TO_DAO
	}

	class MappingException extends RuntimeException {
		private final ExpressionMapper.MappingDirection mappingDirection;
		public MappingException(String cause, ExpressionMapper.MappingDirection mappingDirection) {
			super(cause);
			this.mappingDirection = mappingDirection;
		}
		public ExpressionMapper.MappingDirection getMappingDirection() {
			return mappingDirection;
		}
	}
}
