package foo.bar.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import foo.bar.domain.Customer;
import foo.bar.service.impl.CustomerServiceImpl;
import foo.bar.service.utils.HqlConditions;
import foo.bar.utils.Utils;

public class TestService extends TestCommon<CustomerServiceImpl, Customer> {


	@Test
	public void testFindByExample() {
		Map<String, String> filter = new HashMap<>();
		filter.put(Customer.NAME, HqlConditions.LIKE_IGNORE_CASE);
		filter.put(Customer.LAST_NAME, HqlConditions.EQUALS);
		filter.put(Customer.BIRTH_DATE, HqlConditions.BETWEEN);
		filter.put(Customer.DOCUMENT, HqlConditions.EQUALS);
		filter.put(Customer.DOCUMENT_TYPE, HqlConditions.IN);

		String[] fields = { Customer.PK, Customer.NAME, Customer.LAST_NAME };

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
