package foo.bar.test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import foo.bar.domain.BasicVO;
import foo.bar.domain.Customer;
import foo.bar.exceptions.ExampleQueryException;
import foo.bar.service.impl.ServiceImpl;
import foo.bar.service.utils.HqlConditions;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@Transactional
public abstract class TestCommon<ServiceVO extends ServiceImpl<VO>, VO extends BasicVO<?>> {

	protected static Logger LOGGER = Logger.getLogger(TestCommon.class);

	@PersistenceContext
	protected EntityManager entityManager;

	protected ServiceImpl<VO> service;

	protected Class voClass;

	protected Class serviceVoClass;

	protected Map<String, HqlConditions> filter;

	protected VO[] examples;

	protected String[] customFields;

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
	public void testEntity() throws NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Map<String, Object> mapValues = initEntityFields();
		Constructor constructor = voClass.getDeclaredConstructor(Map.class);
		Object entity = constructor.newInstance(mapValues);
		assertTrue(entity != null);
	}

	protected abstract Map<String, Object> initEntityFields();

	@Test
	public void testFindByExample() throws ExampleQueryException {
		LOGGER.info("testFindByExample at class: " + this.getClass().getName());
		for (int i = 0; i < examples.length; i++) {
			LOGGER.info("-----------------------------------------------------------------------------");
			VO example = examples[i];
			List<VO> result = service.findByExample(example, filter);
			assertTrue(!result.isEmpty());
			if (result.isEmpty()) {
				LOGGER.error("FAIL AT SAMPLE: +" + i);
				break;
			}
		}
	}

	@Test
	public void findCustomByExample() throws ExampleQueryException {
		LOGGER.info("findCustomByExample at class: " + this.getClass().getName());
		for (int i = 0; i < examples.length; i++) {
			LOGGER.info("-----------------------------------------------------------------------------");
			VO example = examples[i];
			List<VO> result = service.findCustomByExample(example, customFields, filter);
			assertTrue(!result.isEmpty());
			if (result.isEmpty()) {
				LOGGER.error("FAIL AT SAMPLE: +" + i);
				break;
			}
		}
	}
}
