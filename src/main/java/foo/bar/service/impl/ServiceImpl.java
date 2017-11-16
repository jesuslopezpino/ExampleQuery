package foo.bar.service.impl;

import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import foo.bar.annotations.readers.RangeReader;
import foo.bar.annotations.readers.ReferenceReader;
import foo.bar.domain.BasicVO;
import foo.bar.exceptions.ExampleQueryException;
import foo.bar.exceptions.UniqueException;
import foo.bar.service.Service;
import foo.bar.service.utils.HqlConditions;
import foo.bar.utils.Utils;

public abstract class ServiceImpl<VO extends BasicVO> implements Service<VO> {

//	private static final String END = "_fin";
//
//	private static final String START = "_inicio";

	EntityManager entityManager;

	private static Logger LOGGER = Logger.getLogger(ServiceImpl.class);

	private final Class<VO> voClass;

	public ServiceImpl() {
		super();
		this.voClass = (Class<VO>) ((ParameterizedType) this.getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
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

	public List<VO> findByExample(VO example, Map<String, String> filter) throws ExampleQueryException {
		String select = "select tabla";
		Query query = createQueryForExample(example, filter, select);
		return query.getResultList();
	}

	public List<VO> findCustomByExample(VO example, String[] fields, Map<String, String> filter)
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

	private String getClauseIsNullOrNotNull(String tableName, String filterField, String condition) {
		return " and (" + tableName + "." + filterField + " " + condition + ")";
	}

	// private String getClauseBetween(String tableName, String filterField) {
	// return " and (" + tableName + "." + filterField + " between " + ":" +
	// getNameForParameter(filterField) + START
	// + " and :" + getNameForParameter(filterField) + END + ")";
	// }

	// private String getClauseBetweenEnd(String tableName, String filterField)
	// {
	// return " and (" + tableName + "." + filterField + " <= " + ":" +
	// getNameForParameter(filterField) + END + ")";
	// }

	// private String getClauseBetweenStart(String tableName, String
	// filterField) {
	// return " and (" + tableName + "." + filterField + " >= " + ":" +
	// getNameForParameter(filterField) + START + ")";
	// }

	private String getClauseLike(String tableName, String filterField, final String condition,
			String nameForParameter) {
		return " and (" + tableName + "." + filterField + " " + condition + ":" + nameForParameter + ")";
	}

	private String getClauseLikeIgnoreCase(String tableName, String filterField, final String condition,
			String nameForParameter) {
		return " and (UPPER(" + tableName + "." + filterField + ")" + condition + ":" + nameForParameter + ")";
	}

	private String getClauseConditionCase(String tableName, String filterField, final String condition,
			String nameForParameter) {
		return " and (" + tableName + "." + filterField + "" + condition + ":" + nameForParameter + ")";
	}

	private String getNameForParameter(String filterField, String condition) {
		String result = null;
		switch (condition) {
		case HqlConditions.IS_NOT_EMPTY:
		case HqlConditions.IS_NOT_NULL:
		case HqlConditions.IS_EMPTY:
		case HqlConditions.IS_NULL:
			break;
		default:
			result = filterField.replaceAll("\\.", "_");
			break;
		}
		return result;
	}

	// private String getNameForParameterStart(String filterField) {
	// return getNameForParameter(filterField) + START;
	// }
	//
	// private String getNameForParameterEnd(String filterField) {
	// return getNameForParameter(filterField) + END;
	// }

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

	private Query createQueryForExample(VO example, Map<String, String> filter, String select)
			throws ExampleQueryException {
		Map<String, Object> parameters = new HashMap<>();
		String from = " from " + voClass.getName() + " tabla";
		String where = " where 1=1";
		for (Iterator<Entry<String, String>> iterator = filter.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, String> type = iterator.next();
			// first check if the field is transient or not
			String condition = type.getValue();
			String filterField = type.getKey();
			boolean isTransient = Utils.isTransientField(filterField, example);
			// if it is part of the entity
			String fieldForQuery;
			Object valueForQuery;
			if (isTransient) {
				fieldForQuery = Utils.getReferencedField(example, filterField);
				valueForQuery = Utils.getFieldValue(example, filterField);
			} else {
				fieldForQuery = filterField;
				valueForQuery = Utils.getFieldValue(example, filterField);
			}
			boolean applyValue = this.hasToApplyConditionForQuery(condition, valueForQuery);
			if (applyValue) {
				String nameForParameter = getNameForParameter(filterField, condition);
				where += getClauseCondition("tabla", filterField, condition, nameForParameter);
				if (nameForParameter != null) {
					Object fixedValueForQuery = this.fixValueForQuery(valueForQuery, condition);
					parameters.put(nameForParameter, fixedValueForQuery);
				}
			}
			// then check if we have to apply the current value to the query
			// (depends on condition + value)
			// if we have to apply the value
			// field for query = the current field name
			// value for query = the current field value
			// else
			// nothing
			// if it is transient
			// then check if we have to apply the current value to the query
			// (depends on condition + value)
			// if we have to apply the value
			// field for query = the referenced field
			// value for query = the current field value

//			// TODO: detect duplicated filter fields to avoid problems
//			Object exampleFieldValue = Utils.getFieldValue(example, filterField);
//			if (exampleFieldValue != null) {
//				// Field Value necessary
//				switch (condition) {
//				case HqlConditions.LOWER_EQUALS:
//				case HqlConditions.LOWER_THAN:
//				case HqlConditions.GREATER_EQUALS:
//				case HqlConditions.GREATER_THAN:
//				case HqlConditions.NOT_EQUALS:
//				case HqlConditions.EQUALS:
//				case HqlConditions.IS_NULL:
//					where += getClauseConditionCase("tabla", filterField, condition);
//					parameters.put(getNameForParameter(filterField), exampleFieldValue);
//					break;
//				case HqlConditions.LIKE:
//					where += getClauseLike("tabla", filterField, condition);
//					parameters.put(getNameForParameter(filterField), "%" + exampleFieldValue.toString() + "%");
//					break;
//				case HqlConditions.LIKE_IGNORE_CASE:
//					where += getClauseLikeIgnoreCase("tabla", filterField, condition);
//					parameters.put(getNameForParameter(filterField),
//							"%" + exampleFieldValue.toString().toUpperCase() + "%");
//					break;
//				case HqlConditions.IN:
//				case HqlConditions.NOT_IN:
//					if (Utils.isListField(filterField, example)) {
//						where += getClauseConditionCase("tabla", filterField, condition);
//						parameters.put(getNameForParameter(filterField), exampleFieldValue);
//					} else {
//						throw new ExampleQueryException("field: " + filterField + " is not List ");
//					}
//					break;
//				default:
//					LOGGER.error("UNEXPECTED CONDITION: " + condition);
//					throw new ExampleQueryException("UNEXPECTED CONDITION: " + condition);
//				}
//			} else if (exampleFieldValue == null) {
//				switch (condition) {
//				// Non value necessary
//				case HqlConditions.IS_NULL:
//				case HqlConditions.IS_NOT_NULL:
//				case HqlConditions.IS_EMPTY:
//				case HqlConditions.IS_NOT_EMPTY:
//					where += getClauseIsNullOrNotNull("tabla", filterField, condition);
//					break;
//				// Range Values necessaries
//				case HqlConditions.BETWEEN:
//					boolean isAnnotated = RangeReader.isDateRangeAnnotatedField(filterField, example);
//					if (isAnnotated) {
//						Date startValue = RangeReader.getStartFieldValue(filterField, example);
//						Date endValue = RangeReader.getEndFieldValue(filterField, example);
//						if (startValue != null && endValue != null) {
//							where += getClauseBetween("tabla", filterField);
//							parameters.put(getNameForParameterStart(filterField), startValue);
//						} else if (startValue != null) {
//							where += getClauseBetweenStart("tabla", filterField);
//						} else if (endValue != null) {
//							where += getClauseBetweenEnd("tabla", filterField);
//							parameters.put(getNameForParameterEnd(filterField), startValue);
//						}
//					} else {
//						LOGGER.warn(filterField + " is not annotated with @DataRange");
//					}
//					break;
//				// Reference field necessary
//				case HqlConditions.IN:
//				case HqlConditions.NOT_IN:
//				case HqlConditions.LOWER_EQUALS:
//				case HqlConditions.LOWER_THAN:
//				case HqlConditions.GREATER_EQUALS:
//				case HqlConditions.GREATER_THAN:
//				case HqlConditions.NOT_EQUALS:
//				case HqlConditions.EQUALS:
//				case HqlConditions.LIKE:
//				case HqlConditions.LIKE_IGNORE_CASE:
//					if (ReferenceReader.isReferenceField(filterField, example)) {
//						String referenceFieldName = ReferenceReader.getReferenceFieldName(filterField, example);
//						Object value = Utils.getFieldValue(example, referenceFieldName);
//						if (value != null) {
//							String referenceFor = ReferenceReader.getReferenceForField(filterField, example);
//							where += getClauseConditionCase("tabla", referenceFor, condition);
//							parameters.put(getNameForParameter(filterField), value);
//						}
//					}
//					break;
//				default:
//					LOGGER.error("UNEXPECTED CONDITION: " + condition);
//					throw new ExampleQueryException("UNEXPECTED CONDITION: " + condition);
//				}
//			}

		}
		String hqlString = select + from + where;
		LOGGER.info("ExampleQuery: " + hqlString);
		Query query = this.entityManager.createQuery(hqlString);
		for (Iterator<Entry<String, Object>> iterator = parameters.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, Object> parameter = (Entry) iterator.next();
			query.setParameter(parameter.getKey(), parameter.getValue());
		}
		return query;
	}

	private Object fixValueForQuery(Object valueForQuery, String condition) {
		Object result = null;
		String stringValue = null;
		switch (condition) {
		case HqlConditions.LIKE:
			stringValue = (String) valueForQuery;
			result = "%" + stringValue + "%";
			break;
		case HqlConditions.LIKE_IGNORE_CASE:
			stringValue = (String) valueForQuery;
			result = "%" + stringValue.toUpperCase() + "%";
			break;
		default:
			result = valueForQuery;
			break;
		}
		return result;
	}

	private String getClauseCondition(String tableName, String filterField, String condition, String nameForParameter) {
		String result = null;
		switch (condition) {
		case HqlConditions.LIKE:
			result = getClauseLike("tabla", filterField, condition, nameForParameter);
			// parameters.put(getNameForParameter(filterField), "%" +
			// exampleFieldValue.toString() + "%");
			break;
		case HqlConditions.LIKE_IGNORE_CASE:
			result = getClauseLikeIgnoreCase("tabla", filterField, condition, nameForParameter);
			break;
		case HqlConditions.IS_NOT_EMPTY:
		case HqlConditions.IS_NOT_NULL:
		case HqlConditions.IS_EMPTY:
		case HqlConditions.IS_NULL:
			result = getClauseIsNullOrNotNull("tabla", filterField, condition);
			break;
		// case HqlConditions.BETWEEN:
		case HqlConditions.EQUALS:
		case HqlConditions.GREATER_EQUALS:
		case HqlConditions.GREATER_THAN:
		case HqlConditions.IN:
		case HqlConditions.LOWER_EQUALS:
		case HqlConditions.LOWER_THAN:
		case HqlConditions.NOT_EQUALS:
		case HqlConditions.NOT_IN:
			result = getClauseConditionCase("tabla", filterField, condition, nameForParameter);
			break;
		// case HqlConditions.MEMBER_OF:
		// case HqlConditions.NOT_MEMBER_OF:
		// // TODO: consider???
		// break;
		}
		return result;
	}

	private boolean hasToApplyConditionForQuery(String condition, Object value) {
		boolean result = false;
		switch (condition) {
		case HqlConditions.IS_NOT_EMPTY:
		case HqlConditions.IS_NOT_NULL:
		case HqlConditions.IS_EMPTY:
		case HqlConditions.IS_NULL:
			result = true;
			break;
		// case HqlConditions.BETWEEN:
		case HqlConditions.EQUALS:
		case HqlConditions.GREATER_EQUALS:
		case HqlConditions.GREATER_THAN:
		case HqlConditions.IN:
		case HqlConditions.LIKE:
		case HqlConditions.LIKE_IGNORE_CASE:
		case HqlConditions.LOWER_EQUALS:
		case HqlConditions.LOWER_THAN:
		case HqlConditions.NOT_EQUALS:
		case HqlConditions.NOT_IN:
			if (value != null) {
				result = true;
			}
			break;
		case HqlConditions.MEMBER_OF:
		case HqlConditions.NOT_MEMBER_OF:
			// TODO: consider???
			break;
		default:
			break;
		}
		return result;
	}

}
