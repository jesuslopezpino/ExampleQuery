package foo.bar.annotations.readers;

import java.lang.reflect.Field;

import org.apache.commons.lang3.reflect.FieldUtils;

import foo.bar.annotations.Reference;

public class ReferenceReader {

	public static String getReferenceForField(String fieldName, Object object)
			throws NoSuchFieldException, SecurityException {
		String result = null;
		Field field = FieldUtils.getField(object.getClass(), fieldName, true);
		final Reference reference = field.getDeclaredAnnotation(Reference.class);
		result = reference.referenceFor();
		return result;
	}

	public static String getReferenceFieldName(String fieldName, Object object)
			throws NoSuchFieldException, SecurityException {
		String result = null;
		Field field = FieldUtils.getField(object.getClass(), fieldName, true);
		final Reference reference = field.getDeclaredAnnotation(Reference.class);
		result = reference.fieldName();
		return result;
	}

	public static boolean isReferenceField(String fieldName, Object object)
			throws NoSuchFieldException, SecurityException {
		boolean result = false;
		Field field = FieldUtils.getField(object.getClass(), fieldName, true);
		final Reference reference = field.getDeclaredAnnotation(Reference.class);
		result = reference != null;
		return result;
	}
}
