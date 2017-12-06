package foo.bar.annotations.readers;

import java.lang.reflect.Field;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import foo.bar.annotations.Reference;
import foo.bar.utils.Utils;

public class ReferenceReader {

	private static final Logger LOG = LogManager.getLogger(ReferenceReader.class);

	public static String getReferenceForField(String fieldName, Object object)
			throws NoSuchFieldException, SecurityException {
		String result = null;
		Field field = Utils.getField(fieldName, object);
		final Reference reference = field.getDeclaredAnnotation(Reference.class);
		result = reference.referenceFor();
		return result;
	}

	public static String getReferenceFieldName(String fieldName, Object object) throws NoSuchFieldException, SecurityException {
		String result = null;
		Field field = Utils.getField(fieldName, object);
		final Reference reference = field.getDeclaredAnnotation(Reference.class);
		result = reference.fieldName();
		return result;
	}

	public static boolean isReferenceField(String fieldName, Object object)
			throws NoSuchFieldException, SecurityException {
		boolean result = false;
		Field field = Utils.getField(fieldName, object);
		final Reference reference = field.getDeclaredAnnotation(Reference.class);
		result = reference != null;
		return result;
	}
}
