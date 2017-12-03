package foo.bar.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import foo.bar.domain.Customer;
import foo.bar.domain.Order;
import foo.bar.domain.ProductStock;
import foo.bar.service.impl.OrderServiceImpl;
import foo.bar.service.utils.HqlConditions;

public class TestOrderService extends TestCommon<OrderServiceImpl, Order> {

	@Override
	protected Map<String, Object> initEntityFields() {
		Map<String, Object> mapValues = new HashMap<>();
		mapValues.put(Order.DATE, new Date());
		mapValues.put(Order.CUSTOMER+"."+Customer.NAME, "Jesus");
		return mapValues;
	}

	@Override
	protected Map<String, HqlConditions> initFilter() {
		Map<String, HqlConditions> filter = new HashMap<String, HqlConditions>();
		filter.put(Order.DATE, HqlConditions.LOWER_THAN);
		filter.put(Order.PRODUCTS, HqlConditions.IN);
		filter.put(Order.CUSTOMER, HqlConditions.NOT_EQUALS);
		return filter;
	}

	@Override
	protected Order[] initExamples() {
		Customer customer = new Customer();
		customer.setPk(1L);

		Order example1 = new Order();
		example1.setCustomer(customer);

		Order example2 = new Order();
		example2.setDate(new Date());

		Order example3 = new Order();
		List<ProductStock> products = new ArrayList<>();
		ProductStock product1 = new ProductStock();
		product1.setPk(1L);
		products.add(product1);

		ProductStock product2 = new ProductStock();
		product2.setPk(2L);
		products.add(product2);
		example3.setProducts(products);
		Order[] examples = { example1, example2, example3 };
		return examples;
	}

	@Override
	protected String[] initCustomFields() {
		String field1 = Order.CUSTOMER;
		String field2 = Order.PK;
		String field3 = Order.DATE;
		String fields[] = {field1, field2, field3};
		return fields;
	}

}
