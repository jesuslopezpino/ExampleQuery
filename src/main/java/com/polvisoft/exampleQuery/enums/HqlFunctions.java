package com.polvisoft.exampleQuery.enums;

public enum HqlFunctions {
	AVG("avg"),
	SUM("sum"),
	COUNT("count"),
	MAX("max"),
	MIN("min");

	private final String function;

	@Override
	public String toString() {
		return this.function;
	}

	HqlFunctions(final String function) {
		this.function = function;
	}

	public boolean equalsName(final String otherName) {
		return this.function.equals(otherName);
	}
}
