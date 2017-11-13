package foo.bar.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.ParameterizedType;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.mockito.Mockito;

import foo.bar.domain.BasicVO;
import foo.bar.domain.Customer;
import foo.bar.service.impl.ServiceImpl;

public abstract class TestCommon<ServiceVO extends ServiceImpl, VO extends BasicVO> {

	protected static Logger LOGGER = Logger.getLogger(TestCommon.class);

	protected EntityManager entityManager;

	protected ServiceImpl<VO> service;

	protected Class serviceVoClass;

	public TestCommon() {
		this.serviceVoClass = (Class<ServiceVO>) ((ParameterizedType) this.getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
		LOGGER.info("Creating service for class: " + this.serviceVoClass.getName());
	}

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
}
