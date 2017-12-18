package com.polvisoft.exampleQuery.repository.impl;

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
import org.springframework.beans.factory.annotation.Autowired;

import com.polvisoft.exampleQuery.annotations.readers.FilterForFieldReader;
import com.polvisoft.exampleQuery.domain.BasicDTO;
import com.polvisoft.exampleQuery.enums.HqlConditions;
import com.polvisoft.exampleQuery.exceptions.ExampleQueryException;
import com.polvisoft.exampleQuery.exceptions.UniqueException;
import com.polvisoft.exampleQuery.filter.FilterMap;
import com.polvisoft.exampleQuery.repository.Respository;
import com.polvisoft.exampleQuery.repository.utils.QueryBuilderHelper;
import com.polvisoft.exampleQuery.repository.utils.UtilsRepository;
import com.polvisoft.exampleQuery.utils.Utils;

public abstract class RepositoryImpl<DTO extends BasicDTO<?>> implements Respository<DTO> {

	@Autowired
	private EntityManager entityManager;

	private static Logger LOGGER = Logger.getLogger(RepositoryImpl.class);

	private final Class<DTO> voClass;

	public RepositoryImpl() {
		super();
		this.voClass = (Class<DTO>) ((ParameterizedType) this.getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
	}

	@Override
	public List<DTO> findAll() throws ExampleQueryException {
		return this.findByExample(null, null);
	}

	@Override
	public int countAll() throws ExampleQueryException {
		final String hqlString = "select count(*) from " + this.voClass.getName();
		LOGGER.debug("hqlString: " + hqlString);
		final Long result = (Long) this.entityManager.createQuery(hqlString).getSingleResult();
		return result.intValue();
	}

	@Override
	public DTO findByPk(final Object primaryKey) {
		return this.entityManager.find(this.voClass, primaryKey);
	}

	@Override
	public DTO findCustomByPk(final Object primaryKey, final String[] fields) throws ExampleQueryException {
		final String select = this.createCustomSelect(fields);
		final String from = this.createCustomFrom(fields);
		final String where = " where " + BasicDTO.PK + " = :" + BasicDTO.PK;
		final String hqlString = select + from + where;
		LOGGER.debug("hqlString: " + hqlString);
		final Query query = this.entityManager.createQuery(hqlString, Map.class);
		query.setParameter(BasicDTO.PK, primaryKey);
		final Map<String, Object> map = (Map<String, Object>) query.getSingleResult();
		final DTO result = this.convertToEntity(map);
		return result;
	}

	private String createCustomFrom(final String[] fields) {
		final String tableAlias = this.getTableAliasForClass(this.voClass);
		String from = " from " + this.voClass.getName() + " " + tableAlias;
		final int fieldsLength = fields.length;
		// TODO: detect duplicated fields to avoid problems
		for (int i = 0; i < fieldsLength; i++) {
			final String field = fields[i];
			final String fromForField = getFromForField(tableAlias, tableAlias + "." + field);
			if (!from.contains(fromForField)) {
				LOGGER.debug("From does not contains: " + fromForField);
				from += fromForField;
			}
		}
		LOGGER.debug("createCustomFromt: " + from);
		return from;
	}

	@Override
	public List<DTO> findByExample(final DTO example, final FilterMap filter) throws ExampleQueryException {
		final String tableAlias = this.getTableAliasForClass(this.voClass);
		final String select = "select " + tableAlias;
		final String from = " from " + this.voClass.getName() + " " + tableAlias;
		final Query query = this.createQueryForExample(example, filter, select, from);
		return query.getResultList();
	}

	@Override
	public List<DTO> findByExample(final DTO example, final FilterMap filter, final int pageNumber, final int pageSize)
			throws ExampleQueryException {
		final String tableAlias = this.getTableAliasForClass(this.voClass);
		final String select = "select " + tableAlias;
		final String from = " from " + this.voClass.getName() + " " + tableAlias;
		final Query query = this.createQueryForExample(example, filter, select, from);
		query.setFirstResult(pageSize * pageNumber);
		query.setMaxResults(pageNumber);
		return query.getResultList();
	}

	@Override
	public int countByExample(final DTO example, final FilterMap filter) throws ExampleQueryException {
		final String select = "select count(*) ";
		final String tableAlias = this.getTableAliasForClass(this.voClass);
		final String from = " from " + this.voClass.getName() + " " + tableAlias;
		final Query query = this.createQueryForExample(example, filter, select, from);
		final Long result = (Long) query.getSingleResult();
		return result.intValue();
	}

	@Override
	public List<DTO> findCustomByExample(final DTO example, final String[] fields, final FilterMap filter) throws ExampleQueryException {
		final String select = this.createCustomSelect(fields);
		final String from = this.createCustomFrom(fields);
		final Query query = this.createQueryForExample(example, filter, select, from);
		final List<Map<String, Object>> list = query.getResultList();
		final List<DTO> result = this.converToEntityList(list);
		return result;
	}

	@Override
	public List<DTO> findCustomByExample(final DTO example, final String[] fields, final FilterMap filter, final int pageNumber, final int pageSize)
			throws ExampleQueryException {
		final String select = this.createCustomSelect(fields);
		final String from = this.createCustomFrom(fields);
		final Query query = this.createQueryForExample(example, filter, select, from);
		query.setFirstResult(pageSize * pageNumber);
		query.setMaxResults(pageNumber);
		final List<Map<String, Object>> list = query.getResultList();
		final List<DTO> result = this.converToEntityList(list);
		return result;
	}

	protected List<DTO> converToEntityList(final List<Map<String, Object>> list) throws ExampleQueryException {
		final List<DTO> result = new ArrayList<>();
		for (final Map<String, Object> mapValues : list) {
			final DTO entity = this.convertToEntity(mapValues);
			result.add(entity);
		}
		return result;
	}

	protected DTO convertToEntity(final Map<String, Object> mapValues) throws ExampleQueryException {
		try {
			final DTO entity = this.voClass.newInstance();
			for (final Iterator<Entry<String, Object>> iterator = mapValues.entrySet().iterator(); iterator.hasNext();) {
				final Entry<String, Object> entry = iterator.next();
				try {
					final String fieldName = UtilsRepository.getFieldFromAlias(entry.getKey());
					Utils.setFieldValue(fieldName, entry.getValue(), entity);
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchFieldException | InstantiationException e) {
					throw new ExampleQueryException(e);
				}
			}
			return entity;
		} catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			throw new ExampleQueryException(e);
		}
	}

