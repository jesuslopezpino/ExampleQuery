package foo.bar.test;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		logGivenEnviromentSubLine();
		this.serviceVoClass = (Class<ServiceVO>) ((ParameterizedType) this.getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
		this.voClass = (Class<ServiceVO>) ((ParameterizedType) this.getClass().getGenericSuperclass())
				.getActualTypeArguments()[1];
		this.givenVoClass = (Class<GivenVO>) ((ParameterizedType) this.getClass().getGenericSuperclass())
				.getActualTypeArguments()[2];
		LOGGER.info("Unit Test Class: " + this.getClass().getName());
		LOGGER.info("Entity: " + this.voClass.getName());
		LOGGER.info("Service: " + this.serviceVoClass.getName());
		LOGGER.info("Service: " + this.serviceVoClass.getName());
		logGivenEnviromentSubLine();
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		logGivenEnviromentLine();
		LOGGER.info("setUpBeforeClass");
		logGivenEnviromentLine();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		logGivenEnviromentLine();
		LOGGER.info("tearDownAfterClass");
		logGivenEnviromentLine();
	}

	@Before
	public void setUp() throws InstantiationException, IllegalAccessException, UniqueException, NoSuchMethodException,
			SecurityException, IllegalArgumentException, InvocationTargetException {
		logGivenEnviromentSubLine();
		LOGGER.info("setUp");
		this.service = (ServiceImpl<VO>) serviceVoClass.newInstance();
		this.service.setEntityManager(this.entityManager);
		Constructor constructor = givenVoClass.getConstructor(EntityManager.class);
		this.given = (GivenVO) constructor.newInstance(this.entityManager);
		logGivenEnviromentSubLine();
	}

	@After
	public void tearDown() throws Exception {
		logGivenEnviromentSubLine();
		LOGGER.info("tearDown");
		logGivenEnviromentSubLine();
	}

	@Test
	public void testEntityConstructor() throws NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		logGivenEnviromentSubLine();
		LOGGER.info("testEntityConstructor");
		Map<String, Object> mapValues = this.given.initEntityFields();
		Constructor constructor = voClass.getConstructor(HashMap.class);
		VO entity = (VO) constructor.newInstance((Map) mapValues);
		LOGGER.info("Instance has been created with map values: " + mapValues);
		LOGGER.info("Instance: " + entity.toStringDebug());
		assertTrue("Instance has been created with map values: " + mapValues, entity != null);
		logGivenEnviromentSubLine();
	}

	@Test
	public void testSaveUpdateAndDelete()
			throws UniqueException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchFieldException, InstantiationException {
		logGivenEnviromentSubLine();
		LOGGER.info("testSaveUpdateAndDelete");
		VO entity = this.testSave();
		entity = this.testUpdate(entity);
		boolean testCompleted = this.testDelete(entity);
		LOGGER.info("testSaveUpdateAndDelete completed " + testCompleted);
		assertTrue("testSaveUpdateAndDelete completed ", testCompleted);
		logGivenEnviromentSubLine();
	}

	public VO testSave() throws UniqueException, InstantiationException, IllegalAccessException {
		logGivenEnviromentSubLine();
		LOGGER.info("testSave");
		VO entity = this.given.initSaveEntity();
		entity = service.save(entity);
		LOGGER.info("Save successfull is " + (entity != null && entity.getPk() != null));
		assertTrue("Save successfull", entity != null && entity.getPk() != null);
		logGivenEnviromentSubLine();
		return entity;
	}

	private VO testUpdate(VO entity) throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchFieldException, InstantiationException {
		logGivenEnviromentSubLine();
		LOGGER.info("testUpdate");
		String field = this.given.initUpdateField();
		Object newValue = this.given.initUpdateValue();
		Object originalValue = Utils.getFieldValue(entity, field);
		Utils.setFieldValue(field, newValue, entity);
		VO updatedEntity = this.service.update(entity);
		boolean updated = !originalValue.equals(newValue);
		LOGGER.info(
				"Entity updated: Field \"" + field + "\" (" + originalValue + ") => (" + newValue + ") is " + updated);
		assertTrue("Entity updated: Field \"" + field + "\" (" + originalValue + ") => (" + newValue + ")", updated);
		logGivenEnviromentSubLine();
		return updatedEntity;

	}

	public boolean testDelete(VO entity) {
		boolean result = false;
		logGivenEnviromentSubLine();
		LOGGER.info("testDelete at class: " + this.getClass().getName());
		if (entity != null) {
			result = service.delete(entity);
			assertTrue("Delete successfull", result);
		} else {
			assertTrue("Can't test delete", false);
		}
		logGivenEnviromentSubLine();
		return result;
	}

	// TODO: test for unique exception

	@Test
	public void testFindAll()
			throws InstantiationException, IllegalAccessException, ExampleQueryException, UniqueException {
		logGivenEnviromentSubLine();
		LOGGER.info("testFindAll at class: " + this.getClass().getName());
		logGivenEnviromentSubLine();
		this.given.givenExamplesEnviroment();
		logGivenEnviromentSubLine();
		List<VO> result = service.findAll();
		LOGGER.info("Test findAll returns more than zero: " + result.size());
		assertTrue("Test findAll returns more than zero: ", !result.isEmpty());
		logGivenEnviromentSubLine();
	}

	@Test
	public void testCountAll()
			throws InstantiationException, IllegalAccessException, ExampleQueryException, UniqueException {
		logGivenEnviromentSubLine();
		LOGGER.info("testCountAll at class: " + this.getClass().getName());
		logGivenEnviromentSubLine();
		this.given.givenExamplesEnviroment();
		logGivenEnviromentSubLine();
		int result = service.countAll();
		LOGGER.info("Count all returns more than zero: " + result);
		assertTrue("Count all returns more than zero: " + result, result > 0);
		logGivenEnviromentSubLine();
	}

	@Test
	public void testFindByExample()
			throws InstantiationException, ExampleQueryException, UniqueException, IllegalAccessException {
		logGivenEnviromentSubLine();
		LOGGER.info("testFindByExample at class: " + this.getClass().getName());
		logGivenEnviromentSubLine();
		this.given.givenExamplesEnviroment();
		this.filter = this.given.initFilter();
		logGivenEnviromentSubLine();
		this.examples = this.given.initExamples();
		for (int i = 0; i < this.examples.length; i++) {
			VO example = this.examples[i];
			List<VO> result = service.findByExample(example, this.filter);
			showResult(example, result, i, this.filter);
			LOGGER.info("findByExample returns more than zero: " + result.size());
			assertTrue("findByExample returns more than zero: ", !result.isEmpty());
		}
		logGivenEnviromentSubLine();
	}

	@Test
	public void testCountByExample()
			throws InstantiationException, ExampleQueryException, UniqueException, IllegalAccessException {
		logGivenEnviromentSubLine();
		LOGGER.info("testCountByExample at class: " + this.getClass().getName());
		logGivenEnviromentSubLine();
		this.given.givenExamplesEnviroment();
		this.filter = this.given.initFilter();
		logGivenEnviromentSubLine();
		this.examples = this.given.initExamples();
		for (int i = 0; i < this.examples.length; i++) {
			VO example = this.examples[i];
			Integer result = service.countByExample(example, this.filter);
			LOGGER.info("Count by example returns more than zero: " + result);
			assertTrue("Count by example returns more than zero: " + result, result > 0);
		}
		logGivenEnviromentSubLine();
	}

	@Test
	public void findCustomByExample()
			throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, ExampleQueryException, UniqueException {
		LOGGER.info("Find Custom by example " + this.getClass().getName());
		logGivenEnviromentSubLine();
		this.given.givenExamplesEnviroment();
		this.filter = this.given.initFilter();
		this.customFields = this.given.initCustomFields();
		logGivenEnviromentSubLine();
		this.examples = this.given.initExamples();
		for (int i = 0; i < this.examples.length; i++) {
			logGivenEnviromentSubLine();
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

	protected static void logGivenEnviromentLine() {
		LOGGER.info("*************************************************"
				+ "***************************************************"
				+ "***************************************************"
				+ "***************************************************");
	}

	protected static void logGivenEnviromentSubLine() {
		LOGGER.info("-------------------------------------------------"
				+ "---------------------------------------------------"
				+ "---------------------------------------------------"
				+ "---------------------------------------------------");
	}

	protected void logGivenEnviromentStart() {
		logGivenEnviromentSubLine();
		LOGGER.info("Given enviroment for " + this.getClass().getName());
		logGivenEnviromentSubLine();
	}
}
