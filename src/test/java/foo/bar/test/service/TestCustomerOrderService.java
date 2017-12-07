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
	protected CustomerOrder[] initExamples() throws UniqueException, InstantiationException, IllegalAccessException {
		Customer customerExample = new Customer();
		customerExample.setPk(100L);

		CustomerOrder example1 = new CustomerOrder();
		example1.setCustomer(customerExample);

		CustomerOrder example2 = new CustomerOrder();
		example2.setDate(new Date());

		CustomerOrder example3 = new CustomerOrder();
		GivenProduct givenProduct = new GivenProduct(entityManager);
		Product product = givenProduct.givenAProduct("Orange", "Color");
		List<Long> productStockIds = new ArrayList<>();
		GivenProductStock givenProductStock = new GivenProductStock(entityManager);
		ProductStock productStock = givenProductStock.givenAProductStock(product, 10, null);
		productStockIds.add(productStock.getPk());
		List<ProductStock> productsStockList = new ArrayList<>();
		productsStockList.add(productStock);
		GivenCustomer givenCustomer = new GivenCustomer(entityManager);
		Customer customer = givenCustomer.givenACustomer("Buyer", "cutomer", new Date(), "NO-ID", "DNI");
		GivenCustomerOrder givenCustomerOrder = new GivenCustomerOrder(entityManager);
		givenCustomerOrder.givenACustomerOrder(customer, new Date(), productsStockList);
		example3.setProductsStockIds(productStockIds);

		CustomerOrder[] examples = { example1, example2, example3 };
		return examples;
	}


	@Override
	protected CustomerOrder initSaveEntity() throws UniqueException, InstantiationException, IllegalAccessException {
		Customer customer = null;
		GivenCustomer givenCustomer = new GivenCustomer(entityManager);
		customer = givenCustomer.givenACustomer("User", "Saved", new Date(), "LKKJHK", "DNI");
		GivenCustomerOrder givenCustomerOrder = new GivenCustomerOrder(entityManager);
		return givenCustomerOrder.givenObjectCustomerOrder(customer, new Date(), null);
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
