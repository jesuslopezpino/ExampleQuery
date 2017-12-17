package com.polvisoft.exampleQuery.test.test.given;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.polvisoft.exampleQuery.test.domain.Product;
import com.polvisoft.exampleQuery.test.service.impl.ProductServiceImpl;
import com.polvisoft.exampleQuery.test.test.common.Given;
import com.polvisoft.exceptions.ExampleQueryException;
import com.polvisoft.exceptions.UniqueException;
import com.polvisoft.filter.FilterAddCondition;
import com.polvisoft.filter.FilterMap;
import com.polvisoft.service.utils.HqlConditions;

public class GivenProduct extends Given<Product, ProductServiceImpl> {

	public GivenProduct(EntityManager entityManager) throws ExampleQueryException {
		super(entityManager);
	}

	private static Logger LOGGER = Logger.getLogger(GivenProduct.class);

	public Product givenAProduct(String name, String description) throws UniqueException {
		Product result = GivenProduct.givenObjectProduct(name, description);
		ProductServiceImpl service = new ProductServiceImpl();
		service.setEntityManager(this.entityManager);
		service.save(result);
		LOGGER.info("GivenProduct instance persisted " + GivenProduct.productToString(result));
		return result;
	}

	public static Product givenObjectProduct(String name, String description) {
		Product result = new Product();
		result.setName(name);
		result.setDescription(description);
		LOGGER.info("GivenProduct class instance " + GivenProduct.productToString(result));
		return result;
	}

	public static String productToString(Product product) {
		return "Product [pk=" + product.getPk() + ", name=" + product.getName() + ", description="
				+ product.getDescription() + "]";
	}

	@Override
	public void givenExamplesEnvironment() throws UniqueException {
		this.givenAProduct("Samsung", "television");
		this.givenAProduct("Apple", "fruit");
	}

	@Override
	public String[] initCustomFields() {
		String field1 = Product.PK;
		String field2 = Product.NAME;
		String field3 = Product.DESCRIPTION;
		String fields[] = { field1, field2, field3 };
		return fields;
	}

	@Override
	public Map<String, Object> initEntityFields() {
		Map<String, Object> mapValues = new HashMap<>();
		mapValues.put(Product.DESCRIPTION, "television");
		mapValues.put(Product.NAME, "Samsung");
		return mapValues;
	}

	@Override
	public FilterMap initFilter() {
		FilterMap filter = new FilterMap();

		// example 1
		FilterMap nameOrDescription = new FilterMap(FilterAddCondition.OR);
		nameOrDescription.put(Product.NAME, HqlConditions.EQUALS);
		nameOrDescription.put(Product.DESCRIPTION, HqlConditions.EQUALS);
		filter.put(nameOrDescription);

		// example 2
		FilterMap pkAndPkList = new FilterMap(FilterAddCondition.AND);
		pkAndPkList.put(Product.PK_LIST, HqlConditions.NOT_IN);
		pkAndPkList.put(Product.PK, HqlConditions.NOT_EQUALS);
		filter.put(pkAndPkList);
		return filter;
	}

	@Override
	public Product[] initExamples() {

		Product example1 = new Product();
		example1.setName("Apple");
		example1.setDescription("fruit");

		Product example2 = new Product();
		example2.setPk(888888L);
		List<Long> notList = new ArrayList<>();
		notList.add(99999L);
		notList.add(99998L);
		notList.add(99997L);
		example2.setPkList(notList);

		Product example3 = new Product();
		example3.setName(example1.getName());
		example3.setDescription(example1.getDescription());
		example3.setPk(example2.getPk());
		example3.setPkList(example2.getPkList());
		Product[] examples = {
				// example1, example2,
				example3 };
		return examples;
	}

	@Override
	public Product initTestSaveInstance() throws UniqueException {
		return GivenProduct.givenObjectProduct("P NAME", "P DESC");
	}

	@Override
	public Map<String, Object> initTestUpdateValues() {
		Map<String, Object> result = new HashMap<>();
		result.put(Product.NAME, "new name value");
		result.put(Product.DESCRIPTION, "new description value");
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
