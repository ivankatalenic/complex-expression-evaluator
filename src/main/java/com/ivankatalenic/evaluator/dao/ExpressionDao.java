package com.ivankatalenic.evaluator.dao;

import com.ivankatalenic.evaluator.validator.CheckSyntax;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public class ExpressionDao {
	private Long id;
	@NotNull
	private String name;
	@NotNull
	@NotEmpty
	@CheckSyntax
	private String value;

	public ExpressionDao(Long id, String name, String value) {
		this.id = id;
		this.name = name;
		this.value = value;
	}

	public ExpressionDao(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public ExpressionDao() {

	}

	public Long getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getValue() {
		return this.value;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ExpressionDao other)) {
			return false;
		}
		return Objects.equals(this.id, other.id) && Objects.equals(this.name, other.name) && Objects.equals(this.value,
		                                                                                                    other.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.name, this.value);
	}

	@Override
	public String toString() {
		return String.format("Expression{id=%d, name=%s, value=%s}", this.id, this.name, this.value);
	}
}
