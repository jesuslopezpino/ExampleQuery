package foo.bar.service.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
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
import foo.bar.service.utils.UtilsService;
import foo.bar.utils.Utils;

public abstract class ServiceImpl<VO extends BasicVO<?>> implements Service<VO> {

	EntityManager entityManager;

	private static Logger LOGGER = Logger.getLogger(ServiceImpl.class);

	private final Class<VO> voClass;

	public ServiceImpl() {
		super();
		this.voClass = (Class<VO>) ((ParameterizedType) this.getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
		LOGGER.debug("****************************************************************************");
		LOGGER.debug("Creating service for class: " + this.voClass.getName());
	}

	public List<VO> findAll() throws InstantiationException, IllegalAccessException, ExampleQueryException {
		VO example = voClass.newInstance();
		return this.findByExample(example, null);
	}

	public int countAll() throws InstantiationException, IllegalAccessException, ExampleQueryException {
		String select = "select count(*) ";
		String from = " from " + voClass.getName();
		String hqlString = select + from;
		Long result = (Long) entityManager.createQuery(hqlString).getSingleResult();
		return result.intValue();
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
		String tableAlias = getTableAliasForClass(voClass);
		String select = "select " + tableAlias;
		Query query = createQueryForExample(example, filter, select);
		return query.getResultList();
	}

	public List<VO> findCustomByExample(VO example, String[] fields, Map<String, HqlConditions> filter)
			throws ExampleQueryException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String select = this.createCustomSelect(fields);
		Query query = createQueryForExample(example, filter, select);
		List<Map<String, Object>> list = query.getResultList();
		List<VO> result = new ArrayList<>();
		for (Map<String, Object> mapValues : list) {
			Constructor constructor = voClass.getConstructor(HashMap.class);
			VO entity = (VO) constructor.newInstance(mapValues);
			result.add(entity);
		}
		return result;
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
			this.entityManager.flush();
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
		String select = "select new map ( ";
		int fieldsLength = fields.length;
		int lastField = fieldsLength;
		// TODO: detect duplicated fields to avoid problems
		for (int i = 0; i < fieldsLength; i++) {
			String field = fields[i];
			String entityField = getLastField(field);
			String tableAlias = getTableAliasForClass(voClass);
			String entityAlias = getLastTableAlias(tableAlias, field);
			select += entityAlias + "." + entityField + " as " + field + "";
			if (i != lastField - 1) {
				select += ", ";
			}
		}
		select += ")";
		LOGGER.debug("createCustomSelect: " + select);
		return select;
	}

	public static String getFromForField(String from, String table, String fieldForQuery) {
		LOGGER.debug("getFromForField");
		LOGGER.debug("table: " + table);
		LOGGER.debug("fieldForquery: " + fieldForQuery);
		String lastTable = table;
		String[] fields = fieldForQuery.split("\\.");
		String nextJoin = "";
		for (int i = 0; i < fields.length - 1; i++) {
			String field = fields[i];
			nextJoin = " join " + lastTable + "." + field + " " + field + " ";
			LOGGER.debug("nextJoin =" + nextJoin);
			if (!from.contains(nextJoin)) {
				from += nextJoin;
			}
			lastTable = field;
		}
		LOGGER.debug("FROOOM: " + from);
		return from;
	}

	private Query createQueryForExample(VO example, Map<String, HqlConditions> filter, String select)
			throws ExampleQueryException {

		Map<String, Object> parameters = new HashMap<>();
		String tableAlias = getTableAliasForClass(voClass);
		String from = " from " + voClass.getName() + " " + tableAlias;
		String where = " where 1=1";
		try {
			if (filter != null) {
				for (Iterator<Entry<String, HqlConditions>> iterator = filter.entrySet().iterator(); iterator
						.hasNext();) {
					Entry<String, HqlConditions> type = iterator.next();
					HqlConditions condition = type.getValue();
					String filterField = type.getKey();
					String fieldForQuery;
					fieldForQuery = UtilsService.getFieldForQuery(example, filterField);
					Object valueForQuery = Utils.getFieldValue(example, filterField);
					boolean applyValue = UtilsService.hasToApplyConditionForQuery(condition, valueForQuery);
					if (applyValue) {
						String nameForParameter = UtilsService.getNameForParameter(filterField, condition);
						String lastTableAlias = getLastTableAlias(tableAlias, fieldForQuery);
						if (lastTableAlias != tableAlias) {
							from = getFromForField(from, tableAlias, fieldForQuery);
						}
						fieldForQuery = getLastField(fieldForQuery);
						LOGGER.debug("FROM: " + from);
						where += UtilsService.getClauseCondition(lastTableAlias, fieldForQuery, condition,
								nameForParameter);
						if (nameForParameter != null) {
							// Object fixedValueForQuery =
							// UtilsService.fixValueForQuery(valueForQuery,
							// condition);
							parameters.put(nameForParameter, valueForQuery);
						}
					}
				}
			}
			String hqlString = select + from + where;
			LOGGER.info("ExampleQuery: " + hqlString);
			Query query = this.entityManager.createQuery(hqlString);
			setQueryParams(query, parameters);
			return query;
		} catch (NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("QUERY " + select + from + where);
			throw new ExampleQueryException(e.getMessage());
		}

	}

	private String getLastField(String fieldForQuery) {
		return fieldForQuery.substring(fieldForQuery.lastIndexOf(".") + 1);
	}

	private String getTableAliasForClass(Class<VO> entity) {
		return entity.getSimpleName().substring(0, 1).toLowerCase() + entity.getSimpleName().substring(1);
	}

	private String getLastTableAlias(String currentTableAlias, String fieldForQuery) {
		String result = currentTableAlias;
		String[] split = fieldForQuery.split("\\.");
		if (split.length > 1) {
			result = split[split.length - 2];
		} else {
			result = currentTableAlias;
		}
		LOGGER.debug("LAST TABLE ALIAS: of " + fieldForQuery + " is " + result);
		return result;
	}

	private void setQueryParams(Query query, Map<String, Object> parameters) {
		for (Iterator<Entry<String, Object>> iterator = parameters.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, Object> parameter = iterator.next();
			String parameterKey = parameter.getKey();
			Object parameterValue = parameter.getValue();
			LOGGER.debug("parameter: \"" + parameterKey + "\"\tvalue: \"" + parameterValue + "\"");
			query.setParameter(parameterKey, parameterValue);
		}
	}

}
