package com.polvisoft.exampleQuery.test.tests.given;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.polvisoft.exampleQuery.enums.HqlConditions;
import com.polvisoft.exampleQuery.exceptions.ExampleQueryException;
import com.polvisoft.exampleQuery.exceptions.UniqueException;
import com.polvisoft.exampleQuery.filter.FilterAddCondition;
import com.polvisoft.exampleQuery.filter.FilterMap;
import com.polvisoft.exampleQuery.test.common.Given;
import com.polvisoft.exampleQuery.test.domain.Product;
import com.polvisoft.exampleQuery.test.domain.ProductStock;
import com.polvisoft.exampleQuery.test.repository.impl.ProductStockRepositoryImpl;

public class GivenProductStock extends Given<ProductStock, ProductStockRepositoryImpl> {

	public GivenProductStock(EntityManager entityManager) throws ExampleQueryException {
		super(entityManager);
	}

	private static Logger LOGGER = Logger.getLogger(GivenProductStock.class);

	public ProductStock givenAProductStock(Product product, Integer price) throws UniqueException {
		ProductStock result = givenObjectProductStock(product, price);
		ProductStockRepositoryImpl service = new ProductStockRepositoryImpl();
		service.setEntityManager(this.entityManager);
		service.save(result);
		LOGGER.info("GivenProductStock instance persisted " + productStockToString(result));
		return result;
	}

	public static ProductStock givenObjectProductStock(Product product, Integer price) {
		ProductStock result = new ProductStock();
		result.setProduct(product);
		result.setPrice(price);
		result.setCustomerOrder(null);
		LOGGER.info("GivenProductStock class instance " + productStockToString(result));
		return result;
	}

	public static String productStockToString(ProductStock productStock) {
		return "ProductStock [pk=" + productStock.getPk() + ", customerOrder=" + productStock.getCustomerOrder()
				+ ", product=" + productStock.getProduct() + ", price=" + productStock.getPrice() + "]";
	}

	@Override
	public void givenExamplesEnvironment() throws ExampleQueryException, UniqueException {
		GivenProduct givenProduct = new GivenProduct(this.entityManager);
		Product product = givenProduct.givenAProduct("Samsung", "tv");
		this.givenAProductStock(product, 6);
	}

	@Override
	public String[] initCustomFields() {
		String field1 = ProductStock.PK;
		String field2 = ProductStock.PRODUCT;
		String field3 = ProductStock.PRICE;
		String fields[] = { field1, field2, field3 };
		return fields;
	}

	@Override
	public FilterMap initFilter() {
		FilterMap filter = new FilterMap(FilterAddCondition.OR);

		// All examples
		// filter.put(ProductStock.CUSTOMER_ORDER, HqlConditions.IS_NULL);

		// example 1
		filter.put(ProductStock.PRODUCT + "." + Product.NAME, HqlConditions.LIKE_IGNORE_CASE);

		// example 2
		filter.put(ProductStock.MAX_PRICE, HqlConditions.LOWER_EQUALS);
		filter.put(ProductStock.MIN_PRICE, HqlConditions.GREATER_THAN);

		// example 3
		filter.put(ProductStock.PK, HqlConditions.NOT_EQUALS);
		return filter;
	}

	@Override
	public ProductStock[] initExamples() {

		ProductStock example1 = new ProductStock();
		Product product = new Product();
		product.setName("Samsung");
		example1.setProduct(product);
		// example1.setProductName("Samsung");

		ProductStock example2 = new ProductStock();
		example2.setMaxPrice(10);
		example2.setMinPrice(3);

		ProductStock example3 = new ProductStock();
		example3.setPk(9999L);

		ProductStock[] examples = { example1, example2, example3 };
		return examples;
	}

	@Override
	public ProductStock initTestSaveInstance() throws UniqueException, ExampleQueryException {
		GivenProduct givenProduct = new GivenProduct(this.entityManager);
		Product product = givenProduct.givenAProduct("Samsung", "tv");
		return GivenProductStock.givenObjectProductStock(product, 20);
	}

	@Override
	public Map<String, Object> initTestUpdateValues() {
		Map<String, Object> result = new HashMap<>();
		result.put(ProductStock.PRICE, 500);
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
