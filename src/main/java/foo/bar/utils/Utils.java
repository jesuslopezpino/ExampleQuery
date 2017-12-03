package foo.bar.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import org.apache.log4j.Logger;

import foo.bar.annotations.Reference;
import foo.bar.annotations.readers.ReferenceReader;

public class Utils {

	private static Logger LOGGER = Logger.getLogger(Utils.class);

	public static <T> Object getFieldValue(T objeto, String fieldName) {
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
			final String methodName = Utils.getGetterOfField(fields.get(i));
			LOGGER.debug("methodName: " + methodName);
			try {
				if (i == fields.size() - 1) {
					// TODO: check at superclasses for the field
					invokedValue = objeto.getClass().getMethod(methodName).invoke(objeto, null);
				} else {
					invokedValue = objeto.getClass().getMethod(methodName).invoke(objeto, (Object[]) null);
					return getFieldValue(invokedValue, fieldName.replaceAll(fields.get(i) + ".", ""));

				}
			} catch (IllegalAccessException e) {
				LOGGER.error(e.getMessage());
			} catch (InvocationTargetException e) {
				LOGGER.error(e.getMessage());
			} catch (NoSuchMethodException e) {
				LOGGER.error(e.getMessage());
			} catch (SecurityException e) {
				LOGGER.error(e.getMessage());
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

	public static boolean isSuperClassField(String campo) {
		// TODO: change the method to return the SuperClass class to avoid
		// herence problems
		return false;
	}

	/**
	 * Gets the getter of field.
	 *
	 * @param field
	 *
	 * @return the getter of field
	 */
	public static String getGetterOfField(String field) {
		final String methodName = "get" + field.substring(0, 1).toUpperCase() + field.substring(1);
		return methodName;
	}

	/**
	 * Gets the setter of field.
	 *
	 * @param field
	 *
	 * @return the getter of field
	 */
	public static String getSetterOfField(String field) {
		final String methodName = "set" + field.substring(0, 1).toUpperCase() + field.substring(1);
		return methodName;
	}

	public static void setFieldValue(String field, Object value, Object objectClass) {
		String[] fields = field.split("\\.");
		String lastField = null;
		boolean loop = false;
		Object lastValue = null;
		if(fields.length == 1){
			lastField = field;
			lastValue = value;
		}else{
			loop = true;
			lastField = fields[fields.length - 1];
			LOGGER.info("loop mode");
		}
		Object lastClass = objectClass;
		try {
			for (int i = 0; loop && i < fields.length; i++) {
				LOGGER.info("loop " + i);
				if(i == fields.length -1){
					LOGGER.info("exit loop at " + i);
					break;
				}
				String getter = fields[i];
				LOGGER.info("searching getter: " + getter);
				lastValue = getFieldValue(lastClass, getter);
				LOGGER.info("lastValue: " + lastValue);
				if (lastValue == null) {
					LOGGER.info("lastClass " + lastClass.getClass().getName());
					Class<? extends Object> fieldType = lastClass.getClass().getField(getter).getType();
					lastValue = fieldType.newInstance();
					String setterToUse = getSetterOfField(getter);
					Method setterMethod = lastClass.getClass().getMethod(setterToUse, lastValue.getClass());
					setterMethod.invoke(lastClass, lastValue);
				}
				lastClass = lastValue;
			}
			String setterName = getSetterOfField(lastField);
			Method setter = lastClass.getClass().getMethod(setterName, value.getClass());
			LOGGER.info("setting field: " + lastField + " with value: " + value);
			setter.invoke(lastClass, value);
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	public static boolean isTransientField(String fieldName, Object object) {
		return hasAnnotation(fieldName, object, Transient.class);
	}

	public static boolean hasAnnotation(String fieldName, Object object, Class<? extends Annotation> annotation) {
		boolean result = false;
		Field field;
		try {
			field = object.getClass().getDeclaredField(fieldName);
			result = field.isAnnotationPresent(annotation);
		} catch (NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public static String getReferencedField(Object object, String fieldName) {
		return ReferenceReader.getReferenceForField(fieldName, object);
	}

}
