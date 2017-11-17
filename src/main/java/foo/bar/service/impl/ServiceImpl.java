package foo.bar.service.impl;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import foo.bar.domain.BasicVO;
import foo.bar.exceptions.ExampleQueryException;
import foo.bar.exceptions.UniqueException;
import foo.bar.service.Service;
import foo.bar.service.utils.HqlConditions;
import foo.bar.utils.Utils;

public abstract class ServiceImpl<VO extends BasicVO> implements Service<VO> {


	EntityManager entityManager;

	private static Logger LOGGER = Logger.getLogger(ServiceImpl.class);

	private final Class<VO> voClass;

	public ServiceImpl() {
		super();
		this.voClass = (Class<VO>) ((ParameterizedType) this.getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
		LOGGER.info("****************************************************************************");
		LOGGER.info("Creating service for class: " + this.voClass.getName());
	}

	public VO findByPk(Object primaryKey) {
		return (VO) entityManager.find(voClass, primaryKey);
	}

	public VO findCustomByPk(Object primaryKey, String[] fields) {
		String select = createCustomSelect(fields);
		String from = " from " + voClass.getName();
		String where = " where " + BasicVO.PK + " = :" + BasicVO.PK;
		String hqlString = select + from + where;
		LOGGER.info("hqlString: " + hqlString);
		TypedQuery<VO> query = entityManager.createQuery(hqlString, voClass);
		query.setParameter(BasicVO.PK, primaryKey);
		VO result = (VO) query.getSingleResult();
		return result;
	}

	public List<VO> findByExample(VO example, Map<String, HqlConditions> filter) throws ExampleQueryException {
		String select = "select tabla";
		Query query = createQueryForExample(example, filter, select);
		return query.getResultList();
	}

	public List<VO> findCustomByExample(VO example, String[] fields, Map<String, HqlConditions> filter)
			throws ExampleQueryException {
		String select = this.createCustomSelect(fields);
		Query query = createQueryForExample(example, filter, select);
		return query.getResultList();
	}

	public boolean delete(VO entity) {
		boolean result = false;
		try {
			this.entityManager.remove(entity);
			result = true;
		} catch (Exception e) {
			// TODO: implement
		}
		return result;
	}

	public VO save(VO entity) throws UniqueException {
		try {
			this.entityManager.persist(entity);
		} catch (Exception e) {
			// TODO: implement
		}
		return entity;
	}

	public VO update(VO entity) {
		try {
			this.entityManager.merge(entity);
		} catch (Exception e) {
			// TODO: implement
		}
		return entity;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	private String getClauseIsNullOrNotNull(String tableName, String filterField, HqlConditions condition) {
		return " and (" + tableName + "." + filterField + " " + condition + ")";
	}

	private String getClauseLike(String tableName, String filterField, final HqlConditions condition,
			String nameForParameter) {
		return " and (" + tableName + "." + filterField + " " + condition + ":" + nameForParameter + ")";
	}

	private String getClauseLikeIgnoreCase(String tableName, String filterField, final HqlConditions condition,
			String nameForParameter) {
		return " and (UPPER(" + tableName + "." + filterField + ")" + condition + ":" + nameForParameter + ")";
	}

	private String getClauseConditionCase(String tableName, String filterField, final HqlConditions condition,
			String nameForParameter) {
		return " and (" + tableName + "." + filterField + "" + condition + ":" + nameForParameter + ")";
	}

	private String getNameForParameter(String filterField, HqlConditions condition) {
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

	private String createCustomSelect(String[] fields) {
		// String select = "select new " + voClass.getName() + " ( ";
		// int fieldsLength = fields.length;
		// int lastField = fieldsLength - 1;
		// for (int i = 0; i < fieldsLength; i++) {
		// String field = fields[i];
		// if (i != lastField) {
		// select += field + ", ";
		// } else {
		// select += field;
		// }
		// }
		// select += ")";
		// return select;
		String select = "select new " + voClass.getName() + " ( ";
		int fieldsLength = fields.length;
		int lastField = fieldsLength - 1;
		// TODO: detect duplicated fields to avoid problems
		for (int i = 0; i < fieldsLength; i++) {
			String field = fields[i];
			select += field + " as '" + field + "'";
			if (i != lastField) {
				select += ", ";
			}
		}
		select += ")";
		LOGGER.debug("createCustomSelect: " + select);
		return select;
	}

	private Query createQueryForExample(VO example, Map<String, HqlConditions> filter, String select)
			throws ExampleQueryException {
		Map<String, Object> parameters = new HashMap<>();
		String from = " from " + voClass.getName() + " tabla";
		String where = " where 1=1";
		for (Iterator<Entry<String, HqlConditions>> iterator = filter.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, HqlConditions> type = iterator.next();
			HqlConditions condition = type.getValue();
			String filterField = type.getKey();
			String fieldForQuery = null;
			Object valueForQuery = null;

			// first check if the field is transient or not
			boolean isTransient = Utils.isTransientField(filterField, example);
			if (isTransient) {
				// field for query = the referenced field
				fieldForQuery = Utils.getReferencedField(example, filterField);
			} else {
				// field for query = the current field name
				fieldForQuery = filterField;
			}
			valueForQuery = Utils.getFieldValue(example, filterField);

			// then check if we have to apply the current value to the query
			// (depends on condition + value)
			boolean applyValue = this.hasToApplyConditionForQuery(condition, valueForQuery);
			if (applyValue) {
				String nameForParameter = getNameForParameter(filterField, condition);
				where += getClauseCondition("tabla", fieldForQuery, condition, nameForParameter);
				if (nameForParameter != null) {
					Object fixedValueForQuery = this.fixValueForQuery(valueForQuery, condition);
					parameters.put(nameForParameter, fixedValueForQuery);
				}
			}
		}
		String hqlString = select + from + where;
		LOGGER.info("ExampleQuery: " + hqlString);
		Query query = this.entityManager.createQuery(hqlString);
		for (Iterator<Entry<String, Object>> iterator = parameters.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, Object> parameter = iterator.next();
			String parameterKey = parameter.getKey();
			Object parameterValue = parameter.getValue();
			LOGGER.info("parameter: \"" + parameterKey + "\"\tvalue: \"" + parameterValue + "\"");
			query.setParameter(parameterKey, parameterValue);
		}
		return query;
	}

	private Object fixValueForQuery(Object valueForQuery, HqlConditions condition) {
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

	private String getClauseCondition(String tableName, String filterField, HqlConditions condition, String nameForParameter) {
		String result = null;
		switch (condition) {
		case LIKE:
			result = getClauseLike("tabla", filterField, condition, nameForParameter);
			break;
		case LIKE_IGNORE_CASE:
			result = getClauseLikeIgnoreCase("tabla", filterField, condition, nameForParameter);
			break;
		case IS_NOT_EMPTY:
		case IS_NOT_NULL:
		case IS_EMPTY:
		case IS_NULL:
			result = getClauseIsNullOrNotNull("tabla", filterField, condition);
			break;
		// case HqlConditions.BETWEEN:
		case EQUALS:
		case GREATER_EQUALS:
		case GREATER_THAN:
		case IN:
		case LOWER_EQUALS:
		case LOWER_THAN:
		case NOT_EQUALS:
		case NOT_IN:
			result = getClauseConditionCase("tabla", filterField, condition, nameForParameter);
			break;
		// case HqlConditions.MEMBER_OF:
		// case HqlConditions.NOT_MEMBER_OF:
		// // TODO: consider???
		// break;
		}
		return result;
	}

	private boolean hasToApplyConditionForQuery(HqlConditions condition, Object value) {
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

}