	@Override
	public boolean delete(final DTO entity) {
		boolean result = false;
		try {
			this.entityManager.remove(entity);
			this.entityManager.flush();
			result = true;
		} catch (final Exception e) {
			// TODO: implement
		}
		return result;
	}

	@Override
	public List<DTO> saveList(final List<DTO> list) throws UniqueException {
		final List<DTO> result = new ArrayList<>();
		for (final Iterator iterator = list.iterator(); iterator.hasNext();) {
			DTO vo = (DTO) iterator.next();
			vo = this.save(vo);
			result.add(vo);
		}
		return result;
	}

	@Override
	public List<DTO> updateList(final List<DTO> list) throws UniqueException {
		final List<DTO> result = new ArrayList<>();
		for (final Iterator iterator = list.iterator(); iterator.hasNext();) {
			DTO vo = (DTO) iterator.next();
			vo = this.update(vo);
			result.add(vo);
		}
		return result;
	}

	@Override
	public boolean deleteList(final List<DTO> list) {
		boolean result = true;
		for (final Iterator iterator = list.iterator(); iterator.hasNext();) {
			final DTO vo = (DTO) iterator.next();
			result &= this.delete(vo);
		}
		return result;
	}

	@Override
	public DTO save(final DTO entity) throws UniqueException {
		try {
			this.entityManager.persist(entity);
			this.entityManager.flush();
		} catch (final Exception e) {
			final String uniqueConstraintViolation = this.getConstraintNameViolation(e);
			if (StringUtils.isNotBlank(uniqueConstraintViolation)
					&& this.isUniqueConstraint(uniqueConstraintViolation)) {
				this.throwUniqueException(entity, uniqueConstraintViolation);
			} else {
				throw e;
			}
		}
		return entity;
	}

