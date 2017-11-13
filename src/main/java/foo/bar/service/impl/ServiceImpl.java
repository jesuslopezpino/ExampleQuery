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
import org.mockito.InjectMocks;

import foo.bar.Utils;
import foo.bar.annotations.readers.DateRangeReader;
import foo.bar.annotations.readers.ReferenceReader;
import foo.bar.domain.BasicVO;
import foo.bar.exceptions.UniqueException;
import foo.bar.service.Service;
import foo.bar.service.SortOrder;
import foo.bar.service.utils.HQL_CONDITIONS;

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
		LOGGER.info("Creating service for class: " + this.voClass);
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

	private TypedQuery<VO> createCustomQuery(VO example, Map<String, String> filter, String query,
			TypedQuery<VO> typedQuery, String sortField, SortOrder sortOrder) {
		LOGGER.info("createCustomQuery - inicio");
		query += " WHERE 1=1 ";
		if (example != null) {
			if (filter != null) {
				for (String filterField : filter.keySet()) {
					final String condition = filter.get(filterField);
					final Object filterValue = Utils.getFieldValue(example, filterField, false);
					LOGGER.info(
							"filter: \"" + filterField + "\" condition: \"" + condition + "\" value: " + filterValue);
					// new impl
					// if condition == " is null"
					if (condition.equals(HQL_CONDITIONS.ES_NULL)) {

					} else if (condition.equals(HQL_CONDITIONS.LIKE_IGNORE_CASE)) {

					}
					// else
					// if DateRange
					// if Date??
					// if Referencia
					// if List
					// setParameters
					// new impl

					final boolean isDateFieldAnnotated = DateRangeReader.isDateRangeAnnotatedField(filterField,
							example);
					if ((filterValue != null && !filterValue.equals("")) || condition.equals(HQL_CONDITIONS.ES_NULL)
							|| isDateFieldAnnotated) {

						Date inicioRango = null;
						Date finRango = null;
						if (isDateFieldAnnotated) {
							inicioRango = DateRangeReader.getStartFieldValue(filterField, example);
							finRango = DateRangeReader.getEndFieldValue(filterField, example);
						}

						if (isDateFieldAnnotated && inicioRango != null && finRango != null) {
							query += " and (tabla." + filterField + " BETWEEN " + ":" + getNameForParameter(filterField)
									+ START;
							query += " AND :" + getNameForParameter(filterField) + END + ")";
						}

						if (filterValue instanceof List) {
							final List lista = (List) filterValue;
							final String referencia = ReferenceReader.getReferenceField(filterField, example);
							int i = 0;
							if (lista.size() > 0) {
								query += " and (";
								for (int j = 0; j < lista.size(); j++) {
									if (i > 0) {
										query += " or tabla." + referencia + condition + ":"
												+ getNameForParameter(filterField) + "_" + i;
									} else {
										query += " tabla." + referencia + condition + ":"
												+ getNameForParameter(filterField) + "_" + i;
									}
									i++;
								}
								query += " )";
							}
						} else if (!isDateFieldAnnotated) {
							if (condition.equals(HQL_CONDITIONS.LIKE_IGNORE_CASE)) {
								query += getClauseLikeIgnoreCase("tabla", filterField, condition);
							} else {
								query += " and tabla." + filterField + condition + ":"
										+ getNameForParameter(filterField);
							}
						}
					}
				}
				if (sortField != null && sortOrder != null) {
					query += " order by tabla." + sortField + " "
							+ (sortOrder.equals(sortOrder.ASCENDING) ? "ASC" : "DESC");
				}
				typedQuery = (TypedQuery) entityManager.createQuery(query);
				typedQuery = rellenarParametros(example, filter, typedQuery);
			}
		}
		LOGGER.info("createCustomQuery - fin");
		return typedQuery;
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

	private TypedQuery rellenarParametros(VO example, Map<String, String> filter, TypedQuery typedQuery) {
		for (String filterField : filter.keySet()) {
			final String condition = filter.get(filterField);
			final Object filterValue = Utils.getFieldValue(example, filterField, false);
			final boolean isDateFieldAnnotated = DateRangeReader.isDateRangeAnnotatedField(filterField, example);
			if ((filterValue != null && !filterValue.equals("")) || condition.equals(HQL_CONDITIONS.ES_NULL)
					|| isDateFieldAnnotated) {

				Date inicioRango = null;
				Date finRango = null;
				if (isDateFieldAnnotated) {
					inicioRango = DateRangeReader.getStartFieldValue(filterField, example);
					finRango = DateRangeReader.getEndFieldValue(filterField, example);
				}

				if (isDateFieldAnnotated && inicioRango != null && finRango != null) {
					typedQuery = typedQuery.setParameter(getNameForParameterStart(filterField), inicioRango);
					typedQuery = typedQuery.setParameter(getNameForParameterEnd(filterField), finRango);

				} else if (filterValue instanceof List) {
					final List lista = (List) filterValue;
					if (lista.size() > 0) {
						int i = 0;
						for (Object object : lista) {
							typedQuery = typedQuery.setParameter(getNameForParameter(filterField) + "_" + i, object);
							i++;
						}
					}
				} else if (!isDateFieldAnnotated) {

					if (condition.equals(HQL_CONDITIONS.LIKE_IGNORE_CASE)) {
						typedQuery = typedQuery.setParameter(getNameForParameter(filterField),
								"%" + filterValue.toString().toUpperCase() + "%");
					} else {
						typedQuery = typedQuery.setParameter(getNameForParameter(filterField), filterValue);
					}
				}

			}
		}
		return typedQuery;
	}

	public List<VO> findByExample(VO example, Map<String, String> filter) {
		String select = "select tabla";
		Query query = createQueryForExample(example, filter, select);
		return query.getResultList();
	}

	public List<VO> findCustomByExample(VO example, String[] fields, Map<String, String> filter) {
		String select = this.createCustomSelect(fields);
		Query query = createQueryForExample(example, filter, select);
		return query.getResultList();
	}

	private Query createQueryForExample(VO example, Map<String, String> filter, String select) {
		Map<String, Object> parameters = new HashMap<>();
		String from = " from " + voClass.getName() + " tabla";
		String where = " where 1=1";
		for (Iterator<Entry<String, String>> iterator = filter.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, String> type = iterator.next();
			String condition = type.getValue();
			String filterField = type.getKey();
			Object exampleFieldValue = Utils.getFieldValue(example, filterField, false);
			if (exampleFieldValue != null) {
				switch (condition) {
				case HQL_CONDITIONS.NOT_EQUALS:
				case HQL_CONDITIONS.EQUALS:
				case HQL_CONDITIONS.ES_NULL:
					where += getClauseConditionCase("tabla", filterField, condition);
					parameters.put(getNameForParameter(filterField), exampleFieldValue);
					break;
				case HQL_CONDITIONS.LIKE_IGNORE_CASE:
					where += getClauseLikeIgnoreCase("tabla", filterField, condition);
					parameters.put(getNameForParameter(filterField),
							"%" + exampleFieldValue.toString().toUpperCase() + "%");
					break;
				default:
					LOGGER.error("UNKNOWN CONDITION: " + condition);
					break;
				}
			} else if (exampleFieldValue == null && condition.equals(HQL_CONDITIONS.ES_NULL)) {
				where += getClauseConditionCase("tabla", filterField, condition);
			} else if (exampleFieldValue == null && condition.equals(HQL_CONDITIONS.BETWEEN)) {
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
			} else if (exampleFieldValue == null && condition.equals(HQL_CONDITIONS.IN)) {
				if (ReferenceReader.isReferenceField(filterField, example)) {
					String referenceField = ReferenceReader.getReferenceField(filterField, example);
					if (Utils.isListField(referenceField, example)) {
						List listValues = (List) Utils.getFieldValue(example, filterField, false);
						where += getClauseConditionCase("tabla", filterField, condition);
						parameters.put(getNameForParameter(filterField), listValues);
					}

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

	public boolean delete(VO entity) {
		boolean result = false;
		try {
			this.entityManager.remove(entity);
			result = true;
		} catch (Exception e) {
			// TODO: hacer
		}
		return result;
	}

	public VO save(VO entity) throws UniqueException {
		try {
			this.entityManager.persist(entity);
		} catch (Exception e) {
			// TODO: hacer
		}
		return entity;
	}

	public VO update(VO entity) {
		try {
			this.entityManager.merge(entity);
		} catch (Exception e) {
			// TODO: hacer
		}
		return entity;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
