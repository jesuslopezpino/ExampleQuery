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
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;

import foo.bar.annotations.readers.FilterForFieldReader;
import foo.bar.domain.BasicVO;
import foo.bar.exceptions.ExampleQueryException;
import foo.bar.exceptions.UniqueException;
import foo.bar.filter.FilterMap;
import foo.bar.service.Service;
import foo.bar.service.utils.HqlConditions;
import foo.bar.service.utils.QueryBuilderHelper;
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
	}

	@Override
	public List<VO> findAll() throws ExampleQueryException {
		return this.findByExample(null, null);
	}

	@Override
	public int countAll() throws ExampleQueryException {
		String hqlString = "select count(*) from " + this.voClass.getName();
		LOGGER.debug("hqlString: " + hqlString);
		Long result = (Long) this.entityManager.createQuery(hqlString).getSingleResult();
		return result.intValue();
	}

	@Override
	public VO findByPk(Object primaryKey) {
		return this.entityManager.find(this.voClass, primaryKey);
	}

	@Override
	public VO findCustomByPk(Object primaryKey, String[] fields) throws ExampleQueryException {
		String select = this.createCustomSelect(fields);
		String from = this.createCustomFrom(fields);
		String where = " where " + BasicVO.PK + " = :" + BasicVO.PK;
		String hqlString = select + from + where;
		LOGGER.debug("hqlString: " + hqlString);
		Query query = this.entityManager.createQuery(hqlString, Map.class);
		query.setParameter(BasicVO.PK, primaryKey);
		Map<String, Object> map = (Map<String, Object>) query.getSingleResult();
		VO result = this.convertToEntity(map);
		return result;
	}

	private String createCustomFrom(String[] fields) {
		String tableAlias = this.getTableAliasForClass(this.voClass);
		String from = " from " + this.voClass.getName() + " " + tableAlias;
		int fieldsLength = fields.length;
		// TODO: detect duplicated fields to avoid problems
		for (int i = 0; i < fieldsLength; i++) {
			String field = fields[i];
			String fromForField = getFromForField(tableAlias, tableAlias + "." + field);
			if (!from.contains(fromForField)) {
				LOGGER.debug("From does not contains: " + fromForField);
				from += fromForField;
			}
		}
		LOGGER.debug("createCustomFromt: " + from);
		return from;
	}

	@Override
	public List<VO> findByExample(VO example, FilterMap filter) throws ExampleQueryException {
		String tableAlias = this.getTableAliasForClass(this.voClass);
		String select = "select " + tableAlias;
		String from = " from " + this.voClass.getName() + " " + tableAlias;
		Query query = this.createQueryForExample(example, filter, select, from);
		return query.getResultList();
	}

	@Override
	public int countByExample(VO example, FilterMap filter) throws ExampleQueryException {
		String select = "select count(*) ";
		String tableAlias = this.getTableAliasForClass(this.voClass);
		String from = " from " + this.voClass.getName() + " " + tableAlias;
		Query query = this.createQueryForExample(example, filter, select, from);
		Long result = (Long) query.getSingleResult();
		return result.intValue();
	}

	@Override
	public List<VO> findCustomByExample(VO example, String[] fields, FilterMap filter) throws ExampleQueryException {
		String select = this.createCustomSelect(fields);
		String from = this.createCustomFrom(fields);
		Query query = this.createQueryForExample(example, filter, select, from);
		List<Map<String, Object>> list = query.getResultList();
		List<VO> result = this.converToEntityList(list);
		return result;
	}

	protected List<VO> converToEntityList(List<Map<String, Object>> list) throws ExampleQueryException {
		List<VO> result = new ArrayList<>();
		for (Map<String, Object> mapValues : list) {
			VO entity = this.convertToEntity(mapValues);
			result.add(entity);
		}
		return result;
	}

	protected VO convertToEntity(Map<String, Object> mapValues) throws ExampleQueryException {
		try {
			Constructor constructor = this.voClass.getConstructor(Map.class);
			VO entity = (VO) constructor.newInstance(mapValues);
			return entity;
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			throw new ExampleQueryException(e);
		}
	}

	@Override
	public boolean delete(VO entity) {
		boolean result = false;
		try {
			this.entityManager.remove(entity);
			this.entityManager.flush();
			result = true;
		} catch (Exception e) {
			// TODO: implement
		}
		return result;
	}

	@Override
	public List<VO> saveList(List<VO> list) throws UniqueException {
		List<VO> result = new ArrayList<>();
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			VO vo = (VO) iterator.next();
			vo = this.save(vo);
			result.add(vo);
		}
		return result;
	}

	@Override
	public List<VO> updateList(List<VO> list) throws UniqueException {
		List<VO> result = new ArrayList<>();
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			VO vo = (VO) iterator.next();
			vo = this.update(vo);
			result.add(vo);
		}
		return result;
	}

	@Override
	public boolean deleteList(List<VO> list) {
		boolean result = true;
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			VO vo = (VO) iterator.next();
			result &= this.delete(vo);
		}
		return result;
	}

	@Override
	public VO save(VO entity) throws UniqueException {
		try {
			this.entityManager.persist(entity);
			this.entityManager.flush();
		} catch (Exception e) {
			String uniqueConstraintViolation = this.getConstraintNameViolation(e);
			if (StringUtils.isNotBlank(uniqueConstraintViolation)
					&& this.isUniqueConstraint(uniqueConstraintViolation)) {
				this.throwUniqueException(entity, uniqueConstraintViolation);
			} else {
				throw e;
			}
		}
		return entity;
	}

	protected void throwUniqueException(VO entity, String uniqueConstraintViolation) throws UniqueException {
		UniqueException uniqueException = new UniqueException(this.voClass, uniqueConstraintViolation, entity);
		LOGGER.error(uniqueException.getUniqueConstraint());
		LOGGER.error(uniqueException.getEntity().toStringDebug());
		throw uniqueException;
	}

	private boolean isUniqueConstraint(String uniqueConstraintViolation) {
		Table table = this.voClass.getAnnotation(Table.class);
		if (table != null) {
			for (int i = 0; i < table.uniqueConstraints().length; i++) {
				UniqueConstraint uniqueConstraint = table.uniqueConstraints()[i];
				if (uniqueConstraint.name().equals(uniqueConstraintViolation)) {
					return true;
				}
			}
		}
		return false;
	}

	private String getConstraintNameViolation(Exception e) {
		if (e instanceof PersistenceException) {
			PersistenceException persistenceException = (PersistenceException) e;
			if (persistenceException.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cause = (ConstraintViolationException) persistenceException.getCause();
				return cause.getConstraintName();
			}
		}
		return null;
	}

	@Override
	public VO update(VO entity) throws UniqueException {
		try {
			this.entityManager.merge(entity);
			this.entityManager.flush();
		} catch (Exception e) {
			String uniqueConstraintViolation = this.getConstraintNameViolation(e);
			if (StringUtils.isNotBlank(uniqueConstraintViolation)
					&& this.isUniqueConstraint(uniqueConstraintViolation)) {
				this.throwUniqueException(entity, uniqueConstraintViolation);
			} else {
				throw e;
			}
		}
		return entity;
	}

	public EntityManager getEntityManager() {
		return this.entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	private String createCustomSelect(String[] fields) {
		String select = "select new map ( ";
		int fieldsLength = fields.length;
		int lastField = fieldsLength;
		// TODO: detect duplicated fields to avoid problems
		for (int i = 0; i < fieldsLength; i++) {
			String field = fields[i];
			String entityField = this.getLastField(field);
			String entityAlias = this.getTableAliasForField(field);
			String fieldAlias = UtilsService.getAliasForField(field);
			select += entityAlias + "." + entityField + " as " + fieldAlias + "";
			if (i != lastField - 1) {
				select += ", ";
			}
		}
		select += ")";
		LOGGER.debug("createCustomSelect: " + select);
		return select;
	}

	public static String getFromForField(String table, String fieldForQuery) {
		LOGGER.debug("getFromForField");
		LOGGER.debug("table: " + table);
		LOGGER.debug("fieldForquery: " + fieldForQuery);
		String lastTable = table;
		String[] fields = fieldForQuery.split("\\.");
		String from = "";
		for (int i = 1; i < fields.length - 1; i++) {
			String field = fields[i];
			String nextJoin = " join " + lastTable + "." + field + " " + field + " ";
			LOGGER.debug("nextJoin =" + nextJoin);
			from += nextJoin;
			lastTable = field;
		}
		LOGGER.debug("FROM FOR FIELD: " + from);
		return from;
	}

	private Query createQueryForExample(VO example, FilterMap filter, String select, String from)
			throws ExampleQueryException {
		return this.createQueryForExample(example, filter, select, from, "");
	}

	private Query createQueryForExample(VO example, FilterMap filter, String select, String from, String where)
			throws ExampleQueryException {
		String tableAlias = this.getTableAliasForClass(this.voClass);
		Map<String, Object> parameters = new HashMap<>();
		QueryBuilderHelper builderHelper = new QueryBuilderHelper(select, from, where, parameters);
		// try {
		builderHelper = this.buildQueryForFilterMap(example, filter, tableAlias, builderHelper);
		LOGGER.info("HQL STRING: " + builderHelper.getHqlString());
		Query query = this.entityManager.createQuery(builderHelper.getHqlString());
		this.setQueryParams(query, builderHelper.getParameters());
		return query;
	}

	protected QueryBuilderHelper buildQueryForFilterMap(VO example, FilterMap filter, String tableAlias,
			QueryBuilderHelper builderHelper) throws ExampleQueryException {
		QueryBuilderHelper result = new QueryBuilderHelper(builderHelper.getSelect(), builderHelper.getFrom(),
				builderHelper.getWhere(), builderHelper.getParameters());
		if (filter != null) {
			for (Iterator<Entry<String, Object>> iterator = filter.getMap().entrySet().iterator(); iterator
					.hasNext();) {
				Entry<String, Object> type = iterator.next();
				if (type.getValue() instanceof FilterMap) {
					FilterMap filterMap = (FilterMap) type.getValue();
					if (filterMap.getMap().size() > 0) {
						LOGGER.info("initial result: " + result);
						QueryBuilderHelper nestedBuilderHelper = new QueryBuilderHelper(result.getSelect(),
								result.getFrom(), "", result.getParameters());
						nestedBuilderHelper = this.buildQueryForFilterMap(example, filterMap, tableAlias,
								nestedBuilderHelper);
						LOGGER.info("nestedBuilderHelper: " + nestedBuilderHelper);
						if (StringUtils.isNotBlank(nestedBuilderHelper.getWhere())) {
							String nestedWhere = null;
							if (StringUtils.isBlank(result.getWhere())) {
								nestedWhere = "(" + nestedBuilderHelper.getWhere() + ") ";
							} else {
								nestedWhere = result.getWhere() + " " + filter.getFilterAddCondition() + " ("
										+ nestedBuilderHelper.getWhere() + ") ";
							}
							LOGGER.info("nestedWhere: " + nestedWhere);
							result.setFrom(nestedBuilderHelper.getFrom());
							result.setWhere(nestedWhere);
							result.getParameters().putAll(nestedBuilderHelper.getParameters());
						}
					}
				} else {
					HqlConditions condition = (HqlConditions) type.getValue();
					String filterField = type.getKey();
					try {
						Object valueForQuery = Utils.getFieldValue(example, filterField, true);
						boolean applyValue = UtilsService.hasToApplyConditionForQuery(condition, valueForQuery);
						if (applyValue) {
							String fieldForQuery = UtilsService.getFieldForQuery(example, filterField);
							String lastTableAlias = this.getTableAliasForField(fieldForQuery);
							result.setFrom(this.applyFieldToForm(example, result.getFrom(), tableAlias, filterField,
									lastTableAlias));
							String nameForParameter = UtilsService.getNameForParameter(filterField, condition);
							result.setWhere(this.applyFieldToWhere(filter, result.getWhere(), condition, fieldForQuery,
									lastTableAlias, nameForParameter));
							this.applyFieldToParameters(result.getParameters(), condition, valueForQuery,
									nameForParameter);
						}
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
							| NoSuchMethodException | SecurityException | NoSuchFieldException e) {
						throw new ExampleQueryException(e);
					}
				}
			}
		}
		LOGGER.debug("ExampleQuery: " + result.getHqlString());
		return result;
	}

	protected void applyFieldToParameters(Map<String, Object> parameters, HqlConditions condition, Object valueForQuery,
			String nameForParameter) {
		if (nameForParameter != null) {
			Object fixedValueForQuery = UtilsService.fixValueForQuery(valueForQuery, condition);
			parameters.put(nameForParameter, fixedValueForQuery);
		}
	}

	protected String applyFieldToWhere(FilterMap filter, String where, HqlConditions condition, String fieldForQuery,
			String lastTableAlias, String nameForParameter) {
		String lastField = this.getLastField(fieldForQuery);
		if (where.equals("")) {
			where += UtilsService.getClauseCondition(lastTableAlias, lastField, condition, nameForParameter, null);
		} else {
			where += UtilsService.getClauseCondition(lastTableAlias, lastField, condition, nameForParameter,
					filter.getFilterAddCondition());
		}
		return where;
	}

	protected String applyFieldToForm(VO example, String from, String tableAlias, String filterField,
			String lastTableAlias) throws ExampleQueryException {
		String fromForField = null;
		try {
			if (FilterForFieldReader.isAnnotatedField(filterField, example)) {
				String referencedField = FilterForFieldReader.getValue(filterField, example);
				fromForField = getFromForField(tableAlias, lastTableAlias + "." + referencedField);
			} else {
				fromForField = getFromForField(tableAlias, lastTableAlias + "." + filterField);
			}
			if (!from.contains(fromForField)) {
				LOGGER.debug("From does not contains: " + fromForField);
				from += fromForField;
			}
			LOGGER.debug("FROM: " + from);
			return from;
		} catch (NoSuchFieldException | SecurityException | IllegalAccessException | InvocationTargetException
				| NoSuchMethodException e) {
			throw new ExampleQueryException(e);
		}
	}

	private String getLastField(String fieldForQuery) {
		return fieldForQuery.substring(fieldForQuery.lastIndexOf(".") + 1);
	}

	private String getTableAliasForClass(Class<VO> entity) {
		return entity.getSimpleName().substring(0, 1).toLowerCase() + entity.getSimpleName().substring(1);
	}

	private String getTableAliasForField(String fieldForQuery) {
		String result = null;
		String[] split = fieldForQuery.split("\\.");
		if (split.length > 1) {
			result = split[split.length - 2];
		} else {
			result = this.getTableAliasForClass(this.voClass);
			;
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
