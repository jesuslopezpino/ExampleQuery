package foo.bar.exceptions;

import java.lang.reflect.Field;

import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import foo.bar.domain.BasicVO;

public class UniqueException extends Exception {

	/**
	 * Serializable
	 */
	private static final long serialVersionUID = 2684525322828162497L;

	private Class clazz;

	private String uk;

	private BasicVO entity;

	private Table table;

	public UniqueException(Class clazz, String uk, BasicVO entity) {
		super();
		this.clazz = clazz;
		this.entity = entity;
		this.uk = uk;
	}

	public BasicVO getEntity() {
		return entity;
	}

	public void setEntity(BasicVO entity) {
		this.entity = entity;
	}

	public Class getClazz() {
		return clazz;
	}

	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}

	public String getUk() {
		return uk;
	}

	public void setUk(String uk) {
		this.uk = uk;
	}

	public UniqueConstraint getUniqueConstraint() {
		UniqueConstraint result = null;
		boolean found = false;
		table = (Table) this.clazz.getAnnotation(Table.class);
		if (table != null && table.uniqueConstraints().length > 0) {
			for (int i = 0; i < table.uniqueConstraints().length; i++) {
				UniqueConstraint uniqueConstraint = table.uniqueConstraints()[i];
				if (uniqueConstraint.name().equals(this.uk)) {
					result = uniqueConstraint;
					break;
				}
			}
		}
		return result;
	}

}
