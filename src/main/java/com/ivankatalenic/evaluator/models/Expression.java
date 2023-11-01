package com.ivankatalenic.evaluator.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.Objects;

/**
 * A complex expression.
 */
@Entity
public class Expression {
	@Id
	@GeneratedValue
	private Long id;
	private String name;
	private String value;

	Expression(Long id, String name, String value) {
		this.id = id;
		this.name = name;
		this.value = value;
	}

	Expression(String name, String value) {
		this.name = name;
		this.value = value;
	}

	Expression() {

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
		if (!(o instanceof Expression other)) {
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
