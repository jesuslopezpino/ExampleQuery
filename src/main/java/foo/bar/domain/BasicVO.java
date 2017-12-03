package foo.bar.domain;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.apache.log4j.Logger;

import foo.bar.utils.Utils;

@MappedSuperclass
public abstract class BasicVO<PK> {

	public static final String PK = "id";
	
	public BasicVO(){
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
	private PK pk;
	
	public PK getPk() {
		return pk;
	}

	public void setPk(PK pk) {
		this.pk = pk;
	}
}
