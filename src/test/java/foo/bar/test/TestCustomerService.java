package foo.bar.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import foo.bar.domain.Customer;
import foo.bar.service.impl.CustomerServiceImpl;
import foo.bar.service.utils.HqlConditions;
import foo.bar.utils.Utils;

public class TestCustomerService extends TestCommon<CustomerServiceImpl, Customer> {

	@Override
	protected Map<String, HqlConditions> initFilter() {
		Map<String, HqlConditions> filter = new HashMap<>();
		filter.put(Customer.NAME, HqlConditions.LIKE_IGNORE_CASE);
		filter.put(Customer.LAST_NAME, HqlConditions.EQUALS);
		// filter.put(Customer.BIRTH_DATE, HqlConditions.BETWEEN);
		filter.put(Customer.BIRTH_DATE_START, HqlConditions.GREATER_EQUALS);
		filter.put(Customer.BIRTH_DATE_END, HqlConditions.LOWER_EQUALS);
		filter.put(Customer.DOCUMENT, HqlConditions.EQUALS);
		filter.put(Customer.DOCUMENT_TYPE_LIST, HqlConditions.IN);
		filter.put(Customer.ORDERS_PRODUCTS_NAME, HqlConditions.LIKE_IGNORE_CASE);
		return filter;
	}

	@Override
	protected Customer[] initExamples() {
		Customer example1 = new Customer();
		example1.setName("Jesus");
		example1.setLastName("Lopez");
		example1.setDocument("30973837J");
		example1.setBirthDateStart(Utils.getDate("01/01/1983 00:00:00", "dd/MM/yyyy hh:mm:ss"));
		example1.setBirthDateEnd(Utils.getDate("01/01/1983 00:00:00", "dd/MM/yyyy hh:mm:ss"));

		Customer example2 = new Customer();
		example2.setName("Jesus");
		example2.setDocument("30973837J");
		example2.setBirthDateStart(Utils.getDate("01/01/1983 00:00:00", "dd/MM/yyyy hh:mm:ss"));

		Customer example3 = new Customer();
		example3.setLastName("Lopez");
		example3.setBirthDateEnd(Utils.getDate("01/01/1983 00:00:00", "dd/MM/yyyy hh:mm:ss"));
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

}
