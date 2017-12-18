package com.polvisoft.exampleQuery.test.common;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

import javax.persistence.EntityManager;

import com.polvisoft.exampleQuery.domain.BasicDTO;
import com.polvisoft.exampleQuery.exceptions.ExampleQueryException;
import com.polvisoft.exampleQuery.exceptions.UniqueException;
import com.polvisoft.exampleQuery.filter.FilterMap;
import com.polvisoft.exampleQuery.repository.impl.RepositoryImpl;

/**
 * The abstract Class Given.
 * 
 * That class represent the instance of all the elements necessaries for the
 * corresponding test class of an DTO
 *
 * @param <DTO>
 *            the generic type for our VOs
 * @param <RepositoryDTO>
 *            the generic repository type for our VOs
 */
// TODO: Given must focus on new specific MethodGiven class
public abstract class Given<DTO extends BasicDTO<?>, RepositoryDTO extends RepositoryImpl<DTO>> {

	/** The entity manager. */
	protected EntityManager entityManager;

	/** The repository class. */
	protected Class repositoryClass;

	/** The repository. */
	protected RepositoryDTO repository;

	/**
	 * It sets up an examples environment for find by example test cases.
	 *
	 * @throws UniqueException
	 *             the unique exception
	 * @throws InstantiationException
	 *             the instantiation exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws ExampleQueryException
	 */
	public abstract void givenExamplesEnvironment() throws UniqueException, ExampleQueryException;

	/**
	 * Return the select custom fields for custom select test cases
	 *
	 * @return the string[]
	 */
	public abstract String[] initCustomFields();

	/**
	 * Return the examples for find by example test cases.
	 *
	 * @return the vo[]
	 * @throws UniqueException
	 *             the unique exception
	 * @throws ExampleQueryException
	 * @throws InstantiationException
	 *             the instantiation exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 */
	public abstract DTO[] initExamples() throws UniqueException, ExampleQueryException;

	/**
	 * Returns the object for save-update-delete test case.
	 *
	 * @return the vo
	 * @throws UniqueException
	 *             the unique exception
	 * @throws InstantiationException
	 *             the instantiation exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws ExampleQueryException
	 */
	public abstract DTO initTestSaveInstance() throws UniqueException, ExampleQueryException;

	/**
	 * Returns the filter for find by example test cases.
	 *
	 * @return the map
	 */
	public abstract FilterMap initFilter();

	/**
	 * Returns the fields and values for testUpdate case.
	 *
	 * @return the map
	 * @throws ExampleQueryException
	 */
	public abstract Map<String, Object> initTestUpdateValues() throws ExampleQueryException;

	public abstract int initPageNumber();

	public abstract int initPageSize();

	/**
	 * Instantiates a new given for DTO.
	 *
	 * @param entityManager
	 *            the entity manager
	 * @throws InstantiationException
	 *             the instantiation exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 */
	public Given(EntityManager entityManager) throws ExampleQueryException {
		this.entityManager = entityManager;
		this.repositoryClass = (Class<RepositoryDTO>) ((ParameterizedType) this.getClass().getGenericSuperclass())
				.getActualTypeArguments()[1];
		try {
			this.repository = (RepositoryDTO) this.repositoryClass.newInstance();
			this.repository.setEntityManager(entityManager);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new ExampleQueryException(e);
		}
	}
}
