package com.polvisoft.exampleQuery.service.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.polvisoft.exampleQuery.annotations.readers.FilterForFieldReader;
import com.polvisoft.exampleQuery.filter.FilterAddCondition;

public class UtilsService {

	private static Logger LOGGER = Logger.getLogger(UtilsService.class);

	public static String getFieldForQuery(Object example, String filterField)
			throws NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException {
		String fieldForQuery;
		// first check if the field is @FilterForField or not
		boolean isFilterForField = FilterForFieldReader.isAnnotatedField(filterField, example);
		if (isFilterForField) {
			// field for query = the referenced field
			fieldForQuery = FilterForFieldReader.getValue(filterField, example);
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
			String nameForParameter, FilterAddCondition filterAddCondition) {
		LOGGER.debug("tableName: " + tableName);
		LOGGER.debug("filterField: " + filterField);
		LOGGER.debug("condition: " + condition);
		LOGGER.debug("nameForParameter: " + nameForParameter);
		LOGGER.debug("filterAddCondition: " + filterAddCondition);
		String result = null;
		String addCondition = null;
		if (filterAddCondition == null) {
			addCondition = " ";
		} else {
			addCondition = filterAddCondition.toString();
		}
		switch (condition) {
		case LIKE:
			result = getClauseLike(tableName, filterField, condition, nameForParameter, addCondition);
			break;
		case LIKE_IGNORE_CASE:
			result = getClauseLikeIgnoreCase(tableName, filterField, condition, nameForParameter, addCondition);
			break;
		case IS_NOT_EMPTY:
		case IS_NOT_NULL:
		case IS_EMPTY:
		case IS_NULL:
			result = getClauseIsNullOrNotNull(tableName, filterField, condition, addCondition);
			break;
		// case HqlConditions.BETWEEN:
		case IN:
		case NOT_IN:
			result = getClauseIn(tableName, filterField, condition, nameForParameter, addCondition);
			break;
		case EQUALS:
		case GREATER_EQUALS:
		case GREATER_THAN:
		case LOWER_EQUALS:
		case LOWER_THAN:
		case NOT_EQUALS:
			result = getClauseConditionCase(tableName, filterField, condition, nameForParameter, addCondition);
			break;
		// case HqlConditions.MEMBER_OF:
		// case HqlConditions.NOT_MEMBER_OF:
		// // TODO: consider???
		// break;
		}
		return result;
	}

	private static String getClauseIn(String tableName, String filterField, HqlConditions condition,
			String nameForParameter, String filterAddCondition) {
		return filterAddCondition + "(" + tableName + "." + filterField + " " + condition + " (:" + nameForParameter
				+ "))";
	}

	public static String getClauseIsNullOrNotNull(String tableName, String filterField, HqlConditions condition,
			String filterAddCondition) {
		return filterAddCondition + "(" + tableName + "." + filterField + " " + condition + ")";
	}

	public static String getClauseLike(String tableName, String filterField, final HqlConditions condition,
			String nameForParameter, String filterAddCondition) {
		return filterAddCondition + "(" + tableName + "." + filterField + " " + condition + " :" + nameForParameter
				+ ")";
	}

	public static String getClauseLikeIgnoreCase(String tableName, String filterField, final HqlConditions condition,
			String nameForParameter, String filterAddCondition) {
		return filterAddCondition + "(UPPER(" + tableName + "." + filterField + ")" + condition + ":" + nameForParameter
				+ ")";
	}

	private static String getClauseConditionCase(String tableName, String filterField, final HqlConditions condition,
			String nameForParameter, String filterAddCondition) {
		return filterAddCondition + "(" + tableName + "." + filterField + "" + condition + ":" + nameForParameter + ")";
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

	public static String getAliasForField(String field) {
		return field.replaceAll("\\.", "_");
	}

	public static String getFieldFromAlias(String alias) {
		return alias.replaceAll("_", "\\.");
	}
}
