package foo.bar.test.given;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import foo.bar.domain.Customer;
import foo.bar.domain.Product;
import foo.bar.domain.ProductStock;
import foo.bar.exceptions.UniqueException;
import foo.bar.service.impl.CustomerServiceImpl;
import foo.bar.service.utils.HqlConditions;
import foo.bar.utils.Utils;

public class GivenCustomer extends Given<Customer, CustomerServiceImpl> {

	public GivenCustomer(EntityManager entityManager) throws InstantiationException, IllegalAccessException {
		super(entityManager);
	}

	private static Logger LOGGER = Logger.getLogger(GivenCustomer.class);

	public Customer givenADefaultCustomer() throws UniqueException {
		return givenACustomer("Jesus", "Lopez", Utils.getDateTime("10/12/1983 12:00:00"), "XXXXXXX", "DNI");
	}

	public Customer givenACustomer(String name, String lastName, Date birthDate, String document, String documentType)
			throws UniqueException {
		Customer result = givenObjectCustomer(name, lastName, birthDate, document, documentType);
		result = service.save(result);
		LOGGER.info("GivenCustomer instance persisted " + customerToString(result));
		return result;
	}

	public Customer givenObjectCustomer(String name, String lastName, Date birthDate, String document,
			String documentType) {
		Customer result = new Customer();
		result.setName(name);
		result.setLastName(lastName);
		result.setBirthDate(birthDate);
		result.setDocument(document);
		result.setDocumentType(documentType);
		LOGGER.info("GivenCustomer class instance " + customerToString(result));
		return result;
	}

	public static String customerToString(Customer customer) {
		return "Customer [pk=" + customer.getPk() + ", name=" + customer.getName() + ", lastName="
				+ customer.getLastName() + ", document=" + customer.getDocument() + ", documentType="
				+ customer.getDocumentType() + ", birthDate=" + customer.getBirthDate() + ", customerOrders="
				+ customer.getCustomerOrders() + "]";
	}

	@Override
	public void givenExamplesEnviroment() throws UniqueException, InstantiationException, IllegalAccessException {
		Customer customer = givenADefaultCustomer();
		List<ProductStock> productsStock = new ArrayList<>();
		GivenProduct givenProduct = new GivenProduct(entityManager);
		Product product = givenProduct.givenAProduct("Pizza", "Pizza");
		GivenProductStock givenProductStock = new GivenProductStock(entityManager);
		ProductStock productStock = givenProductStock.givenAProductStock(product, 7, null);
		productsStock.add(productStock);
		GivenCustomerOrder givenCustomerOrder = new GivenCustomerOrder(entityManager);
		givenCustomerOrder.givenACustomerOrder(customer, Utils.getDateTime("01/01/2017 00:00:00"), productsStock);
		givenACustomer("One", "Customer", new Date(), "DNIXXX", "TYPE");
		givenACustomer("Two", "Customer", new Date(), "DNIXXX", "TYPE");
		givenACustomer("Three", "Customer", new Date(), "DNIXXX", "TYPE");
	}

	@Override
	public String[] initCustomFields() {
		String field1 = Customer.NAME;
		String field2 = Customer.DOCUMENT;
		String field3 = Customer.BIRTH_DATE;
		String field4 = Customer.LAST_NAME;
		String[] fields = { field1, field2, field3, field4 };
		return fields;
	}

	@Override
	public String initUpdateField() {
		return Customer.LAST_NAME;
	}

	@Override
	public Map<String, HqlConditions> initFilter() {
		Map<String, HqlConditions> filter = new HashMap<>();

		// all examples... IS_NULL, IS_NOT_NULL, IS_EMPTY and IS_NOT_EMPTY
		filter.put(Customer.BIRTH_DATE, HqlConditions.IS_NOT_NULL);
		filter.put(Customer.NOTES, HqlConditions.IS_EMPTY);

		// example 1
		filter.put(Customer.NAME, HqlConditions.LIKE);
		filter.put(Customer.LAST_NAME, HqlConditions.EQUALS);
		filter.put(Customer.DOCUMENT, HqlConditions.EQUALS);

		// example 2
		filter.put(Customer.BIRTH_DATE_START, HqlConditions.GREATER_EQUALS);
		filter.put(Customer.BIRTH_DATE_END, HqlConditions.LOWER_THAN);

		// example 3
		filter.put(Customer.ORDERS_PRODUCTS_NAME, HqlConditions.LIKE_IGNORE_CASE);

		// example 4
		filter.put(Customer.DOCUMENT_TYPE_LIST, HqlConditions.IN);
		return filter;
	}

	@Override
	public Customer[] initExamples() {
		Customer example1 = new Customer();
		example1.setName("Jesus");
		example1.setLastName("Lopez");
		example1.setDocument("XXXXXXX");

		Customer example2 = new Customer();
		example2.setBirthDateStart(Utils.getDateTime("01/01/1983 00:00:00"));
		example2.setBirthDateEnd(Utils.getDateTime("12/12/1983 23:59:59"));

		Customer example3 = new Customer();
		example3.setOrdersProductsName("Pizza");

		List<String> documentTypeListExample = new ArrayList<>();
		documentTypeListExample.add("DNI");
		documentTypeListExample.add("NIF");
		documentTypeListExample.add("PASSPORT");
		Customer example4 = new Customer();
		example4.setDocumentTypeList(documentTypeListExample);

		Customer[] examples = { example1, example2, example3, example4 };
		return examples;
	}

	@Override
	public Map<String, Object> initEntityFields() {
		Map<String, Object> mapValues = new HashMap<>();
		mapValues.put(Customer.BIRTH_DATE, new Date());
		mapValues.put(Customer.NAME, "Jesus");
		return mapValues;
	}

	@Override
	public Customer initSaveEntity() throws UniqueException, InstantiationException, IllegalAccessException {
		GivenCustomer givenCustomer = new GivenCustomer(entityManager);
		return givenCustomer.givenObjectCustomer("test name", "test last name", new Date(), "1234", "DNI");
	}

	@Override
	public Object initUpdateValue() {
		return "Gamero";
	}

}
