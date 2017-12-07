package foo.bar.test.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import foo.bar.domain.Customer;
import foo.bar.exceptions.UniqueException;
import foo.bar.service.impl.CustomerServiceImpl;
import foo.bar.service.utils.HqlConditions;
import foo.bar.test.TestCommon;
import foo.bar.test.given.GivenCustomer;
import foo.bar.utils.Utils;

public class TestCustomerService extends TestCommon<CustomerServiceImpl, Customer, GivenCustomer> {

	@Override
	protected Map<String, HqlConditions> initFilter() {
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
	protected Customer[] initExamples() {
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
	protected String[] initCustomFields() {
		String field1 = Customer.NAME;
		String field2 = Customer.DOCUMENT;
		String field3 = Customer.BIRTH_DATE;
		String field4 = Customer.LAST_NAME;
		String[] fields = { field1, field2, field3, field4 };
		return fields;
	}

	@Override
	protected Map<String, Object> initEntityFields() {
		Map<String, Object> mapValues = new HashMap<>();
		mapValues.put(Customer.BIRTH_DATE, new Date());
		mapValues.put(Customer.NAME, "Jesus");
		return mapValues;
	}

	@Override
	protected Customer initSaveEntity() throws UniqueException, InstantiationException, IllegalAccessException {
		GivenCustomer givenCustomer = new GivenCustomer(entityManager);
		return givenCustomer.givenObjectCustomer("test name", "test last name", new Date(), "1234", "DNI");
	}

	@Override
	protected String initUpdateField() {
		return Customer.LAST_NAME;
	}

	@Override
	protected Object initUpdateValue() {
		return "Gamero";
	}

}
