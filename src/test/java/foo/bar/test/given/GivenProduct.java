package foo.bar.test.given;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import foo.bar.domain.Product;
import foo.bar.exceptions.UniqueException;
import foo.bar.filter.FilterMap;
import foo.bar.service.impl.ProductServiceImpl;
import foo.bar.service.utils.HqlConditions;
import foo.bar.test.common.Given;

public class GivenProduct extends Given<Product, ProductServiceImpl> {

	public GivenProduct(EntityManager entityManager) throws InstantiationException, IllegalAccessException {
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
		filter.put(Product.NAME, HqlConditions.LIKE_IGNORE_CASE);

		// example 2
		filter.put(Product.DESCRIPTION, HqlConditions.LIKE_IGNORE_CASE);

		// example 3
		filter.put(Product.PK_LIST, HqlConditions.NOT_IN);

		return filter;
	}

	@Override
	public Product[] initExamples() {

		Product example1 = new Product();
		example1.setName("Apple");

		Product example2 = new Product();
		example2.setDescription("fruit");

		Product example3 = new Product();
		List<Long> notList = new ArrayList<>();
		notList.add(9L);
		notList.add(99L);
		notList.add(999L);
		example3.setPkList(notList);

		Product[] examples = { example1, example2, example3 };
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
}
