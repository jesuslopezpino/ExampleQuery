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

import foo.bar.annotations.readers.DateRangeReader;
import foo.bar.annotations.readers.ReferenceReader;
import foo.bar.domain.BasicVO;
import foo.bar.exceptions.ExampleQueryException;
import foo.bar.exceptions.UniqueException;
import foo.bar.service.Service;
import foo.bar.service.utils.HqlConditions;
import foo.bar.utils.Utils;

public abstract class ServiceImpl<VO extends BasicVO> implements Service<VO> {

	private static final String END = "_fin";

	private static final String START = "_inicio";

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

	public List<VO> findCustomByExample(VO example, String[] fields, Map<String, String> filter) throws ExampleQueryException {
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

	private String getClauseBetween(String tableName, String filterField) {
		return " and (" + tableName + "." + filterField + " between " + ":" + getNameForParameter(filterField) + START
				+ " and :" + getNameForParameter(filterField) + END + ")";
	}

	private String getClauseBetweenEnd(String tableName, String filterField) {
		return " and (" + tableName + "." + filterField + " <= " + ":" + getNameForParameter(filterField) + END + ")";
	}

	private String getClauseBetweenStart(String tableName, String filterField) {
		return " and (" + tableName + "." + filterField + " >= " + ":" + getNameForParameter(filterField) + START + ")";
	}

	private String getClauseLike(String tableName, String filterField, final String condition) {
		return " and (" + tableName + "." + filterField + " " + condition + ":" + getNameForParameter(filterField)
				+ ")";
	}

	private String getClauseLikeIgnoreCase(String tableName, String filterField, final String condition) {
		return " and (UPPER(" + tableName + "." + filterField + ")" + condition + ":" + getNameForParameter(filterField)
				+ ")";
	}

	private String getClauseConditionCase(String tableName, String filterField, final String condition) {
		return " and (" + tableName + "." + filterField + "" + condition + ":" + getNameForParameter(filterField) + ")";
	}

	private String getNameForParameter(String filterField) {
		return filterField.replaceAll("\\.", "_");
	}

	private String getNameForParameterStart(String filterField) {
		return getNameForParameter(filterField) + START;
	}

	private String getNameForParameterEnd(String filterField) {
		return getNameForParameter(filterField) + END;
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

	private Query createQueryForExample(VO example, Map<String, String> filter, String select)
			throws ExampleQueryException {
		Map<String, Object> parameters = new HashMap<>();
		String from = " from " + voClass.getName() + " tabla";
		String where = " where 1=1";
		for (Iterator<Entry<String, String>> iterator = filter.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, String> type = iterator.next();
			String condition = type.getValue();
			String filterField = type.getKey();
			// TODO: detect duplicated filter fields to avoid problems
			Object exampleFieldValue = Utils.getFieldValue(example, filterField, false);
			if (exampleFieldValue != null) {
				// Field Value necessary
				switch (condition) {
				case HqlConditions.LOWER_EQUALS:
				case HqlConditions.LOWER_THAN:
				case HqlConditions.GREATER_EQUALS:
				case HqlConditions.GREATER_THAN:
				case HqlConditions.NOT_EQUALS:
				case HqlConditions.EQUALS:
				case HqlConditions.IS_NULL:
					where += getClauseConditionCase("tabla", filterField, condition);
					parameters.put(getNameForParameter(filterField), exampleFieldValue);
					break;
				case HqlConditions.LIKE:
					where += getClauseLike("tabla", filterField, condition);
					parameters.put(getNameForParameter(filterField), "%" + exampleFieldValue.toString() + "%");
					break;
				case HqlConditions.LIKE_IGNORE_CASE:
					where += getClauseLikeIgnoreCase("tabla", filterField, condition);
					parameters.put(getNameForParameter(filterField),
							"%" + exampleFieldValue.toString().toUpperCase() + "%");
					break;
				case HqlConditions.IN:
				case HqlConditions.NOT_IN:
					if (Utils.isListField(filterField, example)) {
						where += getClauseConditionCase("tabla", filterField, condition);
						parameters.put(getNameForParameter(filterField), exampleFieldValue);
					}else{
						throw new ExampleQueryException("field: " + filterField + " is not List ");
					}
					break;
				default:
					LOGGER.error("UNEXPECTED CONDITION: " + condition);
					throw new ExampleQueryException("UNEXPECTED CONDITION: " + condition);
				}
			} else if (exampleFieldValue == null) {
				switch (condition) {
				// Non value necessary
				case HqlConditions.IS_NULL:
				case HqlConditions.IS_NOT_NULL:
				case HqlConditions.IS_EMPTY:
				case HqlConditions.IS_NOT_EMPTY:
					where += getClauseIsNullOrNotNull("tabla", filterField, condition);
					break;
				// Range Values necessaries
				case HqlConditions.BETWEEN:
					boolean isAnnotated = DateRangeReader.isDateRangeAnnotatedField(filterField, example);
					if (isAnnotated) {
						Date startValue = DateRangeReader.getStartFieldValue(filterField, example);
						Date endValue = DateRangeReader.getEndFieldValue(filterField, example);
						if (startValue != null && endValue != null) {
							where += getClauseBetween("tabla", filterField);
							parameters.put(getNameForParameterStart(filterField), startValue);
						} else if (startValue != null) {
							where += getClauseBetweenStart("tabla", filterField);
						} else if (endValue != null) {
							where += getClauseBetweenEnd("tabla", filterField);
							parameters.put(getNameForParameterEnd(filterField), startValue);
						}
					} else {
						LOGGER.warn(filterField + " is not annotated with @DataRange");
					}
					break;
				// Reference field necessary
				case HqlConditions.IN:
				case HqlConditions.NOT_IN:
					if (ReferenceReader.isReferenceField(filterField, example)) {
						String referenceField = ReferenceReader.getReferenceField(filterField, example);
						if (Utils.isListField(referenceField, example)) {
							List listValues = (List) Utils.getFieldValue(example, filterField, false);
							where += getClauseConditionCase("tabla", filterField, condition);
							parameters.put(getNameForParameter(filterField), listValues);
						}
					}
					break;
				case HqlConditions.LOWER_EQUALS:
				case HqlConditions.LOWER_THAN:
				case HqlConditions.GREATER_EQUALS:
				case HqlConditions.GREATER_THAN:
				case HqlConditions.NOT_EQUALS:
				case HqlConditions.EQUALS:
				case HqlConditions.LIKE:
				case HqlConditions.LIKE_IGNORE_CASE:
					// Nothing to do because we have to ignore null for those cases, maybe default and these cases are unnecessary
					break;
				default:
					LOGGER.error("UNEXPECTED CONDITION: " + condition);
					throw new ExampleQueryException("UNEXPECTED CONDITION: " + condition);
				}
			}

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

}
