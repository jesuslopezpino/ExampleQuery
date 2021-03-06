package com.polvisoft.exampleQuery.test.common;

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

import com.polvisoft.exampleQuery.domain.BasicDTO;
import com.polvisoft.exampleQuery.exceptions.ExampleQueryException;
import com.polvisoft.exampleQuery.exceptions.UniqueException;
import com.polvisoft.exampleQuery.filter.FilterMap;
import com.polvisoft.exampleQuery.repository.impl.RepositoryImpl;
import com.polvisoft.exampleQuery.utils.Utils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@Transactional
// TODO: add DTO
public abstract class TestCommon<ServiceVO extends RepositoryImpl<DTO>, DTO extends BasicDTO<?>, GivenVO extends Given<DTO, ServiceVO>> {

	protected static Logger LOGGER = Logger.getLogger(TestCommon.class);

	@PersistenceContext
	protected EntityManager entityManager;

	private RepositoryImpl<DTO> repository;

	protected GivenVO given;

	private Class voClass;

	private Class repositoryClass;

	private Class givenVoClass;

	private FilterMap filter;

	private DTO[] examples;

	private String[] customFields;

	private int pageSize;

	private int pageNumber;

	public TestCommon() {
		logLine();
		this.repositoryClass = (Class<ServiceVO>) ((ParameterizedType) this.getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
		this.voClass = (Class<DTO>) ((ParameterizedType) this.getClass().getGenericSuperclass())
				.getActualTypeArguments()[1];
		this.givenVoClass = (Class<GivenVO>) ((ParameterizedType) this.getClass().getGenericSuperclass())
				.getActualTypeArguments()[2];
		LOGGER.info("Unit Test Class: " + this.getClass().getName());
		LOGGER.info("Entity: " + this.voClass.getName());
		LOGGER.info("Repository: " + this.repositoryClass.getName());
		LOGGER.info("Given: " + this.givenVoClass.getName());
		logLine();
	}

	@BeforeClass
	public static void setUpBeforeClass() {
		logAsteriscLine();
		LOGGER.info("setUpBeforeClass");
		logAsteriscLine();
	}

	@AfterClass
	public static void tearDownAfterClass() {
		logAsteriscLine();
		LOGGER.info("tearDownAfterClass");
		logAsteriscLine();
	}

	@Before
	public void setUp() throws InstantiationException, IllegalAccessException, UniqueException, NoSuchMethodException,
			SecurityException, IllegalArgumentException, InvocationTargetException {
		logLine();
		LOGGER.info("setUp");
		this.repository = (RepositoryImpl<DTO>) this.repositoryClass.newInstance();
		this.repository.setEntityManager(this.entityManager);
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
	public void testUniqueException() throws ExampleQueryException {
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
				LOGGER.info(this.repository.getClass() + " returns UniqueException");
				assertTrue(this.repository.getClass() + " returns UniqueException", true);
			}
		} else {
			LOGGER.info(this.voClass.getName() + " doesn't have unique constraints");
			assertTrue(this.voClass.getName() + " doesn't have unique constraints", true);
		}
		logLine();
	}

	@Test
	public void testSaveUpdateFindByPkAndDelete() throws UniqueException, ExampleQueryException {
		logLine();
		LOGGER.info("testSaveUpdateFindByPkAndDelete");
		DTO entity = this.testSave();
		entity = this.testUpdate(entity);
		DTO findByPkEntity = this.testFindByPk(entity);
		boolean testCompleted = this.testDelete(findByPkEntity);
		LOGGER.info("testSaveUpdateFindByPkAndDelete completed " + testCompleted);
		assertTrue("testSaveUpdateFindByPkAndDelete completed ", testCompleted);
		logLine();
	}

	protected DTO testFindByPk(DTO entity) {
		DTO findByPkEntity = this.repository.findByPk(entity.getPk());
		LOGGER.info("Entity find by pk is found: " + findByPkEntity);
		assertTrue("Entity find by pk is found", findByPkEntity != null);
		return findByPkEntity;
	}

	protected DTO testSave() throws UniqueException, ExampleQueryException {
		logLine();
		LOGGER.info("testSave");
		DTO entity = this.given.initTestSaveInstance();
		entity = this.repository.save(entity);
		LOGGER.info("Save successfull is " + (entity != null && entity.getPk() != null));
		assertTrue("Save successfull", entity != null && entity.getPk() != null);
		logLine();
		return entity;
	}

	protected DTO testUpdate(DTO entity) throws UniqueException, ExampleQueryException {
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
		DTO updatedEntity = this.repository.update(entity);
		LOGGER.info("Entity updated: is " + updated);
		assertTrue("Entity updated", updated);
		logLine();
		return updatedEntity;

	}

	protected boolean updateFieldAtEntity(DTO entity, String fieldName, Object newValue) throws ExampleQueryException {
		try {
			Object originalValue = Utils.getFieldValue(entity, fieldName);
			Utils.setFieldValue(fieldName, newValue, entity);
			LOGGER.info("Entity updated: Field \"" + fieldName + "\" (" + originalValue + ") => (" + newValue + ")");
			return (newValue != originalValue);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | NoSuchFieldException | InstantiationException e) {
			throw new ExampleQueryException(e);
		}
	}

