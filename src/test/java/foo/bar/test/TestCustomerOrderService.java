package foo.bar.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import foo.bar.domain.Customer;
import foo.bar.domain.CustomerOrder;
import foo.bar.domain.ProductStock;
import foo.bar.service.impl.CustomerOrderServiceImpl;
import foo.bar.service.utils.HqlConditions;

public class TestCustomerOrderService extends TestCommon<CustomerOrderServiceImpl, CustomerOrder> {

	@Override
	protected Map<String, Object> initEntityFields() {
		Map<String, Object> mapValues = new HashMap<>();
		mapValues.put(CustomerOrder.DATE, new Date());
		mapValues.put(CustomerOrder.CUSTOMER + "." + Customer.NAME, "Jesus");
		return mapValues;
	}

	@Override
	protected Map<String, HqlConditions> initFilter() {
		Map<String, HqlConditions> filter = new HashMap<String, HqlConditions>();
		filter.put(CustomerOrder.DATE, HqlConditions.LOWER_THAN);
		// TODO
		// filter.put(CustomerOrder.PRODUCTS_STOCK, HqlConditions.IN);
		filter.put(CustomerOrder.CUSTOMER, HqlConditions.NOT_EQUALS);
		return filter;
	}

	@Override
	protected CustomerOrder[] initExamples() {
		Customer customer = new Customer();
		customer.setPk(1L);

		CustomerOrder example1 = new CustomerOrder();
		example1.setCustomer(customer);

		CustomerOrder example2 = new CustomerOrder();
		example2.setDate(new Date());

		CustomerOrder example3 = new CustomerOrder();
		List<ProductStock> products = new ArrayList<>();
		ProductStock product1 = new ProductStock();
		product1.setPk(1L);
		products.add(product1);

		ProductStock product2 = new ProductStock();
		product2.setPk(2L);
		products.add(product2);
		example3.setProductsStock(products);
		CustomerOrder[] examples = { example1, example2, example3 };
		return examples;
	}

	@Override
	protected String[] initCustomFields() {
		String field1 = CustomerOrder.CUSTOMER;
		String field2 = CustomerOrder.PK;
		String field3 = CustomerOrder.DATE;
		String fields[] = { field1, field2, field3};
		return fields;
	}

}
