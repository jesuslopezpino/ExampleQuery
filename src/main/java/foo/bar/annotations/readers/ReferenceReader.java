package foo.bar.annotations.readers;

import java.lang.reflect.Field;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import foo.bar.annotations.Reference;

public class ReferenceReader {

	private static final Logger LOG = LogManager.getLogger(ReferenceReader.class);

	public static String getReferenceForField(String fieldName, Object object) {
		String result = null;
		try {
			Field field = object.getClass().getDeclaredField(fieldName);
			final Reference reference = field.getDeclaredAnnotation(Reference.class);
			result = reference.referenceFor();
		} catch (NoSuchFieldException e) {
			LOG.error(e.getMessage(), e);
			// TODO: throw exception
		}
		return result;
	}
	
	public static String getReferenceFieldName(String fieldName, Object object) {
		String result = null;
		try {
			Field field = object.getClass().getDeclaredField(fieldName);
			final Reference reference = field.getDeclaredAnnotation(Reference.class);
			result = reference.fieldName();
		} catch (NoSuchFieldException e) {
			LOG.error(e.getMessage(), e);
			// TODO: throw exception
		}
		return result;
	}

	public static boolean isReferenceField(String fieldName, Object object) {
		boolean result = false;
		try {
			Field field = object.getClass().getDeclaredField(fieldName);
			final Reference reference = field.getDeclaredAnnotation(Reference.class);
			result = reference != null;
		} catch (NoSuchFieldException e) {
			LOG.error(e.getMessage(), e);
			// TODO: throw exception
		}
		return result;
	}
}
