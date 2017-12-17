package com.polvisoft.exampleQuery.service.query;

import java.util.ArrayList;
import java.util.List;

public class CustomSelect {

	private boolean distinct;

	private List<CustomField> fields;

	public CustomSelect() {
		this.fields = new ArrayList<>();
		this.distinct = false;
	}

	public CustomSelect(boolean distinct) {
		this.fields = new ArrayList<>();
		this.distinct = distinct;
	}

	public void addField(String field) {
		CustomField customField = new CustomField(field);
		this.fields.add(customField);
	}

	public void addField(CustomField customField) {
		this.fields.add(customField);
	}

	public boolean isDistinct() {
		return this.distinct;
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	public List<CustomField> getFields() {
		return this.fields;
	}

	public void setFields(List<CustomField> fields) {
		this.fields = fields;
	}

}
