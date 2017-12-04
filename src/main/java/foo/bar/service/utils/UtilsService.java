package foo.bar.service.utils;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import foo.bar.utils.Utils;

public class UtilsService {

	private static Logger LOGGER = Logger.getLogger(UtilsService.class);

	public static String getFieldForQuery(Object example, String filterField)
			throws NoSuchFieldException, SecurityException {
		String fieldForQuery;
		// first check if the field is transient or not
		boolean isTransient = Utils.isTransientField(filterField, example);
		if (isTransient) {
			// field for query = the referenced field
			fieldForQuery = Utils.getReferencedField(example, filterField);
		} else {
			// field for query = the current field name
			fieldForQuery = filterField;
		}
		return fieldForQuery;
	}

	public static boolean hasToApplyConditionForQuery(HqlConditions condition, Object value) {
		boolean result = false;
		switch (condition) {
		case IS_NOT_EMPTY:
		case IS_NOT_NULL:
		case IS_EMPTY:
		case IS_NULL:
			result = true;
			break;
		// case HqlConditions.BETWEEN:
		case EQUALS:
		case GREATER_EQUALS:
		case GREATER_THAN:
		case IN:
		case LIKE:
		case LIKE_IGNORE_CASE:
		case LOWER_EQUALS:
		case LOWER_THAN:
		case NOT_EQUALS:
		case NOT_IN:
			if (value != null) {
				if (value instanceof Collection) {
					result = ((Collection) value).size() > 0;
				}
				result = true;
			}
			break;
		// TODO: consider???
		// case HqlConditions.MEMBER_OF:
		// case HqlConditions.NOT_MEMBER_OF:
		// break;
		default:
			break;
		}
		return result;
	}

	public static String getClauseCondition(String tableName, String filterField, HqlConditions condition,
			String nameForParameter) {
		LOGGER.debug("tableName: " + tableName);
		LOGGER.debug("filterField: " + filterField);
		LOGGER.debug("condition: " + condition);
		LOGGER.debug("nameForParameter: " + nameForParameter);
		String result = null;
		switch (condition) {
		case LIKE:
			result = getClauseLike(tableName, filterField, condition, nameForParameter);
			break;
		case LIKE_IGNORE_CASE:
			result = getClauseLikeIgnoreCase(tableName, filterField, condition, nameForParameter);
			break;
		case IS_NOT_EMPTY:
		case IS_NOT_NULL:
		case IS_EMPTY:
		case IS_NULL:
			result = getClauseIsNullOrNotNull(tableName, filterField, condition);
			break;
		// case HqlConditions.BETWEEN:
		case IN:
		case NOT_IN:
			result = getClauseIn(tableName, filterField, condition, nameForParameter);
			break;
		case EQUALS:
		case GREATER_EQUALS:
		case GREATER_THAN:
		case LOWER_EQUALS:
		case LOWER_THAN:
		case NOT_EQUALS:
			result = getClauseConditionCase(tableName, filterField, condition, nameForParameter);
			break;
		// case HqlConditions.MEMBER_OF:
		// case HqlConditions.NOT_MEMBER_OF:
		// // TODO: consider???
		// break;
		}
		return result;
	}

	private static String getClauseIn(String tableName, String filterField, HqlConditions condition,
			String nameForParameter) {
		return " and (" + tableName + "." + filterField + " " + condition + " (:" + nameForParameter + "))";
	}

	public static String getClauseIsNullOrNotNull(String tableName, String filterField, HqlConditions condition) {
		return " and (" + tableName + "." + filterField + " " + condition + ")";
	}

	public static String getClauseLike(String tableName, String filterField, final HqlConditions condition,
			String nameForParameter) {
		return " and (" + tableName + "." + filterField + " " + condition + ":" + nameForParameter + ")";
	}

	public static String getClauseLikeIgnoreCase(String tableName, String filterField, final HqlConditions condition,
			String nameForParameter) {
		return " and (UPPER(" + tableName + "." + filterField + ")" + condition + ":" + nameForParameter + ")";
	}

	private static String getClauseConditionCase(String tableName, String filterField, final HqlConditions condition,
			String nameForParameter) {
		return " and (" + tableName + "." + filterField + "" + condition + ":" + nameForParameter + ")";
	}

	public static String getNameForParameter(String filterField, HqlConditions condition) {
		String result = null;
		switch (condition) {
		case IS_NOT_EMPTY:
		case IS_NOT_NULL:
		case IS_EMPTY:
		case IS_NULL:
			break;
		default:
			result = filterField.replaceAll("\\.", "_");
			break;
		}
		return result;
	}

	public static Object fixValueForQuery(Object valueForQuery, HqlConditions condition) {
		Object result = null;
		String stringValue = null;
		switch (condition) {
		case IN:
		case NOT_IN:
			if (valueForQuery instanceof List) {
				List listValue = (List) valueForQuery;
				if (listValue != null && listValue.get(0) != null && listValue.get(0) instanceof String) {
					result = "";
					for (int i = 0; i < listValue.size(); i++) {
						stringValue = (String) listValue.get(i);
						if (i != 0) {
							result += ", '" + stringValue + "'";
						} else {
							result = "'" + stringValue + "'";
						}
					}
				} else {
					result = valueForQuery;
				}
			} else {
				result = valueForQuery;
			}
			break;
		case LIKE:
			stringValue = (String) valueForQuery;
			result = "%" + stringValue + "%";
			break;
		case LIKE_IGNORE_CASE:
			stringValue = (String) valueForQuery;
			result = "%" + stringValue.toUpperCase() + "%";
			break;
		default:
			// TODO: consider date formats here!!!
			result = valueForQuery;
			break;
		}
		return result;
	}

}
