package com.polvisoft.exampleQuery.repository.query;

import java.util.ArrayList;
import java.util.List;

public class CustomSelect {

	private boolean distinct;

	private List<CustomField> fields;

	public CustomSelect() {
		this.fields = new ArrayList<>();
		this.distinct = false;
	}

	public CustomSelect(final boolean distinct) {
		this.fields = new ArrayList<>();
		this.distinct = distinct;
	}

	public void addField(final String field) {
		final CustomField customField = new CustomField(field);
		this.fields.add(customField);
	}

	public void addField(final CustomField customField) {
		this.fields.add(customField);
	}

	public boolean isDistinct() {
		return this.distinct;
	}

	public void setDistinct(final boolean distinct) {
		this.distinct = distinct;
	}

	public List<CustomField> getFields() {
		return this.fields;
	}

	public void setFields(final List<CustomField> fields) {
		this.fields = fields;
	}

}
