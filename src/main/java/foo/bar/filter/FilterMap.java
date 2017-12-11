package foo.bar.filter;

import java.util.HashMap;
import java.util.Map;

import foo.bar.service.utils.HqlConditions;

public class FilterMap
// extends HashMap<String, HqlConditions> implements Map<String, HqlConditions>
{

	/**
	 * serializable
	 */
	private static final long serialVersionUID = 6271753688743170694L;

	private FilterAddCondition filterAddCondition;

	private Map<String, Object> map = new HashMap<>();

	public FilterMap() {
		super();
		this.filterAddCondition = FilterAddCondition.AND;
	}

	public FilterMap(FilterAddCondition filterAddCondition) {
		super();
		this.filterAddCondition = filterAddCondition;
	}

	public FilterAddCondition getFilterAddCondition() {
		return filterAddCondition;
	}

	public void put(String key, HqlConditions condition) {
		this.map.put(key, condition);
	}

	public void put(FilterMap filterMap) {
		this.map.put(filterMap.hashCode() + "", filterMap);
	}

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}

}