	protected void throwUniqueException(final DTO entity, final String uniqueConstraintViolation) throws UniqueException {
		final UniqueException uniqueException = new UniqueException(this.voClass, uniqueConstraintViolation, entity);
		LOGGER.error(uniqueException.getUniqueConstraint());
		LOGGER.error(uniqueException.getEntity().toStringDebug());
		throw uniqueException;
	}

	private boolean isUniqueConstraint(final String uniqueConstraintViolation) {
		final Table table = this.voClass.getAnnotation(Table.class);
		if (table != null) {
			for (int i = 0; i < table.uniqueConstraints().length; i++) {
				final UniqueConstraint uniqueConstraint = table.uniqueConstraints()[i];
				if (uniqueConstraint.name().equals(uniqueConstraintViolation)) {
					return true;
				}
			}
		}
		return false;
	}

	private String getConstraintNameViolation(final Exception e) {
		if (e instanceof PersistenceException) {
			final PersistenceException persistenceException = (PersistenceException) e;
			if (persistenceException.getCause() instanceof ConstraintViolationException) {
				final ConstraintViolationException cause = (ConstraintViolationException) persistenceException.getCause();
				return cause.getConstraintName();
			}
		}
		return null;
	}

	@Override
	public DTO update(final DTO entity) throws UniqueException {
		try {
			this.entityManager.merge(entity);
			this.entityManager.flush();
		} catch (final Exception e) {
			final String uniqueConstraintViolation = this.getConstraintNameViolation(e);
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

	public void setEntityManager(final EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	private String createCustomSelect(final String[] fields) {
		String select = "select new map ( ";
		final int fieldsLength = fields.length;
		final int lastField = fieldsLength;
		// TODO: detect duplicated fields to avoid problems
		for (int i = 0; i < fieldsLength; i++) {
			final String field = fields[i];
			final String entityField = this.getLastField(field);
			final String entityAlias = this.getTableAliasForField(field);
			final String fieldAlias = UtilsRepository.getAliasForField(field);
			select += entityAlias + "." + entityField + " as " + fieldAlias + "";
			if (i != lastField - 1) {
				select += ", ";
			}
		}
		select += ")";
		LOGGER.debug("createCustomSelect: " + select);
		return select;
	}

	public static String getFromForField(final String table, final String fieldForQuery) {
		LOGGER.debug("getFromForField");
		LOGGER.debug("table: " + table);
		LOGGER.debug("fieldForquery: " + fieldForQuery);
		String lastTable = table;
		final String[] fields = fieldForQuery.split("\\.");
		String from = "";
		for (int i = 1; i < fields.length - 1; i++) {
			final String field = fields[i];
			final String nextJoin = " join " + lastTable + "." + field + " " + field + " ";
			LOGGER.debug("nextJoin =" + nextJoin);
			from += nextJoin;
			lastTable = field;
		}
		LOGGER.debug("FROM FOR FIELD: " + from);
		return from;
	}

	private Query createQueryForExample(final DTO example, final FilterMap filter, final String select, final String from)
			throws ExampleQueryException {
		return this.createQueryForExample(example, filter, select, from, "");
	}

	private Query createQueryForExample(final DTO example, final FilterMap filter, final String select, final String from, final String where)
			throws ExampleQueryException {
		final String tableAlias = this.getTableAliasForClass(this.voClass);
		final Map<String, Object> parameters = new HashMap<>();
		QueryBuilderHelper builderHelper = new QueryBuilderHelper(select, from, where, parameters);
		// try {
		builderHelper = this.buildQueryForFilterMap(example, filter, tableAlias, builderHelper);
		LOGGER.info("HQL STRING: " + builderHelper.getHqlString());
		final Query query = this.entityManager.createQuery(builderHelper.getHqlString());
		this.setQueryParams(query, builderHelper.getParameters());
		return query;
	}

	protected QueryBuilderHelper buildQueryForFilterMap(final DTO example, final FilterMap filter, final String tableAlias,
			final QueryBuilderHelper builderHelper) throws ExampleQueryException {
		final QueryBuilderHelper result = new QueryBuilderHelper(builderHelper.getSelect(), builderHelper.getFrom(),
				builderHelper.getWhere(), builderHelper.getParameters());
		if (filter != null) {
			for (final Iterator<Entry<String, Object>> iterator = filter.getMap().entrySet().iterator(); iterator
					.hasNext();) {
				final Entry<String, Object> type = iterator.next();
				if (type.getValue() instanceof FilterMap) {
					final FilterMap filterMap = (FilterMap) type.getValue();
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
					final HqlConditions condition = (HqlConditions) type.getValue();
					final String filterField = type.getKey();
					try {
						final Object valueForQuery = Utils.getFieldValue(example, filterField, true);
						final boolean applyValue = UtilsRepository.hasToApplyConditionForQuery(condition, valueForQuery);
						if (applyValue) {
							final String fieldForQuery = UtilsRepository.getFieldForQuery(example, filterField);
							final String lastTableAlias = this.getTableAliasForField(fieldForQuery);
							result.setFrom(this.applyFieldToForm(example, result.getFrom(), tableAlias, filterField,
									lastTableAlias));
							final String nameForParameter = UtilsRepository.getNameForParameter(filterField, condition);
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

	protected void applyFieldToParameters(final Map<String, Object> parameters, final HqlConditions condition, final Object valueForQuery,
			final String nameForParameter) {
		if (nameForParameter != null) {
			final Object fixedValueForQuery = UtilsRepository.fixValueForQuery(valueForQuery, condition);
			parameters.put(nameForParameter, fixedValueForQuery);
		}
	}

	protected String applyFieldToWhere(final FilterMap filter, String where, final HqlConditions condition, final String fieldForQuery,
			final String lastTableAlias, final String nameForParameter) {
		final String lastField = this.getLastField(fieldForQuery);
		if (where.equals("")) {
			where += UtilsRepository.getClauseCondition(lastTableAlias, lastField, condition, nameForParameter, null);
		} else {
			where += UtilsRepository.getClauseCondition(lastTableAlias, lastField, condition, nameForParameter,
					filter.getFilterAddCondition());
		}
		return where;
	}

	protected String applyFieldToForm(final DTO example, String from, final String tableAlias, final String filterField,
			final String lastTableAlias) throws ExampleQueryException {
		String fromForField = null;
		try {
			if (FilterForFieldReader.isAnnotatedField(filterField, example)) {
				final String referencedField = FilterForFieldReader.getValue(filterField, example);
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

	private String getLastField(final String fieldForQuery) {
		return fieldForQuery.substring(fieldForQuery.lastIndexOf(".") + 1);
	}

	private String getTableAliasForClass(final Class<DTO> entity) {
		return entity.getSimpleName().substring(0, 1).toLowerCase() + entity.getSimpleName().substring(1);
	}

	private String getTableAliasForField(final String fieldForQuery) {
		String result = null;
		final String[] split = fieldForQuery.split("\\.");
		if (split.length > 1) {
			result = split[split.length - 2];
		} else {
			result = this.getTableAliasForClass(this.voClass);
		}
		LOGGER.debug("LAST TABLE ALIAS: of " + fieldForQuery + " is " + result);
		return result;
	}

	private void setQueryParams(final Query query, final Map<String, Object> parameters) {
		for (final Iterator<Entry<String, Object>> iterator = parameters.entrySet().iterator(); iterator.hasNext();) {
			final Entry<String, Object> parameter = iterator.next();
			final String parameterKey = parameter.getKey();
			final Object parameterValue = parameter.getValue();
			LOGGER.debug("parameter: \"" + parameterKey + "\"\tvalue: \"" + parameterValue + "\"");
			query.setParameter(parameterKey, parameterValue);
		}
	}

}
