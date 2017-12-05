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
import foo.bar.service.impl.ServiceImpl;
import foo.bar.service.utils.HqlConditions;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@Transactional
public abstract class TestCommon<ServiceVO extends ServiceImpl<VO>, VO extends BasicVO<?>> {

	protected static Logger LOGGER = Logger.getLogger(TestCommon.class);

	protected static final String TIME_FORMAT = "DD/MM/YYYY HH:mm:SS";

	protected static final String DATE_FORMAT = "DD/MM/YYYY";

	@PersistenceContext
	protected EntityManager entityManager;

	private ServiceImpl<VO> service;

	private Class voClass;

	private Class serviceVoClass;

	private Map<String, HqlConditions> filter;

	private VO[] examples;

	private String[] customFields;

	public TestCommon() {
		this.serviceVoClass = (Class<ServiceVO>) ((ParameterizedType) this.getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
		this.voClass = (Class<ServiceVO>) ((ParameterizedType) this.getClass().getGenericSuperclass())
				.getActualTypeArguments()[1];
		LOGGER.info("Creating test for class: " + this.serviceVoClass.getName());
		this.filter = this.initFilter();
		this.examples = this.initExamples();
		this.customFields = this.initCustomFields();
	}

	protected abstract String[] initCustomFields();

	protected abstract VO[] initExamples();

	protected abstract Map<String, HqlConditions> initFilter();

	protected abstract Map<String, Object> initEntityFields();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws InstantiationException, IllegalAccessException {
		service = (ServiceImpl<VO>) serviceVoClass.newInstance();
		service.setEntityManager(entityManager);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAll() throws InstantiationException, IllegalAccessException, ExampleQueryException {
		List<VO> result = service.findAll();
		assertTrue(!result.isEmpty());
	}

	@Test
	public void testCountAll() throws InstantiationException, IllegalAccessException, ExampleQueryException {
		int result = service.countAll();
		assertTrue("Count all returns more than zero: " + result, result > 0);
	}

	@Test
	public void testEntity() throws NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Map<String, Object> mapValues = initEntityFields();
		Constructor constructor = voClass.getConstructor(HashMap.class);
		VO entity = (VO) constructor.newInstance((Map) mapValues);
		assertTrue(entity != null);
	}

	@Test
	public void testFindByExample() {
		LOGGER.info("testFindByExample at class: " + this.getClass().getName());
		for (int i = 0; i < examples.length; i++) {
			LOGGER.info("-----------------------------------------------------------------------------");
			VO example = examples[i];
			List<VO> result;
			try {
				result = service.findByExample(example, filter);
				showResult(example, result, i, filter);
				assertTrue(!result.isEmpty());
			} catch (ExampleQueryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testCountByExample() {
		LOGGER.info("testCountByExample at class: " + this.getClass().getName());
		for (int i = 0; i < examples.length; i++) {
			LOGGER.info("-----------------------------------------------------------------------------");
			VO example = examples[i];
			Integer result;
			try {
				result = service.countByExample(example, filter);
				// showResult(example, result, i, filter);
				assertTrue("Count by example returns more than zero: " + result, result > 0);
			} catch (ExampleQueryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test
	public void findCustomByExample() {
		LOGGER.info("findCustomByExample at class: " + this.getClass().getName());
		for (int i = 0; i < examples.length; i++) {
			LOGGER.info("-----------------------------------------------------------------------------");
			VO example = examples[i];
			List<VO> result;
			try {
				result = service.findCustomByExample(example, customFields, filter);
				showResult(example, result, i, filter);
				assertTrue(!result.isEmpty());
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException | ExampleQueryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
				LOGGER.info("item[" + exampleIndex + "]: " + vo.toString());
			}
		}
	}
}
