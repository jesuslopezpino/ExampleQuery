package foo.bar.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import org.apache.log4j.Logger;

import foo.bar.annotations.readers.ReferenceReader;

public class Utils {

	private static Logger LOGGER = Logger.getLogger(Utils.class);

	public static <T> Object getFieldValue(T object, String fieldName) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		final String[] fieldSplit = fieldName.split("\\.");
		final List<String> fields = new ArrayList<>();
		if (fieldSplit.length == 0) {
			fields.add(fieldName);
		} else {
			for (int i = 0; i < fieldSplit.length; i++) {
				final String string = fieldSplit[i];
				fields.add(string);
			}
		}

		Object invokedValue = null;
		for (int i = 0; i < fields.size(); i++) {
			final String methodName = fields.get(i);
			LOGGER.debug("methodName: " + methodName);
			if (i == fields.size() - 1) {
				// TODO: check at superclasses for the field
				invokedValue = invokeGetter(methodName, object);
			} else {
				invokedValue = invokeGetter(methodName, object);
				return getFieldValue(invokedValue, fieldName.replaceAll(methodName + ".", ""));

			}

		}
		return invokedValue;
	}

	public static boolean isDateField(String fieldName, Object object) throws NoSuchFieldException {
		return object.getClass().getDeclaredField(fieldName).getType().equals(Date.class);
	}

	public static boolean isListField(String fieldName, Object object) {
		boolean result = false;
		try {
			result = object.getClass().getDeclaredField(fieldName).getType().equals(List.class);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public static String getGetterOfField(String field) {
		final String methodName = "get" + field.substring(0, 1).toUpperCase() + field.substring(1);
		return methodName;
	}

	public static String getSetterOfField(String field) {
		final String methodName = "set" + field.substring(0, 1).toUpperCase() + field.substring(1);
		return methodName;
	}

	public static void setFieldValue(String fieldName, Object value, Object objectClass)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchFieldException, InstantiationException {
		String[] fields = fieldName.split("\\.");
		String lastField = null;
		boolean loop = false;
		Object lastValue = null;
		if (fields.length == 1) {
			lastField = fieldName;
			lastValue = value;
		} else {
			loop = true;
			lastField = fields[fields.length - 1];
			LOGGER.info("loop mode");
		}
		Object lastClass = objectClass;
		for (int i = 0; loop && i < fields.length; i++) {
			LOGGER.info("loop " + i);
			if (i == fields.length - 1) {
				LOGGER.info("exit loop at " + i);
				break;
			}
			String getter = fields[i];
			LOGGER.info("searching getter: " + getter);
			lastValue = getFieldValue(lastClass, getter);
			LOGGER.info("lastValue: " + lastValue);
			if (lastValue == null) {
				LOGGER.info("lastClass " + lastClass.getClass().getName());
				Class<? extends Object> fieldType = lastClass.getClass().getDeclaredField(getter).getType();
				lastValue = fieldType.newInstance();
				LOGGER.info("NEW LAST VALUE: " + lastValue);
				invokeSetter(getter, lastClass, lastValue);
			}
			lastClass = lastValue;
		}
		invokeSetter(lastField, lastClass, value);
	}

	public static void invokeSetter(String fieldName, Object objectClass, Object value) throws NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String setterName = getSetterOfField(fieldName);
		// Dates... Database will return Timestamp that we usually implements
		// with java.util.Date, so we have to check it and fix it
		Method setter = null;
		if (isTimestamp(value)) {
			Date dateValue = timestampToDate((Timestamp) value);
			setter = objectClass.getClass().getMethod(setterName, Date.class);
			setter.invoke(objectClass, dateValue);
		} else {
			setter = objectClass.getClass().getMethod(setterName, value.getClass());
			setter.invoke(objectClass, value);
		}
	}

	private static Date timestampToDate(Timestamp value) {
		return new Date(value.getTime());
	}

	private static boolean isTimestamp(Object value) {
		return value instanceof Timestamp;
	}

	public static Object invokeGetter(String fieldName, Object objectClass) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		String getterName = getGetterOfField(fieldName);
		LOGGER.debug("invokeGetter getterName \"" + getterName + "");
		Object invokedValue = objectClass.getClass().getMethod(getterName).invoke(objectClass, (Object[]) null);
		return invokedValue;

	}

	public static Date getDate(String date, String format) {
		// TODO: improve to don't make new always
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		Date result = null;
		try {
			result = formatter.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public static boolean isTransientField(String fieldName, Object object)
			throws NoSuchFieldException, SecurityException {
		return hasAnnotation(fieldName, object, Transient.class);
	}

	public static boolean hasAnnotation(String fieldName, Object object, Class<? extends Annotation> annotation)
			throws NoSuchFieldException, SecurityException {
		boolean result = false;
		Field field;
		field = object.getClass().getDeclaredField(fieldName);
		result = field.isAnnotationPresent(annotation);
		return result;
	}

	public static String getReferencedField(Object object, String fieldName) {
		return ReferenceReader.getReferenceForField(fieldName, object);
	}

}
