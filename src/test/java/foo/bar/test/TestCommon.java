package foo.bar.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import foo.bar.domain.BasicVO;
import foo.bar.domain.Customer;
import foo.bar.service.impl.ServiceImpl;

public abstract class TestCommon<ServiceVO extends ServiceImpl, VO extends BasicVO> {

	protected static Logger LOGGER = Logger.getLogger(TestCommon.class);

	protected EntityManager entityManager;

	protected ServiceImpl<VO> service;

	protected Class serviceVoClass;

	protected Map<String, String> filter;

	protected VO[] examples;

	public TestCommon() {
		this.serviceVoClass = (Class<ServiceVO>) ((ParameterizedType) this.getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
		LOGGER.info("Creating test for class: " + this.serviceVoClass.getName());
		this.filter = this.initFilter();
		this.examples = this.initExamples();
	}

	protected abstract VO[] initExamples();

	protected abstract Map<String, String> initFilter();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() {
		try {
			service = (ServiceImpl<VO>) serviceVoClass.newInstance();
			entityManager = mock(EntityManager.class);
			service.setEntityManager(entityManager);

			TypedQuery<Customer> query = mock(TypedQuery.class);
			when(entityManager.createQuery(Mockito.anyString())).thenReturn(query);

		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFindByExample() {
		LOGGER.info("testFindByExample at class: " + this.getClass().getName());
		for (int i = 0; i < examples.length; i++) {
			VO example = examples[i];
			List<VO> result = service.findByExample(example, filter);
			assert (result == null);
		}
	}
}
