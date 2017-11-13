package foo.bar.test;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import foo.bar.domain.Customer;
import foo.bar.service.impl.CustomerServiceImpl;
import foo.bar.service.impl.ServiceImpl;
import foo.bar.service.utils.HQL_CONDITIONS;
import foo.bar.utils.Utils;

public class TestService {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private ServiceImpl<Customer> service;
	private EntityManager entityManager;

	@Before
	public void setUp() throws Exception {
		service = new CustomerServiceImpl();
		entityManager = mock(EntityManager.class);
		service.setEntityManager(entityManager);

		TypedQuery<Customer> query = mock(TypedQuery.class);
		when(entityManager.createQuery(Mockito.anyString())).thenReturn(query);
	}

	@After
	public void tearDown() throws Exception {
	}

	// @Test
	public void testFindById() {
		fail("Not yet implemented");
	}

//	@Test
	public void testFindCustomByPk() {

		String[] fields = { Customer.PK, Customer.NAME, Customer.LAST_NAME, Customer.DOCUMENT, Customer.DOCUMENT_TYPE };
		Customer result = service.findCustomByPk(1, fields);
		assert (result == null);
	}

	@Test
	public void testFindByExample() {
		Map<String, String> filter = new HashMap<>();
		filter.put(Customer.NAME, HQL_CONDITIONS.LIKE_IGNORE_CASE);
		filter.put(Customer.LAST_NAME, HQL_CONDITIONS.EQUALS);
		filter.put(Customer.BIRTH_DATE, HQL_CONDITIONS.BETWEEN);
		filter.put(Customer.DOCUMENT, HQL_CONDITIONS.EQUALS);
		filter.put(Customer.DOCUMENT_TYPE, HQL_CONDITIONS.IN);

		String[] fields = { Customer.PK, Customer.NAME, Customer.LAST_NAME };

		Customer example1 = new Customer();
		example1.setName("Jesus");
		example1.setLastName("Lopez");
		example1.setDocument("30973837J");
		example1.setBirthDateStart(Utils.getDate("01/01/1983 00:00:00", "dd/MM/yyyy hh:mm:ss"));
		example1.setBirthDateEnd(Utils.getDate("01/01/1983 00:00:00", "dd/MM/yyyy hh:mm:ss"));

		Customer example2 = new Customer();
		example2.setName("Jesus");
		example2.setLastName("Lopez");
		example2.setDocument("30973837J");
		example2.setBirthDateStart(Utils.getDate("01/01/1983 00:00:00", "dd/MM/yyyy hh:mm:ss"));

		Customer example3 = new Customer();
		example3.setName("Jesus");
		example3.setLastName("Lopez");
		example3.setDocument("30973837J");
		example3.setBirthDateEnd(Utils.getDate("01/01/1983 00:00:00", "dd/MM/yyyy hh:mm:ss"));

		List<String> documentTypeListExample = new ArrayList<>();
		documentTypeListExample.add("DNI");
		documentTypeListExample.add("NIF");
		documentTypeListExample.add("PASSPORT");
		Customer example4 = new Customer();
		example4.setDocumentTypeList(documentTypeListExample);

		Customer[] examples = { example1, example2, example3, example4 };

		for (int i = 0; i < examples.length; i++) {
			Customer example = examples[i];
			List<Customer> result = service.findByExample(example, filter);
			assert (result == null);
		}
	}

}
