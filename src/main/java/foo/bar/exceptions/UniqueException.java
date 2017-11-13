package foo.bar.exceptions;

import java.lang.reflect.Field;

import foo.bar.annotations.Unique;

public class UniqueException extends Exception {

	/**
	 * Serializable
	 */
	private static final long serialVersionUID = 2684525322828162497L;

	private Class clazz;

	private String uk;

	public UniqueException(Class clazz, String uk) {
		super();
		this.clazz = clazz;
		this.uk = uk;
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

	public String getUniqueMessage() {
		String result = "";
		boolean found = false;
		for (Field field : this.clazz.getDeclaredFields()) {
			if (field.isAnnotationPresent(Unique.class)) {
				Unique annotationValue = field.getAnnotation(Unique.class);
				if (annotationValue.uk().equals(this.uk)) {
					found = true;
					result = annotationValue.message();
					break;
				}
			}
		}
		if (!found) {
			result = "Unexpected Unique Annotation \"" + this.uk + "\" not found in class: " + this.clazz.getName();
		}
		return result;
	}

	public String[] getAffectedFields() {
		String[] result = {};
		for (Field field : this.clazz.getDeclaredFields()) {
			if (field.isAnnotationPresent(Unique.class)) {
				Unique annotationValue = field.getAnnotation(Unique.class);
				if (annotationValue.uk().equals(this.uk)) {
					result[result.length] = field.getName();
					break;
				}
			}
		}
		return result;
	}
}
