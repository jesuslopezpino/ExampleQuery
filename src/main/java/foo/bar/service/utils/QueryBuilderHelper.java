package foo.bar.service.utils;

import java.util.Map;

public class QueryBuilderHelper {

	String hqlString;

	Map<String, Object> parameters;

	public QueryBuilderHelper(String hqlString, Map<String, Object> parameters) {
		super();
		this.hqlString = hqlString;
		this.parameters = parameters;
	}

	public String getHqlString() {
		return this.hqlString;
	}

	public void setHqlString(String hqlString) {
		this.hqlString = hqlString;
	}

	public Map<String, Object> getParameters() {
		return this.parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

}
