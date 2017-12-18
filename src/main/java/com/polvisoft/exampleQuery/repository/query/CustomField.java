package com.polvisoft.exampleQuery.repository.query;

import com.polvisoft.exampleQuery.enums.HqlFunctions;

public class CustomField {

	private HqlFunctions function;

	private String fieldName;

	public CustomField(final String fieldName) {
		this.fieldName = fieldName;
	}

	public CustomField(final String fieldName, final HqlFunctions function) {
		this.fieldName = fieldName;
		this.function = function;
	}

	public String getFieldName() {
		return this.fieldName;
	}

	public void setFieldName(final String fieldName) {
		this.fieldName = fieldName;
	}

	public HqlFunctions getFunction() {
		return this.function;
	}

	public void setFunction(final HqlFunctions function) {
		this.function = function;
	}

}
