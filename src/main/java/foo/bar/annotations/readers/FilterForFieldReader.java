package foo.bar.annotations.readers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.reflect.FieldUtils;

import foo.bar.annotations.FilterForField;
import foo.bar.utils.Utils;

public class FilterForFieldReader {

	public static String getValue(String fieldName, Object object)
			throws NoSuchFieldException, SecurityException {
		String result = null;
		Field field = FieldUtils.getField(object.getClass(), fieldName, true);
		final FilterForField filterForField = field.getDeclaredAnnotation(FilterForField.class);
		result = filterForField.value();
		return result;
	}

	public static boolean isAnnotatedField(String fieldName, Object object) throws NoSuchFieldException,
			SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		boolean result = false;
		Field field = Utils.getFinalField(fieldName, object, true);
		if(field != null){
			final FilterForField filterForField = field.getDeclaredAnnotation(FilterForField.class);
			result = filterForField != null;
		}
		return result;
	}
}
