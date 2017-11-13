package foo.bar.annotations;

import java.lang.reflect.Field;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ReferenceReader {

	private static final Logger LOG = LogManager.getLogger(ReferenceReader.class);

	public static String getReferenceField(String fieldName, Object object) {
		String result = null;
		try {
			Field field = object.getClass().getDeclaredField(fieldName);
			final Reference reference = field.getDeclaredAnnotation(Reference.class);
			result = reference.value();
		} catch (NoSuchFieldException e) {
			LOG.error(e.getMessage(), e);
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
		}
		return result;
	}
}
