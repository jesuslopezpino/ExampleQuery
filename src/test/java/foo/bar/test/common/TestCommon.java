package foo.bar.test.common;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import foo.bar.domain.BasicVO;
import foo.bar.exceptions.ExampleQueryException;
import foo.bar.exceptions.UniqueException;
import foo.bar.filter.FilterMap;
import foo.bar.service.impl.ServiceImpl;
import foo.bar.utils.Utils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@Transactional
// TODO: add DTO
public abstract class TestCommon<ServiceVO extends ServiceImpl<VO>, VO extends BasicVO<?>, GivenVO extends Given<VO, ServiceVO>> {

	protected static Logger LOGGER = Logger.getLogger(TestCommon.class);

	@PersistenceContext
	protected EntityManager entityManager;

	private ServiceImpl<VO> service;

	protected GivenVO given;

	private Class voClass;

	private Class serviceVoClass;

	private Class givenVoClass;

	private FilterMap filter;

	private VO[] examples;

	private String[] customFields;

	public TestCommon() {
		logLine();
		this.serviceVoClass = (Class<ServiceVO>) ((ParameterizedType) this.getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
		this.voClass = (Class<VO>) ((ParameterizedType) this.getClass().getGenericSuperclass())
				.getActualTypeArguments()[1];
		this.givenVoClass = (Class<GivenVO>) ((ParameterizedType) this.getClass().getGenericSuperclass())
				.getActualTypeArguments()[2];
		LOGGER.info("Unit Test Class: " + this.getClass().getName());
		LOGGER.info("Entity: " + this.voClass.getName());
		LOGGER.info("Service: " + this.serviceVoClass.getName());
		LOGGER.info("Given: " + this.givenVoClass.getName());
		logLine();
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		logAsteriscLine();
		LOGGER.info("setUpBeforeClass");
		logAsteriscLine();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		logAsteriscLine();
		LOGGER.info("tearDownAfterClass");
		logAsteriscLine();
	}

	@Before
	public void setUp() throws InstantiationException, IllegalAccessException, UniqueException, NoSuchMethodException,
			SecurityException, IllegalArgumentException, InvocationTargetException {
		logLine();
		LOGGER.info("setUp");
		this.service = (ServiceImpl<VO>) this.serviceVoClass.newInstance();
		this.service.setEntityManager(this.entityManager);
		Constructor constructor = this.givenVoClass.getConstructor(EntityManager.class);
		this.given = (GivenVO) constructor.newInstance(this.entityManager);
		logLine();
	}

	@After
	public void tearDown() throws Exception {
		logLine();
		LOGGER.info("tearDown");
		logLine();
	}

	@Test
	public void testUniqueException() throws InstantiationException, IllegalAccessException {
		logLine();
		LOGGER.info("testUniqueException");
		Table table = (Table) this.voClass.getAnnotation(Table.class);
		UniqueConstraint[] uniqueConstraints = table.uniqueConstraints();
		if (uniqueConstraints != null && uniqueConstraints.length > 0) {
			try {
				LOGGER.info("trying first save test");
				this.testSave();
				LOGGER.info("trying second save test");
				this.testSave();
			} catch (UniqueException e) {
				LOGGER.info(this.service.getClass() + " returns UniqueException");
				assertTrue(this.service.getClass() + " returns UniqueException", true);
			}
		} else {
			LOGGER.info(this.voClass.getName() + " doesn't have unique constraints");
			assertTrue(this.voClass.getName() + " doesn't have unique constraints", true);
		}
		logLine();
	}

	@Test
	public void testEntityConstructor() throws NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		logLine();
		LOGGER.info("testEntityConstructor");
		Map<String, Object> mapValues = this.given.initEntityFields();
		Constructor constructor = this.voClass.getConstructor(Map.class);
		VO entity = (VO) constructor.newInstance((Map) mapValues);
		LOGGER.info("Instance has been created with map values: " + mapValues);
		LOGGER.info("Instance: " + entity.toStringDebug());
		assertTrue("Instance has been created with map values: " + mapValues, entity != null);
		logLine();
	}

