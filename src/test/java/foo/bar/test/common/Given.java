package foo.bar.test.common;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

import javax.persistence.EntityManager;

import foo.bar.domain.BasicVO;
import foo.bar.exceptions.ExampleQueryException;
import foo.bar.exceptions.UniqueException;
import foo.bar.filter.FilterMap;
import foo.bar.service.impl.ServiceImpl;

/**
 * The abstract Class Given.
 * 
 * That class represent the instance of all the elements necessaries for the
 * corresponding test class of an VO
 *
 * @param <VO>
 *            the generic type for our VOs
 * @param <ServiceVO>
 *            the generic service type for our VOs
 */
// TODO: Given must focus on new specific MethodGiven class
public abstract class Given<VO extends BasicVO<?>, ServiceVO extends ServiceImpl<VO>> {

	/** The entity manager. */
	protected EntityManager entityManager;

	/** The service vo class. */
	protected Class serviceVoClass;

	/** The service. */
	protected ServiceVO service;

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
	public abstract VO[] initExamples() throws UniqueException, ExampleQueryException;

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
	public abstract VO initTestSaveInstance() throws UniqueException, ExampleQueryException;

	/**
	 * Returns the filter for find by example test cases.
	 *
	 * @return the map
	 */
	public abstract FilterMap initFilter();

	/**
	 * Return the fields that we want to use for entity creation with map of
	 * string objects.
	 *
	 * @return the map
	 */
	public abstract Map<String, Object> initEntityFields();

	/**
	 * Returns the fields and values for testUpdate case.
	 *
	 * @return the map
	 * @throws ExampleQueryException
	 */
	public abstract Map<String, Object> initTestUpdateValues() throws ExampleQueryException;

	/**
	 * Instantiates a new given for VO.
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
		this.serviceVoClass = (Class<ServiceVO>) ((ParameterizedType) this.getClass().getGenericSuperclass())
				.getActualTypeArguments()[1];
		try {
			this.service = (ServiceVO) this.serviceVoClass.newInstance();
			this.service.setEntityManager(entityManager);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new ExampleQueryException(e);
		}
	}
}
