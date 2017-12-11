package foo.bar.filter;

import java.util.HashMap;
import java.util.Map;

import foo.bar.service.utils.HqlConditions;

public class FilterMap extends HashMap<String, HqlConditions> implements Map<String, HqlConditions>{

	/**
	 * serializable
	 */
	private static final long serialVersionUID = 6271753688743170694L;

	private FilterAddCondition filterAddCondition;

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

}
