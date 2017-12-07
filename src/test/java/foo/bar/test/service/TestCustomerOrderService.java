package foo.bar.test.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import foo.bar.domain.Customer;
import foo.bar.domain.CustomerOrder;
import foo.bar.domain.Product;
import foo.bar.domain.ProductStock;
import foo.bar.exceptions.UniqueException;
import foo.bar.service.impl.CustomerOrderServiceImpl;
import foo.bar.service.utils.HqlConditions;
import foo.bar.test.TestCommon;
import foo.bar.test.given.GivenCustomer;
import foo.bar.test.given.GivenCustomerOrder;
import foo.bar.test.given.GivenProduct;
import foo.bar.test.given.GivenProductStock;
import foo.bar.utils.Utils;

public class TestCustomerOrderService extends TestCommon<CustomerOrderServiceImpl, CustomerOrder, GivenCustomerOrder> {

	protected void givenExamplesEnviroment() throws UniqueException {
		super.logGivenEnviromentStart();
		Customer customer = GivenCustomer.givenADefaultCustomer(entityManager);
		Product product = GivenProduct.givenAProduct("CocaCola", "Lata", entityManager);
		Date dateOrder = Utils.getDate("01/01/2017");
		ProductStock productsStock = GivenProductStock.givenAProductStock(product, 100, null, entityManager);
		List<ProductStock> productsStockList = new ArrayList<>();
		productsStockList.add(productsStock);
		GivenCustomerOrder.givenACustomerOrder(customer, dateOrder, productsStockList, entityManager);
	}

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

		// all examples
		filter.put(CustomerOrder.PRODUCTS_STOCK, HqlConditions.IS_NOT_EMPTY);

		// example 1
		filter.put(CustomerOrder.CUSTOMER, HqlConditions.NOT_EQUALS);

		// example 2
		filter.put(CustomerOrder.DATE, HqlConditions.LOWER_EQUALS);

		// example 3
		filter.put(CustomerOrder.PRODUCTS_STOCK_IDS, HqlConditions.IN);
		return filter;
	}

	@Override
	protected CustomerOrder[] initExamples() throws UniqueException {
		Customer customerExample = new Customer();
		customerExample.setPk(100L);

		CustomerOrder example1 = new CustomerOrder();
		example1.setCustomer(customerExample);

		CustomerOrder example2 = new CustomerOrder();
		example2.setDate(new Date());

		CustomerOrder example3 = new CustomerOrder();
		Product product = GivenProduct.givenAProduct("Orange", "Color", entityManager);
		List<Long> productStockIds = new ArrayList<>();
		ProductStock productStock = GivenProductStock.givenAProductStock(product, 10, null, entityManager);
		productStockIds.add(productStock.getPk());
		List<ProductStock> productsStockList = new ArrayList<>();
		productsStockList.add(productStock);
		Customer customer = GivenCustomer.givenACustomer("Buyer", "cutomer", new Date(), "NO-ID", "DNI", entityManager);
		GivenCustomerOrder.givenACustomerOrder(customer, new Date(), productsStockList, entityManager);
		example3.setProductsStockIds(productStockIds);

		CustomerOrder[] examples = { example1, example2, example3 };
		return examples;
	}

	@Override
	protected String[] initCustomFields() {
		String field1 = CustomerOrder.PK;
		String field2 = CustomerOrder.DATE;
		String field3 = CustomerOrder.CUSTOMER + "." + Customer.PK;
		String field4 = CustomerOrder.CUSTOMER + "." + Customer.NAME;
		String field5 = CustomerOrder.CUSTOMER + "." + Customer.LAST_NAME;
		String fields[] = { field1, field2, field3, field4, field5 };
		return fields;
	}

	@Override
	protected CustomerOrder initSaveEntity() throws UniqueException {
		Customer customer = null;
		customer = GivenCustomer.givenACustomer("User", "Saved", new Date(), "LKKJHK", "DNI", entityManager);
		return GivenCustomerOrder.givenObjectCustomerOrder(customer, new Date(), null);
	}

	@Override
	protected String initUpdateField() {
		return CustomerOrder.DATE;
	}

	@Override
	protected Object initUpdateValue() {
		return new Date();
	}

}