	@Test
	public void testSaveUpdateFindByPkAndDelete()
			throws UniqueException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchFieldException, InstantiationException {
		logLine();
		LOGGER.info("testSaveUpdateFindByPkAndDelete");
		VO entity = this.testSave();
		entity = this.testUpdate(entity);
		VO findByPkEntity = this.testFindByPk(entity);
		boolean testCompleted = this.testDelete(findByPkEntity);
		LOGGER.info("testSaveUpdateFindByPkAndDelete completed " + testCompleted);
		assertTrue("testSaveUpdateFindByPkAndDelete completed ", testCompleted);
		logLine();
	}

	protected VO testFindByPk(VO entity) {
		VO findByPkEntity = this.service.findByPk(entity.getPk());
		LOGGER.info("Entity find by pk is found: " + findByPkEntity);
		assertTrue("Entity find by pk is found", findByPkEntity != null);
		return findByPkEntity;
	}

	protected VO testSave() throws UniqueException, InstantiationException, IllegalAccessException {
		logLine();
		LOGGER.info("testSave");
		VO entity = this.given.initTestSaveInstance();
		entity = this.service.save(entity);
		LOGGER.info("Save successfull is " + (entity != null && entity.getPk() != null));
		assertTrue("Save successfull", entity != null && entity.getPk() != null);
		logLine();
		return entity;
	}

	protected VO testUpdate(VO entity) throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchFieldException, InstantiationException {
		logLine();
		LOGGER.info("testUpdate");
		boolean updated = true;
		Map<String, Object> mapValues = this.given.initTestUpdateValues();
		for (Iterator<Entry<String, Object>> iterator = mapValues.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, Object> entry = iterator.next();
			String fieldName = entry.getKey();
			Object newValue = entry.getValue();
			updated &= this.updateFieldAtEntity(entity, fieldName, newValue);
		}
		VO updatedEntity = this.service.update(entity);
		LOGGER.info("Entity updated: is " + updated);
		assertTrue("Entity updated", updated);
		logLine();
		return updatedEntity;

	}

	protected boolean updateFieldAtEntity(VO entity, String fieldName, Object newValue) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, NoSuchFieldException, InstantiationException {
		Object originalValue = Utils.getFieldValue(entity, fieldName);
		Utils.setFieldValue(fieldName, newValue, entity);
		LOGGER.info("Entity updated: Field \"" + fieldName + "\" (" + originalValue + ") => (" + newValue + ")");
		return (newValue != originalValue);
	}

	protected boolean testDelete(VO entity) {
		boolean result = false;
		logLine();
		LOGGER.info("testDelete at class: " + this.getClass().getName());
		if (entity != null) {
			result = this.service.delete(entity);
			assertTrue("Delete successfull", result);
		} else {
			assertTrue("Can't test delete", false);
		}
		logLine();
		return result;
	}

	// TODO: test for unique exception

	@Test
	public void testFindAll()
			throws InstantiationException, IllegalAccessException, ExampleQueryException, UniqueException {
		logLine();
		LOGGER.info("testFindAll at class: " + this.getClass().getName());
		logLine();

		this.initSetupEnvironmentExamples();

		logLine();
		List<VO> result = this.service.findAll();
		LOGGER.info("Test findAll returns more than zero: " + result.size());
		assertTrue("Test findAll returns more than zero: ", !result.isEmpty());
		logLine();
	}

	@Test
	public void testCountAll()
			throws InstantiationException, IllegalAccessException, ExampleQueryException, UniqueException {
		logLine();
		LOGGER.info("testCountAll at class: " + this.getClass().getName());
		logLine();

		this.initSetupEnvironmentExamples();

		logLine();
		int result = this.service.countAll();
		LOGGER.info("Count all returns more than zero: " + result);
		assertTrue("Count all returns more than zero: " + result, result > 0);
		logLine();
	}

	@Test
	public void testFindByExample()
			throws InstantiationException, ExampleQueryException, UniqueException, IllegalAccessException {
		logLine();
		LOGGER.info("testFindByExample at class: " + this.getClass().getName());
		this.setupExamplesQueryEnvironment();
		for (int i = 0; i < this.examples.length; i++) {
			VO example = this.examples[i];
			List<VO> result = this.service.findByExample(example, this.filter);
			this.showResult(example, result, i, this.filter);
			LOGGER.info("findByExample returns more than zero: " + result.size());
			assertTrue("findByExample returns more than zero: ", !result.isEmpty());
		}
		logLine();
	}

