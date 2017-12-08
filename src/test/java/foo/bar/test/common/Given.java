package foo.bar.test.common;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

import javax.persistence.EntityManager;

import foo.bar.domain.BasicVO;
import foo.bar.exceptions.UniqueException;
import foo.bar.service.impl.ServiceImpl;
import foo.bar.service.utils.HqlConditions;

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
	 */
	public abstract void givenExamplesEnvironment()
			throws UniqueException, InstantiationException, IllegalAccessException;

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
	 * @throws InstantiationException
	 *             the instantiation exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 */
	public abstract VO[] initExamples() throws UniqueException, InstantiationException, IllegalAccessException;

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
	 */
	public abstract VO initTestSaveInstance() throws UniqueException, InstantiationException, IllegalAccessException;

	/**
	 * Returns the filter for find by example test cases.
	 *
	 * @return the map
	 */
	public abstract Map<String, HqlConditions> initFilter();

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
	 */
	public abstract Map<String, Object> initTestUpdateValues();

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
	public Given(EntityManager entityManager) throws InstantiationException, IllegalAccessException {
		this.entityManager = entityManager;
		this.serviceVoClass = (Class<ServiceVO>) ((ParameterizedType) this.getClass().getGenericSuperclass())
				.getActualTypeArguments()[1];
		this.service = (ServiceVO) this.serviceVoClass.newInstance();
		this.service.setEntityManager(entityManager);
	}
}
