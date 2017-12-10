package foo.bar.test.given;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import foo.bar.domain.Product;
import foo.bar.domain.ProductStock;
import foo.bar.exceptions.UniqueException;
import foo.bar.service.impl.ProductStockServiceImpl;
import foo.bar.service.utils.HqlConditions;
import foo.bar.test.common.Given;

public class GivenProductStock extends Given<ProductStock, ProductStockServiceImpl> {

	public GivenProductStock(EntityManager entityManager) throws InstantiationException, IllegalAccessException {
		super(entityManager);
	}

	private static Logger LOGGER = Logger.getLogger(GivenProductStock.class);

	public ProductStock givenAProductStock(Product product, Integer price) throws UniqueException {
		ProductStock result = givenObjectProductStock(product, price);
		ProductStockServiceImpl service = new ProductStockServiceImpl();
		service.setEntityManager(entityManager);
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
	public void givenExamplesEnvironment() throws InstantiationException, IllegalAccessException, UniqueException {
		GivenProduct givenProduct = new GivenProduct(entityManager);
		Product product = givenProduct.givenAProduct("Samsung", "tv");
		givenAProductStock(product, 6);
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
	public Map<String, Object> initEntityFields() {
		Map<String, Object> mapValues = new HashMap<>();
		mapValues.put(ProductStock.PRODUCT + "." + Product.NAME, "LG");
		mapValues.put(ProductStock.PRODUCT + "." + Product.NAME, "Samsung");
		return mapValues;
	}

	@Override
	public Map<String, HqlConditions> initFilter() {
		Map<String, HqlConditions> filter = new HashMap<>();

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
	public ProductStock initTestSaveInstance() throws UniqueException, InstantiationException, IllegalAccessException {
		GivenProduct givenProduct = new GivenProduct(entityManager);
		Product product = givenProduct.givenAProduct("Samsung", "tv");
		return GivenProductStock.givenObjectProductStock(product, 20);
	}

	@Override
	public Map<String, Object> initTestUpdateValues() {
		Map<String, Object> result = new HashMap<>();
		result.put(ProductStock.PRICE, 500);
		return result;
	}
}
