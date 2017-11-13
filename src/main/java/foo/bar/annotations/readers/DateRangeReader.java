package foo.bar.annotations.readers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import foo.bar.Utils;

/**
 * The Class RangoFechaReader.
 */
public class DateRangeReader {

	private static final Logger LOGGER = LogManager.getLogger(DateRangeReader.class);

	/**
	 * Obtener valor campo inicio.
	 *
	 * @param campo
	 *            the campo
	 * @param objeto
	 *            the objeto
	 * @return the date
	 */
	public static Date getStartFieldValue(String campo, Object objeto) {
		Date result = null;
		try {
			final DateRange dateRange = getDataRangeValue(campo, objeto);
			final String campoInicio = dateRange.startField();
			final String methodName = Utils.getGetterOfField(campoInicio);

			if (Utils.isSuperClassField(campoInicio)) {
				result = (Date) objeto.getClass().getSuperclass().getMethod(methodName, null).invoke(objeto, null);
			} else {
				result = (Date) objeto.getClass().getMethod(methodName, null).invoke(objeto, null);
			}

		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			LOGGER.error(e.getMessage(), e);
		}

		return result;
	}

	/**
	 * Obtener valor campo fin.
	 *
	 * @param campo
	 *            the campo
	 * @param objeto
	 *            the objeto
	 * @return the date
	 */
	public static Date getEndFieldValue(String campo, Object objeto) {
		Date result = null;
		try {
			final DateRange rangoBusqueda = getDataRangeValue(campo, objeto);
			final String campoFin = rangoBusqueda.endField();
			final String methodName = Utils.getGetterOfField(campoFin);

			if (Utils.isSuperClassField(campoFin)) {
				result = (Date) objeto.getClass().getSuperclass().getMethod(methodName, null).invoke(objeto, null);
			} else {
				result = (Date) objeto.getClass().getMethod(methodName, null).invoke(objeto, null);
			}
			if (result != null) {

				final SimpleDateFormat formateoFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
				final String fechaFinal = formateoFecha.format(result);

				final SimpleDateFormat formateoHoras = new SimpleDateFormat("HH:mm:SS", Locale.getDefault());
				final String horaFinal = formateoHoras.format(result);

				final SimpleDateFormat formateoFinal = new SimpleDateFormat("dd/MM/yyyy HH:mm:SS", Locale.getDefault());
				if (horaFinal.equals("00:00:00")) {
					result = formateoFinal.parse(fechaFinal + " 23:59:59");
				}
			}

		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ParseException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return result;
	}


	/**
	 * Es campo date anotado.
	 *
	 * @param <T>
	 *            the generic type
	 * @param fieldName
	 *            the campo
	 * @param object
	 *            the object
	 * @return the boolean
	 */
	public static <T> Boolean isDateRangeAnnotatedField(String fieldName, Object object) {
		Boolean result = null;
		boolean isSuperClassField = false;
		try {
			if (fieldName.indexOf(".") > -1) {
				String[] fieldPath = fieldName.split("\\.");
				int i = 0;
				for (String field : fieldPath) {
					if (i < fieldPath.length - 1) {
						String getter = Utils.getGetterOfField(field);
						object = object.getClass().getMethod(getter, null).invoke(object, null);
						i++;
					} else {
						fieldName = field;
					}
				}
			}

			if (Utils.isSuperClassField(fieldName)) {
				isSuperClassField = true;
				result = object.getClass().getSuperclass().getDeclaredField(fieldName).getType().equals(Date.class);
			} else {
				isSuperClassField = false;
				result = Utils.isDateField(fieldName, object);
			}
			if (result) {
				Field field = null;
				if (isSuperClassField) {
					field = object.getClass().getSuperclass().getDeclaredField(fieldName);
				} else {
					field = object.getClass().getDeclaredField(fieldName);
				}
				final DateRange rangoBusqueda = field.getDeclaredAnnotation(DateRange.class);
				result = (rangoBusqueda != null);
			}
		}

		catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return result;
	}


	private static <T> DateRange getDataRangeValue(String fieldName, T object) {
		DateRange rangoBusqueda = null;
		boolean campoSuperclass = false;
		boolean isDate = false;
		try {
			if (Utils.isSuperClassField(fieldName)) {
				campoSuperclass = true;
				isDate = object.getClass().getSuperclass().getDeclaredField(fieldName).getType().equals(Date.class);
			} else {
				campoSuperclass = false;
				isDate = Utils.isDateField(fieldName, object);
			}
			if (isDate) {
				Field field = null;
				if (campoSuperclass) {
					field = object.getClass().getSuperclass().getDeclaredField(fieldName);
				} else {
					field = object.getClass().getDeclaredField(fieldName);
				}
				rangoBusqueda = field.getDeclaredAnnotation(DateRange.class);
			}
		} catch (NoSuchFieldException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return rangoBusqueda;
	}

}
