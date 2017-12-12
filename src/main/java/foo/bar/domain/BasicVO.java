package foo.bar.domain;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.MappedSuperclass;

import foo.bar.exceptions.ExampleQueryException;
import foo.bar.service.utils.UtilsService;
import foo.bar.utils.Utils;

@MappedSuperclass
public abstract class BasicVO<PK> {

	public static final String PK = "pk";

	public BasicVO() {
		super();
	}

	public BasicVO(Map<String, Object> mapValues) throws ExampleQueryException {
		super();
		for (Iterator<Entry<String, Object>> iterator = mapValues.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, Object> entry = iterator.next();
			try {
				String fieldName = UtilsService.getFieldFromAlias(entry.getKey());
				Utils.setFieldValue(fieldName, entry.getValue(), this);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchFieldException | InstantiationException e) {
				throw new ExampleQueryException(e);
			}
		}
	}

	public abstract PK getPk();

	public abstract void setPk(PK pk);

	@Override
	public String toString() {
		return "BasicVO [pk=" + this.getPk() + "]";
	}

	public abstract String toStringDebug();

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof BasicVO) {
			BasicVO other = (BasicVO) obj;
			if (other.getPk().equals(this.getPk())) {
				return true;
			}
		}
		return super.equals(obj);
	}

}
