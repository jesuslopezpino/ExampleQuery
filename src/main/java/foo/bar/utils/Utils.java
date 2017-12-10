package foo.bar.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.log4j.Logger;

import foo.bar.domain.BasicVO;

public class Utils {

	private static Logger LOGGER = Logger.getLogger(Utils.class);

	public static <T> Object getFieldValue(T object, String fieldName) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		return getFieldValue(object, fieldName, false);
	}

	public static <T> Object getFieldValue(T object, String fieldName, boolean nullIfNoPath)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException {
		Object finalObject = getFinalInvokeClass(fieldName, object);
		if (nullIfNoPath && finalObject == null) {
			return null;
		}
		String lastField = getLastField(fieldName);
		return invokeGetter(lastField, finalObject);
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
			LOGGER.debug("loop mode");
		}
		Object lastClass = objectClass;
		for (int i = 0; loop && i < fields.length; i++) {
			LOGGER.debug("loop " + i);
			if (i == fields.length - 1) {
				LOGGER.debug("exit loop at " + i);
				break;
			}
			String getter = fields[i];
			LOGGER.debug("searching getter: " + getter);
			lastValue = getFieldValue(lastClass, getter);
			LOGGER.debug("lastValue: " + lastValue);
			if (lastValue == null) {
				LOGGER.debug("lastClass " + lastClass.getClass().getName());
				Class<? extends Object> fieldType = lastClass.getClass().getDeclaredField(getter).getType();
				lastValue = fieldType.newInstance();
				LOGGER.debug("NEW LAST VALUE: " + lastValue);
				invokeSetter(getter, lastClass, lastValue);
			}
			lastClass = lastValue;
		}
		invokeSetter(lastField, lastClass, value);
	}

	public static void invokeSetter(String fieldName, Object objectClass, Object value)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchFieldException {
		String setterName = getSetterOfField(fieldName);
		Method setter = null;
		// Dates... Database will return Timestamp that we usually implements
		// with java.util.Date, so we have to check it and fix it
		if (isTimestamp(value)) {
			Date dateValue = timestampToDate((Timestamp) value);
			setter = objectClass.getClass().getMethod(setterName, Date.class);
			setter.invoke(objectClass, dateValue);
		} else {
			if (isClassField(fieldName, objectClass)) {
				setter = objectClass.getClass().getMethod(setterName, value.getClass());
				setter.invoke(objectClass, value);
			} else {
				// TODO: we are only covering one herence...
				if (!isPkField(fieldName)) {
					setter = objectClass.getClass().getSuperclass().getMethod(setterName, value.getClass());
				} else {
					// Maybe I can use that case allways...?
					setter = objectClass.getClass().getSuperclass().getMethod(setterName, Object.class);
				}
				setter.invoke(objectClass, value);

			}
		}
	}

	private static boolean isPkField(String fieldName) {
		return fieldName.equals(BasicVO.PK);
	}

	private static boolean isClassField(String fieldName, Object objectClass) {
		try {
			objectClass.getClass().getDeclaredField(fieldName);
			return true;
		} catch (NoSuchFieldException | SecurityException e) {
			return false;
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

	public static final String TIME_FORMAT = "DD/MM/YYYY HH:mm:SS";

	public static final String DATE_FORMAT = "DD/MM/YYYY";

	public static Date getDate(String date) {
		return getDate(date, DATE_FORMAT);
	}

	public static Date getDateTime(String date) {
		return getDate(date, TIME_FORMAT);
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

	// public static boolean isTransientField(String fieldName, Object object)
	// throws NoSuchFieldException, SecurityException, IllegalAccessException,
	// IllegalArgumentException,
	// InvocationTargetException, NoSuchMethodException {
	// Field field = getFinalField(fieldName, object);
	// return field.isAnnotationPresent(Transient.class);
	// }
	public static Field getFinalField(String fieldName, Object object)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		return getFinalField(fieldName, object, false);
	}

	public static Field getFinalField(String fieldName, Object object, boolean nullIfNoPath)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Object finalObject = getFinalInvokeClass(fieldName, object);
		String lastField = getLastField(fieldName);
		if (nullIfNoPath && finalObject == null) {
			return null;
		}
		Field field = FieldUtils.getField(finalObject.getClass(), lastField, true);
		return field;
	}

	private static String getLastField(String fieldName) {
		if (fieldName.indexOf(".") > -1) {
			return fieldName.substring(fieldName.lastIndexOf(".") + 1);
		} else {
			return fieldName;
		}
	}

	protected static Object getFinalInvokeClass(String fieldName, Object object)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		String[] fields = fieldName.split("\\.");
		Object finalObject = object;
		for (int i = 0; i < fields.length - 1; i++) {
			String field = fields[i];
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
