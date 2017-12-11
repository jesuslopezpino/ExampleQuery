package foo.bar.service.utils;

import java.util.Map;

public class QueryBuilderHelper {

	private String select;

	private String from;

	private String where;

	private Map<String, Object> parameters;

	public QueryBuilderHelper(String select, String from, String where, Map<String, Object> parameters) {
		super();
		this.select = select;
		this.from = from;
		this.where = where;
		this.parameters = parameters;
	}

	public String getSelect() {
		return this.select;
	}

	public void setSelect(String select) {
		this.select = select;
	}

	public String getFrom() {
		return this.from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getWhere() {
		return this.where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public Map<String, Object> getParameters() {
		return this.parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public String getHqlString() {
		return this.select + this.from + this.where;
	}

}
