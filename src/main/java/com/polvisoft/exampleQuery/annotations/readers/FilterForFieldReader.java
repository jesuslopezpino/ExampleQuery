package com.polvisoft.exampleQuery.annotations.readers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.polvisoft.exampleQuery.annotations.FilterForField;
import com.polvisoft.exampleQuery.utils.Utils;

public class FilterForFieldReader {

	public static String getValue(final String fieldName, final Object object)
			throws NoSuchFieldException, SecurityException {
		String result = null;
		final Field field = FieldUtils.getField(object.getClass(), fieldName, true);
		final FilterForField filterForField = field.getDeclaredAnnotation(FilterForField.class);
		result = filterForField.value();
		return result;
	}

	public static boolean isAnnotatedField(final String fieldName, final Object object) throws NoSuchFieldException,
	SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		boolean result = false;
		final Field field = Utils.getFinalField(fieldName, object, true);
		if (field != null) {
			final FilterForField filterForField = field.getDeclaredAnnotation(FilterForField.class);
			result = filterForField != null;
		}
		return result;
	}

}
