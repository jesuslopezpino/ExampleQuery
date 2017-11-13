package foo.bar.service;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Column;
import javax.persistence.Id;

import org.apache.log4j.Logger;

import foo.bar.NuevoUtils;

public abstract class BasicVO<PK> {

	public static final String PK = "id";
	
	Logger LOG = Logger.getLogger(BasicVO.class);

	public BasicVO(){
		super();
	}
	
	public BasicVO(Map<String, Object> mapValues) {
		super();
		for (Iterator<Entry<String, Object>> iterator = mapValues.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, Object> entry = iterator.next();
			NuevoUtils.setFieldValue(entry.getKey(), entry.getValue(), this);
		}
	}

	@Id
	@Column(name = BasicVO.PK)
	public PK id;
	
	public PK getId() {
		return id;
	}

	public void setId(PK id) {
		this.id = id;
	}
}
