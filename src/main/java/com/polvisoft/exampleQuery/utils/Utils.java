package com.polvisoft.exampleQuery.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.log4j.Logger;

import com.polvisoft.exampleQuery.exceptions.ExampleQueryException;

public class Utils {

	private static Logger LOGGER = Logger.getLogger(Utils.class);

	public static <T> Object getFieldValue(final T object, final String fieldName) throws IllegalAccessException,
	IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		return getFieldValue(object, fieldName, false);
	}

	public static <T> Object getFieldValue(final T object, final String fieldName, final boolean nullIfNoPath)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException {
		final Object finalObject = getFinalInvokeClass(fieldName, object);
		if (nullIfNoPath && finalObject == null) {
			return null;
		}
		final String lastField = getLastField(fieldName);
		return invokeGetter(lastField, finalObject);
	}

	public static String getGetterOfField(final String field) {
		final String methodName = "get" + field.substring(0, 1).toUpperCase() + field.substring(1);
		return methodName;
	}

	public static String getSetterOfField(final String field) {
		final String methodName = "set" + field.substring(0, 1).toUpperCase() + field.substring(1);
		return methodName;
	}

	public static void setFieldValue(final String fieldName, final Object value, final Object objectClass)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchFieldException, InstantiationException {
		final String[] fields = fieldName.split("\\.");
		String lastField = null;
		boolean loop = false;
		Object lastValue = null;
		if (fields.length == 1) {
			lastField = fieldName;
			lastValue = value;
		} else {
			loop = true;
			lastField = fields[fields.length - 1];
			LOGGER.debug("loop mode");
		}
		Object lastClass = objectClass;
		for (int i = 0; loop && i < fields.length; i++) {
			LOGGER.debug("loop " + i);
			if (i == fields.length - 1) {
				LOGGER.debug("exit loop at " + i);
				break;
			}
			final String getter = fields[i];
			LOGGER.debug("searching getter: " + getter);
			lastValue = getFieldValue(lastClass, getter);
			LOGGER.debug("lastValue: " + lastValue);
			if (lastValue == null) {
				LOGGER.debug("lastClass " + lastClass.getClass().getName());
				final Class<? extends Object> fieldType = FieldUtils.getField(lastClass.getClass(), getter, true).getType();
				lastValue = fieldType.newInstance();
				LOGGER.debug("NEW LAST VALUE: " + lastValue);
				invokeSetter(getter, lastClass, lastValue);
			}
			lastClass = lastValue;
		}
		invokeSetter(lastField, lastClass, value);
	}

	public static void invokeSetter(final String fieldName, final Object objectClass, final Object value)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchFieldException {
		final String setterName = getSetterOfField(fieldName);
		// Dates... Database will return Timestamp that we usually implements
		// with java.util.Date, so we have to check it and fix it
		if (isTimestamp(value)) {
			final Date dateValue = timestampToDate((Timestamp) value);
			final Class[] argsTypes = { Date.class };
			final Object[] argsValues = { dateValue };
			MethodUtils.invokeMethod(objectClass, true, setterName, argsValues, argsTypes);
		} else {
			final Class[] argsTypes = { value.getClass() };
			final Object[] argsValues = { value };
			MethodUtils.invokeMethod(objectClass, true, setterName, argsValues, argsTypes);
		}
	}

	private static Date timestampToDate(final Timestamp value) {
		return new Date(value.getTime());
	}

	private static boolean isTimestamp(final Object value) {
		return value instanceof Timestamp;
	}

	public static Object invokeGetter(final String fieldName, final Object objectClass) throws IllegalAccessException,
	IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		final String getterName = getGetterOfField(fieldName);
		LOGGER.debug("invokeGetter getterName \"" + getterName + "");
		return MethodUtils.invokeMethod(objectClass, true, getterName);
	}

	public static final String TIME_FORMAT = "DD/MM/YYYY HH:mm:SS";

	public static final String DATE_FORMAT = "DD/MM/YYYY";

	public static Date getDate(final String date) throws ExampleQueryException {
		return getDate(date, DATE_FORMAT);
	}

	public static Date getDateTime(final String date) throws ExampleQueryException {
		return getDate(date, TIME_FORMAT);
	}

	public static Date getDate(final String date, final String format) throws ExampleQueryException {
		// TODO: improve to don't make new always
		final SimpleDateFormat formatter = new SimpleDateFormat(format);
		Date result = null;
		try {
			result = formatter.parse(date);
		} catch (final ParseException e) {
			throw new ExampleQueryException(e);
		}
		return result;
	}

	public static Field getFinalField(final String fieldName, final Object object)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		return getFinalField(fieldName, object, false);
	}

	public static Field getFinalField(final String fieldName, final Object object, final boolean nullIfNoPath)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		final Object finalObject = getFinalInvokeClass(fieldName, object);
		final String lastField = getLastField(fieldName);
		if (nullIfNoPath && finalObject == null) {
			return null;
		}
		final Field field = FieldUtils.getField(finalObject.getClass(), lastField, true);
		return field;
	}

	private static String getLastField(final String fieldName) {
		if (fieldName.indexOf(".") > -1) {
			return fieldName.substring(fieldName.lastIndexOf(".") + 1);
		} else {
			return fieldName;
		}
	}

	protected static Object getFinalInvokeClass(final String fieldName, final Object object)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		final String[] fields = fieldName.split("\\.");
		Object finalObject = object;
		for (int i = 0; i < fields.length - 1; i++) {
			final String field = fields[i];
			finalObject = invokeGetter(field, finalObject);
		}
		return finalObject;
	}

	// public static boolean hasAnnotation(String fieldName, Class objectClass,
	// Class<? extends Annotation> annotation)
	// throws NoSuchFieldException, SecurityException {
	// boolean result = false;
	// Field field;
	// field = objectClass.getDeclaredField(fieldName);
	// result = field.isAnnotationPresent(annotation);
	// return result;
	// }

}
