package com.polvisoft.exampleQuery.exceptions;

import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.polvisoft.exampleQuery.domain.BasicDTO;

public class UniqueException extends Exception {

	/**
	 * Serializable
	 */
	private static final long serialVersionUID = 2684525322828162497L;

	private Class clazz;

	private String uk;

	private BasicDTO entity;

	private Table table;

	public UniqueException(final Class clazz, final String uk, final BasicDTO entity) {
		super();
		this.clazz = clazz;
		this.entity = entity;
		this.uk = uk;
	}

	public BasicDTO getEntity() {
		return this.entity;
	}

	public void setEntity(final BasicDTO entity) {
		this.entity = entity;
	}

	public Class getClazz() {
		return this.clazz;
	}

	public void setClazz(final Class clazz) {
		this.clazz = clazz;
	}

	public String getUk() {
		return this.uk;
	}

	public void setUk(final String uk) {
		this.uk = uk;
	}

	public UniqueConstraint getUniqueConstraint() {
		UniqueConstraint result = null;
		final boolean found = false;
		this.table = (Table) this.clazz.getAnnotation(Table.class);
		if (this.table != null && this.table.uniqueConstraints().length > 0) {
			for (int i = 0; i < this.table.uniqueConstraints().length; i++) {
				final UniqueConstraint uniqueConstraint = this.table.uniqueConstraints()[i];
				if (uniqueConstraint.name().equals(this.uk)) {
					result = uniqueConstraint;
					break;
				}
			}
		}
		return result;
	}

}
