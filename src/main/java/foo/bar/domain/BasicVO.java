package foo.bar.domain;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import foo.bar.utils.Utils;

@MappedSuperclass
public abstract class BasicVO<PK> {

	public static final String PK = "pk";

	public BasicVO() {
		super();
	}

	public BasicVO(Map<String, Object> mapValues) {
		super();
		for (Iterator<Entry<String, Object>> iterator = mapValues.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, Object> entry = iterator.next();
			try {
				Utils.setFieldValue(entry.getKey(), entry.getValue(), this);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchFieldException | InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Id
	@Column(name = BasicVO.PK)
	protected PK pk;

	public PK getPk() {
		return pk;
	}

	public void setPk(PK pk) {
		this.pk = pk;
	}

	@Override
	public String toString() {
		return "BasicVO [pk=" + pk + "]";
	}

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