	private void initSetupEnvironmentExamples() throws InstantiationException, IllegalAccessException, UniqueException {
		this.logGivenEnvironmentStart();
		this.given.givenExamplesEnvironment();
		LOGGER.info("Example environment ends");
	}

	protected void setupExamplesQueryEnvironment()
			throws UniqueException, InstantiationException, IllegalAccessException {
		logLine();
		LOGGER.info("setupExamplesQueryEnvironment");
		this.initSetupEnvironmentExamples();
		this.customFields = this.given.initCustomFields();
		assertTrue("Custom fields has been initializated", this.customFields != null && this.customFields.length > 0);
		this.filter = this.given.initFilter();
		assertTrue("Filters has been initializated", this.filter != null && this.filter.getMap().size() > 0);
		this.examples = this.given.initExamples();
		assertTrue("Examples has been initializated", this.examples != null && this.examples.length > 0);
		logLine();
	}

	@Test
	public void testCountByExample()
			throws InstantiationException, ExampleQueryException, UniqueException, IllegalAccessException {
		logLine();
		LOGGER.info("testCountByExample at class: " + this.getClass().getName());
		logLine();
		this.setupExamplesQueryEnvironment();
		LOGGER.info("Count by example filters: " + this.filter);
		for (int i = 0; i < this.examples.length; i++) {
			VO example = this.examples[i];
			Integer result = this.service.countByExample(example, this.filter);
			LOGGER.info("Count by example returns more than zero: " + result + " Example: " + example.toStringDebug());
			assertTrue("Count by example returns more than zero: " + result, result > 0);
		}
		logLine();
	}

	@Test
	public void testFindCustomByPk() throws InstantiationException, IllegalAccessException, UniqueException,
			NoSuchMethodException, InvocationTargetException {
		logLine();
		this.customFields = this.given.initCustomFields();
		VO entity = this.testSave();
		VO result = this.service.findCustomByPk(entity.getPk(), this.customFields);
		LOGGER.info("testFindCustomByPk returns the searched element: " + result != null);
		assertTrue("testFindCustomByPk returns the searched element: " + (result != null), result != null);
		logLine();
	}

	@Test
	public void findCustomByExample()
			throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, ExampleQueryException, UniqueException {
		LOGGER.info("Find Custom by example " + this.getClass().getName());
		logLine();
		this.setupExamplesQueryEnvironment();
		for (int i = 0; i < this.examples.length; i++) {
			logLine();
			VO example = this.examples[i];
			LOGGER.info("example: " + (i + 1));
			List<VO> result = this.service.findCustomByExample(example, this.customFields, this.filter);
			this.showResult(example, result, i, this.filter);
			LOGGER.info("findCustomByExample returns more than zero: " + result.size());
			this.logEmptyLine();
			assertTrue("findCustomByExample returns more than zero: " + result, !result.isEmpty());
		}
		logLine();
	}

	private void logEmptyLine() {
		LOGGER.info("");
	}

	private void showResult(VO example, List<VO> result, int exampleIndex, FilterMap filters) {
		if (result.isEmpty()) {
			LOGGER.error("FAIL AT SAMPLE: " + (exampleIndex + 1));
			LOGGER.error("Example object: " + example.toStringDebug());
			LOGGER.error("With filters: " + filters);
		} else {
			LOGGER.info("Example object: " + example.toStringDebug());
			LOGGER.info("With filters: " + filters);
			LOGGER.info("Returns " + result.size() + " items");
			for (VO vo : result) {
				LOGGER.info("item[" + exampleIndex + "]: " + vo.toStringDebug());
			}
		}
	}

	private static void logAsteriscLine() {
		LOGGER.info("*************************************************"
				+ "***************************************************"
				+ "***************************************************"
				+ "***************************************************");
	}

	private static void logLine() {
		LOGGER.info("-------------------------------------------------"
				+ "---------------------------------------------------"
				+ "---------------------------------------------------"
				+ "---------------------------------------------------");
	}

	private void logGivenEnvironmentStart() {
		logLine();
		LOGGER.info("Given enviroment for " + this.getClass().getName());
		logLine();
	}
}
