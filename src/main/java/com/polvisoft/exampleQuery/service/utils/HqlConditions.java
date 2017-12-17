package com.polvisoft.exampleQuery.service.utils;

public enum HqlConditions {

	IS_NULL(" is null "), 
	IS_NOT_NULL(" is not null "), 
	IS_EMPTY(" is empty "), 
	IS_NOT_EMPTY(" is not empty "),
	LIKE(" like "), 
	LIKE_IGNORE_CASE(" LIKE "), 
	EQUALS(" = "), 
	NOT_EQUALS(" != "), 
	GREATER_THAN(" > "), 
	GREATER_EQUALS(" >= "), 
	LOWER_THAN(" < "), 
	LOWER_EQUALS(" <= "),
	IN(" in "), 
	NOT_IN(" not in ");

	private final String condition;

	@Override
	public String toString() {
		return this.condition;
	}

	HqlConditions(final String condition) {
		this.condition = condition;
	}
	
	public boolean equalsName(String otherName) {
        // (otherName == null) check is not needed because name.equals(null) returns false 
        return this.condition.equals(otherName);
    }
}
