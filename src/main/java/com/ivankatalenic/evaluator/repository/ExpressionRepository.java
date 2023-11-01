package com.ivankatalenic.evaluator.repository;

import com.ivankatalenic.evaluator.models.Expression;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpressionRepository extends JpaRepository<Expression, Long> {
}
