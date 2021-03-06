package com.polvisoft.exampleQuery.test.tests.given;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.polvisoft.exampleQuery.enums.HqlConditions;
import com.polvisoft.exampleQuery.exceptions.ExampleQueryException;
import com.polvisoft.exampleQuery.exceptions.UniqueException;
import com.polvisoft.exampleQuery.filter.FilterMap;
import com.polvisoft.exampleQuery.test.common.Given;
import com.polvisoft.exampleQuery.test.domain.Customer;
import com.polvisoft.exampleQuery.test.domain.CustomerOrder;
import com.polvisoft.exampleQuery.test.domain.Product;
import com.polvisoft.exampleQuery.test.domain.ProductStock;
import com.polvisoft.exampleQuery.test.repository.impl.CustomerOrderRepositoryImpl;
import com.polvisoft.exampleQuery.test.repository.impl.ProductStockRepositoryImpl;
import com.polvisoft.exampleQuery.utils.Utils;

public class GivenCustomerOrder extends Given<CustomerOrder, CustomerOrderRepositoryImpl> {

	public GivenCustomerOrder(EntityManager entityManager) throws ExampleQueryException {
		super(entityManager);
	}

	private static Logger LOGGER = Logger.getLogger(GivenCustomerOrder.class);

	@Override
	public String[] initCustomFields() {
		String field1 = CustomerOrder.PK;
		String field2 = CustomerOrder.DATE;
		String field3 = CustomerOrder.CUSTOMER + "." + Customer.PK;
		String field4 = CustomerOrder.CUSTOMER + "." + Customer.NAME;
		String field5 = CustomerOrder.CUSTOMER + "." + Customer.LAST_NAME;
		String fields[] = { field1, field2, field3, field4, field5 };
		return fields;
	}

	public static String customerOrderToString(CustomerOrder customerOrder) {
		return "CustomerOrder [pk=" + customerOrder.getPk() + ", date=" + customerOrder.getDate() + ", customer="
				+ customerOrder.getCustomer() + ", productsStock=" + customerOrder.getProductsStock() + "]";
	}

	public CustomerOrder givenACustomerOrder(Customer customer, Date date, List<ProductStock> productsStock)
			throws UniqueException {
		CustomerOrder result = this.givenObjectCustomerOrder(customer, date, productsStock);
		CustomerOrderRepositoryImpl repository = new CustomerOrderRepositoryImpl();
		repository.setEntityManager(this.entityManager);
		repository.save(result);
		if (productsStock.size() > 0) {
			ProductStockRepositoryImpl productStockRepositoryImpl = new ProductStockRepositoryImpl();
			productStockRepositoryImpl.setEntityManager(this.entityManager);
			// TODO: change for list save
			for (ProductStock productStock : productsStock) {
				productStock.setCustomerOrder(result);
				productStockRepositoryImpl.update(productStock);
			}
		}
		LOGGER.info("GivenCustomerOrder instance persisted " + customerOrderToString(result));
		return result;
	}

	public CustomerOrder givenObjectCustomerOrder(Customer customer, Date date, List<ProductStock> productsStock) {
		CustomerOrder result = new CustomerOrder();
		result.setCustomer(customer);
		result.setDate(date);
		result.setProductsStock(productsStock);
		LOGGER.info("GivenCustomerOrder class instance " + customerOrderToString(result));
		return result;
	}

	@Override
	public void givenExamplesEnvironment() throws UniqueException, ExampleQueryException {
		GivenCustomer givenCustomer = new GivenCustomer(this.entityManager);
		Customer customer = givenCustomer.givenADefaultCustomer();
		GivenProduct givenProduct = new GivenProduct(this.entityManager);
		Product product = givenProduct.givenAProduct("CocaCola", "Lata");
		Date dateOrder = Utils.getDate("01/01/2017");
		GivenProductStock givenProductStock = new GivenProductStock(this.entityManager);
		ProductStock productsStock = givenProductStock.givenAProductStock(product, 100);
		List<ProductStock> productsStockList = new ArrayList<>();
		productsStockList.add(productsStock);
		this.givenACustomerOrder(customer, dateOrder, productsStockList);
	}

	@Override
	public FilterMap initFilter() {
		FilterMap filter = new FilterMap();
		// all examples
		filter.put(CustomerOrder.PRODUCTS_STOCK, HqlConditions.IS_NOT_EMPTY);

		// example 1
		filter.put(CustomerOrder.CUSTOMER + "." + Customer.NAME, HqlConditions.NOT_EQUALS);

		// example 2
		filter.put(CustomerOrder.DATE, HqlConditions.LOWER_EQUALS);

		// example 3
		filter.put(CustomerOrder.PRODUCTS_STOCK_IDS, HqlConditions.IN);
		return filter;
	}

	@Override
	public CustomerOrder[] initExamples() throws ExampleQueryException, UniqueException {
		Customer customerExample = new Customer();
		customerExample.setPk(100L);

		CustomerOrder example1 = new CustomerOrder();
		example1.setCustomer(customerExample);

		CustomerOrder example2 = new CustomerOrder();
		customerExample.setName("Jesús");
		example2.setCustomer(customerExample);

		CustomerOrder example3 = new CustomerOrder();
		GivenProduct givenProduct = new GivenProduct(this.entityManager);
		Product product = givenProduct.givenAProduct("Orange", "Color");
		List<Long> productStockIds = new ArrayList<>();
		GivenProductStock givenProductStock = new GivenProductStock(this.entityManager);
		ProductStock productStock = givenProductStock.givenAProductStock(product, 10);
		productStockIds.add(productStock.getPk());
		List<ProductStock> productsStockList = new ArrayList<>();
		productsStockList.add(productStock);
		GivenCustomer givenCustomer = new GivenCustomer(this.entityManager);
		Customer customer = givenCustomer.givenACustomer("Buyer", "cutomer", new Date(), "NO-ID", "DNI");
		GivenCustomerOrder givenCustomerOrder = new GivenCustomerOrder(this.entityManager);
		givenCustomerOrder.givenACustomerOrder(customer, new Date(), productsStockList);
		example3.setProductsStockIds(productStockIds);

		CustomerOrder[] examples = { example1, example2, example3 };
		return examples;
	}

	@Override
	public CustomerOrder initTestSaveInstance() throws ExampleQueryException, UniqueException {
		Customer customer = null;
		GivenCustomer givenCustomer = new GivenCustomer(this.entityManager);
		customer = givenCustomer.givenACustomer("User", "Saved", new Date(), "LKKJHK", "DNI");
		GivenCustomerOrder givenCustomerOrder = new GivenCustomerOrder(this.entityManager);
		return givenCustomerOrder.givenObjectCustomerOrder(customer, new Date(), null);
	}

	@Override
	public Map<String, Object> initTestUpdateValues() {
		Map<String, Object> result = new HashMap<>();
		result.put(CustomerOrder.DATE, new Date());
		return result;
	}

	@Override
	public int initPageNumber() {
		return 0;
	}

	@Override
	public int initPageSize() {
		return 10;
	}

}
