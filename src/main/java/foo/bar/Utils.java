package foo.bar;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

public class Utils {

	private static Logger LOGGER = Logger.getLogger(Utils.class);

	/**
	 * Obtener valor string.
	 *
	 * @param <T>
	 *            the generic type
	 * @param objeto
	 *            the objeto
	 * @param field
	 *            the field
	 * @param returnStringValue
	 *            the return string value
	 * @return the object
	 */
	public static <T> Object getFieldValue(T objeto, String field, boolean returnStringValue) {
		final String[] fieldSplit = field.split("\\.");
		final List<String> campos = new ArrayList<>();
		if (fieldSplit.length == 0) {
			campos.add(field);
		} else {
			for (int i = 0; i < fieldSplit.length; i++) {
				final String string = fieldSplit[i];
				campos.add(string);
			}
		}

		Object invokedValue = null;
		for (int i = 0; i < campos.size(); i++) {
			final String methodName = Utils.getGetterOfField(campos.get(i));
			LOGGER.debug("methodName: " + methodName);
			try {
				if (i == campos.size() - 1) {
					invokedValue = objeto.getClass().getMethod(methodName).invoke(objeto, null);
					if (invokedValue instanceof Date) {
						if (returnStringValue) {
							// TODO: update DateRange with data format field
							final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
							invokedValue = sdf.format(invokedValue);
						} else {
							// devuelve la fecha como Date (para usarlo al
							// llamar a la
							// cfv persistence)
						}
					}
				} else {
					invokedValue = objeto.getClass().getMethod(methodName).invoke(objeto, (Object[]) null);
					return getFieldValue(invokedValue, field.replaceAll(campos.get(i) + ".", ""), returnStringValue);

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
		// return invokedValue == null ? "" : invokedValue;
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
		String methodName = getSetterOfField(field);
		try {
			Method method = objectClass.getClass().getMethod(methodName, value.getClass());
			method.invoke(objectClass, value);
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

}
