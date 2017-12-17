package com.polvisoft.exampleQuery.service.query;

public class CustomField {

	private HqlFunctions function;

	private String fieldName;

	public CustomField(String fieldName) {
		this.fieldName = fieldName;
	}

	public CustomField(String fieldName, HqlFunctions function) {
		this.fieldName = fieldName;
		this.function = function;
	}

	public String getFieldName() {
		return this.fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public HqlFunctions getFunction() {
		return this.function;
	}

	public void setFunction(HqlFunctions function) {
		this.function = function;
	}

}
