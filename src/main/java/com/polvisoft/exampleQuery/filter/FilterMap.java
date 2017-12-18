package com.polvisoft.exampleQuery.filter;

import java.util.HashMap;
import java.util.Map;

import com.polvisoft.exampleQuery.enums.HqlConditions;

public class FilterMap {

	/**
	 * serializable
	 */
	private static final long serialVersionUID = 6271753688743170694L;

	private final FilterAddCondition filterAddCondition;

	private Map<String, Object> map = new HashMap<>();

	public FilterMap() {
		super();
		this.filterAddCondition = FilterAddCondition.AND;
	}

	public FilterMap(final FilterAddCondition filterAddCondition) {
		super();
		this.filterAddCondition = filterAddCondition;
	}

	public FilterAddCondition getFilterAddCondition() {
		return this.filterAddCondition;
	}

	public void put(final String key, final HqlConditions condition) {
		this.map.put(key, condition);
	}

	public void put(final FilterMap filterMap) {
		this.map.put(filterMap.hashCode() + "", filterMap);
	}

	public Map<String, Object> getMap() {
		return this.map;
	}

	public void setMap(final Map<String, Object> map) {
		this.map = map;
	}

	@Override
	public String toString(){
		return this.filterAddCondition +  " map: " + this.map;
	}
}