	protected boolean testDelete(DTO entity) {
		boolean result = false;
		logLine();
		LOGGER.info("testDelete at class: " + this.getClass().getName());
		if (entity != null) {
			result = this.repository.delete(entity);
			assertTrue("Delete successfull", result);
		} else {
			assertTrue("Can't test delete", false);
		}
		logLine();
		return result;
	}

	// TODO: test for unique exception

	@Test
	public void testFindAll() throws UniqueException, ExampleQueryException {
		logLine();
		LOGGER.info("testFindAll at class: " + this.getClass().getName());
		logLine();

		this.initSetupEnvironmentExamples();

		logLine();
		List<DTO> result = this.repository.findAll();
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
		int result = this.repository.countAll();
		LOGGER.info("Count all returns more than zero: " + result);
		assertTrue("Count all returns more than zero: " + result, result > 0);
		logLine();
	}

	@Test
	public void testFindByExample() throws UniqueException, ExampleQueryException {
		logLine();
		LOGGER.info("testFindByExample at class: " + this.getClass().getName());
		this.setupExamplesQueryEnvironment();
		for (int i = 0; i < this.examples.length; i++) {
			DTO example = this.examples[i];
			List<DTO> result = this.repository.findByExample(example, this.filter);
			this.showResult(example, result, i, this.filter);
			LOGGER.info("findByExample returns more than zero: " + result.size());
			assertTrue("findByExample returns more than zero: ", !result.isEmpty());
		}
		logLine();
	}
	
	@Test
	public void testFindByExamplePaginated() throws UniqueException, ExampleQueryException {
		logLine();
		LOGGER.info("testFindByExample at class: " + this.getClass().getName());
		this.setupExamplesQueryEnvironment();
		for (int i = 0; i < this.examples.length; i++) {
			DTO example = this.examples[i];
			List<DTO> result = this.repository.findByExample(example, this.filter, this.pageNumber, this.pageSize);
			this.showResult(example, result, i, this.filter);
			LOGGER.info("findByExample returns more than zero: " + result.size());
			assertTrue("findByExample returns more than zero: ", !result.isEmpty());
		}
		logLine();
	}

	private void initSetupEnvironmentExamples() throws UniqueException, ExampleQueryException {
		this.logGivenEnvironmentStart();
		this.given.givenExamplesEnvironment();
		LOGGER.info("Example environment ends");
	}

	protected void setupExamplesQueryEnvironment() throws UniqueException, ExampleQueryException {
		logLine();
		LOGGER.info("setupExamplesQueryEnvironment");
		this.initSetupEnvironmentExamples();
		this.customFields = this.given.initCustomFields();
		assertTrue("Custom fields has been initializated", this.customFields != null && this.customFields.length > 0);
		this.filter = this.given.initFilter();
		assertTrue("Filters has been initializated", this.filter != null && this.filter.getMap().size() > 0);
		this.examples = this.given.initExamples();
		assertTrue("Examples has been initializated", this.examples != null && this.examples.length > 0);
		this.pageNumber = this.given.initPageNumber();
		assertTrue("PageNumber has been initilizated", this.pageNumber >= 0);
		this.pageSize = this.given.initPageSize();
		assertTrue("PageSize has been initilizated", this.pageSize > 0);
		logLine();
	}

	@Test
	public void testCountByExample() throws InstantiationException, ExampleQueryException, UniqueException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, NoSuchFieldException {
		logLine();
		LOGGER.info("testCountByExample at class: " + this.getClass().getName());
		logLine();
		this.setupExamplesQueryEnvironment();
		LOGGER.info("Count by example filters: " + this.filter);
		for (int i = 0; i < this.examples.length; i++) {
			DTO example = this.examples[i];
			Integer result = this.repository.countByExample(example, this.filter);
			LOGGER.info("Count by example returns more than zero: " + result + " Example: " + example.toStringDebug());
			assertTrue("Count by example returns more than zero: " + result, result > 0);
		}
		logLine();
	}

	@Test
	public void testFindCustomByPk() throws InstantiationException, IllegalAccessException, UniqueException,
			NoSuchMethodException, InvocationTargetException, ExampleQueryException {
		logLine();
		this.customFields = this.given.initCustomFields();
		DTO entity = this.testSave();
		DTO result = this.repository.findCustomByPk(entity.getPk(), this.customFields);
		LOGGER.info("testFindCustomByPk returns the searched element: " + result != null);
		assertTrue("testFindCustomByPk returns the searched element: " + (result != null), result != null);
		logLine();
	}

	@Test
	public void findCustomByExample() throws NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, ExampleQueryException,
			UniqueException, NoSuchFieldException {
		LOGGER.info("Find Custom by example " + this.getClass().getName());
		logLine();
		this.setupExamplesQueryEnvironment();
		for (int i = 0; i < this.examples.length; i++) {
			logLine();
			DTO example = this.examples[i];
			LOGGER.info("example: " + (i + 1));
			List<DTO> result = this.repository.findCustomByExample(example, this.customFields, this.filter);
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

	private void showResult(DTO example, List<DTO> result, int exampleIndex, FilterMap filters) {
		if (result.isEmpty()) {
			LOGGER.error("FAIL AT SAMPLE: " + (exampleIndex + 1));
			LOGGER.error("Example object: " + example.toStringDebug());
			LOGGER.error("With filters: " + filters);
		} else {
			LOGGER.info("Example object: " + example.toStringDebug());
			LOGGER.info("With filters: " + filters);
			LOGGER.info("Returns " + result.size() + " items");
			for (DTO vo : result) {
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
