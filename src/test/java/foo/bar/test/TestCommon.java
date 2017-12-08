package foo.bar.test;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
import foo.bar.service.impl.ServiceImpl;
import foo.bar.service.utils.HqlConditions;
import foo.bar.test.given.Given;
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

	private Map<String, HqlConditions> filter;

	private VO[] examples;

	private String[] customFields;

	public TestCommon() {
		logGivenEnvironmentSubLine();
		this.serviceVoClass = (Class<ServiceVO>) ((ParameterizedType) this.getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
		this.voClass = (Class<VO>) ((ParameterizedType) this.getClass().getGenericSuperclass())
				.getActualTypeArguments()[1];
		this.givenVoClass = (Class<GivenVO>) ((ParameterizedType) this.getClass().getGenericSuperclass())
				.getActualTypeArguments()[2];
		LOGGER.info("Unit Test Class: " + this.getClass().getName());
		LOGGER.info("Entity: " + this.voClass.getName());
		LOGGER.info("Service: " + this.serviceVoClass.getName());
		LOGGER.info("Service: " + this.serviceVoClass.getName());
		logGivenEnvironmentSubLine();
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		logGivenEnvironmentLine();
		LOGGER.info("setUpBeforeClass");
		logGivenEnvironmentLine();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		logGivenEnvironmentLine();
		LOGGER.info("tearDownAfterClass");
		logGivenEnvironmentLine();
	}

	@Before
	public void setUp() throws InstantiationException, IllegalAccessException, UniqueException, NoSuchMethodException,
			SecurityException, IllegalArgumentException, InvocationTargetException {
		logGivenEnvironmentSubLine();
		LOGGER.info("setUp");
		this.service = (ServiceImpl<VO>) serviceVoClass.newInstance();
		this.service.setEntityManager(this.entityManager);
		Constructor constructor = givenVoClass.getConstructor(EntityManager.class);
		this.given = (GivenVO) constructor.newInstance(this.entityManager);
		logGivenEnvironmentSubLine();
	}

	@After
	public void tearDown() throws Exception {
		logGivenEnvironmentSubLine();
		LOGGER.info("tearDown");
		logGivenEnvironmentSubLine();
	}

	@Test
	public void testEntityConstructor() throws NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		logGivenEnvironmentSubLine();
		LOGGER.info("testEntityConstructor");
		Map<String, Object> mapValues = this.given.initEntityFields();
		Constructor constructor = voClass.getConstructor(HashMap.class);
		VO entity = (VO) constructor.newInstance((Map) mapValues);
		LOGGER.info("Instance has been created with map values: " + mapValues);
		LOGGER.info("Instance: " + entity.toStringDebug());
		assertTrue("Instance has been created with map values: " + mapValues, entity != null);
		logGivenEnvironmentSubLine();
	}

	@Test
	public void testSaveUpdateAndDelete()
			throws UniqueException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchFieldException, InstantiationException {
		logGivenEnvironmentSubLine();
		LOGGER.info("testSaveUpdateAndDelete");
		VO entity = this.testSave();
		entity = this.testUpdate(entity);
		boolean testCompleted = this.testDelete(entity);
		LOGGER.info("testSaveUpdateAndDelete completed " + testCompleted);
		assertTrue("testSaveUpdateAndDelete completed ", testCompleted);
		logGivenEnvironmentSubLine();
	}

	public VO testSave() throws UniqueException, InstantiationException, IllegalAccessException {
		logGivenEnvironmentSubLine();
		LOGGER.info("testSave");
		VO entity = this.given.initTestSaveInstance();
		entity = service.save(entity);
		LOGGER.info("Save successfull is " + (entity != null && entity.getPk() != null));
		assertTrue("Save successfull", entity != null && entity.getPk() != null);
		logGivenEnvironmentSubLine();
		return entity;
	}

	private VO testUpdate(VO entity) throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchFieldException, InstantiationException {
		logGivenEnvironmentSubLine();
		LOGGER.info("testUpdate");
		boolean updated = false;
		Map<String, Object> mapValues = this.given.initTestUpdateValues();
		for (Iterator<Entry<String, Object>> iterator = mapValues.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, Object> entry = iterator.next();
			String fieldName = entry.getKey();
			Object newValue = entry.getValue();
			Object originalValue = Utils.getFieldValue(entity, fieldName);
			Utils.setFieldValue(fieldName, newValue, entity);
			LOGGER.info("Entity updated: Field \"" + fieldName + "\" (" + originalValue + ") => (" + newValue + ")");
			updated |= (newValue != originalValue);
		}
		VO updatedEntity = this.service.update(entity);
		LOGGER.info("Entity updated: is " + updated);
		assertTrue("Entity updated", updated);
		logGivenEnvironmentSubLine();
		return updatedEntity;

	}

	public boolean testDelete(VO entity) {
		boolean result = false;
		logGivenEnvironmentSubLine();
		LOGGER.info("testDelete at class: " + this.getClass().getName());
		if (entity != null) {
			result = service.delete(entity);
			assertTrue("Delete successfull", result);
		} else {
			assertTrue("Can't test delete", false);
		}
		logGivenEnvironmentSubLine();
		return result;
	}

	// TODO: test for unique exception

	@Test
	public void testFindAll()
			throws InstantiationException, IllegalAccessException, ExampleQueryException, UniqueException {
		logGivenEnvironmentSubLine();
		LOGGER.info("testFindAll at class: " + this.getClass().getName());
		logGivenEnvironmentSubLine();

		initSetupEnvironmentExamples();

		logGivenEnvironmentSubLine();
		List<VO> result = service.findAll();
		LOGGER.info("Test findAll returns more than zero: " + result.size());
		assertTrue("Test findAll returns more than zero: ", !result.isEmpty());
		logGivenEnvironmentSubLine();
	}

	@Test
	public void testCountAll()
			throws InstantiationException, IllegalAccessException, ExampleQueryException, UniqueException {
		logGivenEnvironmentSubLine();
		LOGGER.info("testCountAll at class: " + this.getClass().getName());
		logGivenEnvironmentSubLine();

		initSetupEnvironmentExamples();

		logGivenEnvironmentSubLine();
		int result = service.countAll();
		LOGGER.info("Count all returns more than zero: " + result);
		assertTrue("Count all returns more than zero: " + result, result > 0);
		logGivenEnvironmentSubLine();
	}

	@Test
	public void testFindByExample()
			throws InstantiationException, ExampleQueryException, UniqueException, IllegalAccessException {
		logGivenEnvironmentSubLine();
		LOGGER.info("testFindByExample at class: " + this.getClass().getName());
		setupExamplesQueryEnvironment();
		for (int i = 0; i < this.examples.length; i++) {
			VO example = this.examples[i];
			List<VO> result = service.findByExample(example, this.filter);
			showResult(example, result, i, this.filter);
			LOGGER.info("findByExample returns more than zero: " + result.size());
			assertTrue("findByExample returns more than zero: ", !result.isEmpty());
		}
		logGivenEnvironmentSubLine();
	}

	private void initSetupEnvironmentExamples() throws InstantiationException, IllegalAccessException, UniqueException {
		this.given.givenExamplesEnvironment();
	}

	protected void setupExamplesQueryEnvironment()
			throws UniqueException, InstantiationException, IllegalAccessException {
		logGivenEnvironmentSubLine();
		LOGGER.info("setupExamplesQueryEnvironment");
		this.initSetupEnvironmentExamples();
		this.customFields = this.given.initCustomFields();
		assertTrue("Custom fields has been initializated", this.customFields != null && this.customFields.length > 0);
		this.filter = this.given.initFilter();
		assertTrue("Filters has been initializated", this.filter != null && this.filter.size() > 0);
		this.examples = this.given.initExamples();
		assertTrue("Examples has been initializated", this.examples != null && this.examples.length > 0);
		logGivenEnvironmentSubLine();
	}

	@Test
	public void testCountByExample()
			throws InstantiationException, ExampleQueryException, UniqueException, IllegalAccessException {
		logGivenEnvironmentSubLine();
		LOGGER.info("testCountByExample at class: " + this.getClass().getName());
		logGivenEnvironmentSubLine();
		setupExamplesQueryEnvironment();
		for (int i = 0; i < this.examples.length; i++) {
			VO example = this.examples[i];
			Integer result = service.countByExample(example, this.filter);
			LOGGER.info("Count by example returns more than zero: " + result);
			assertTrue("Count by example returns more than zero: " + result, result > 0);
		}
		logGivenEnvironmentSubLine();
	}

	@Test
	public void findCustomByExample()
			throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, ExampleQueryException, UniqueException {
		LOGGER.info("Find Custom by example " + this.getClass().getName());
		logGivenEnvironmentSubLine();

		setupExamplesQueryEnvironment();

		logGivenEnvironmentSubLine();
		for (int i = 0; i < this.examples.length; i++) {
			logGivenEnvironmentSubLine();
			VO example = this.examples[i];
			LOGGER.info("example: " + (i + 1));
			List<VO> result = service.findCustomByExample(example, this.customFields, this.filter);
			showResult(example, result, i, this.filter);
			LOGGER.info("findCustomByExample returns more than zero: " + result.size());
			logEmptyLine();
			assertTrue("findCustomByExample returns more than zero: " + result, !result.isEmpty());
		}
	}

	private void logEmptyLine() {
		LOGGER.info("");
	}

	private void showResult(VO example, List<VO> result, int exampleIndex, Map<String, HqlConditions> filters) {
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

	protected static void logGivenEnvironmentLine() {
		LOGGER.info("*************************************************"
				+ "***************************************************"
				+ "***************************************************"
				+ "***************************************************");
	}

	protected static void logGivenEnvironmentSubLine() {
		LOGGER.info("-------------------------------------------------"
				+ "---------------------------------------------------"
				+ "---------------------------------------------------"
				+ "---------------------------------------------------");
	}

	protected void logGivenEnvironmentStart() {
		logGivenEnvironmentSubLine();
		LOGGER.info("Given enviroment for " + this.getClass().getName());
		logGivenEnvironmentSubLine();
	}
}
