package com.polvisoft.service.query;

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

	public boolean equalsName(String otherName) {
		return this.function.equals(otherName);
	}
}
