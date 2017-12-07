package foo.bar.test.given;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import foo.bar.domain.CustomerOrder;
import foo.bar.domain.Product;
import foo.bar.domain.ProductStock;
import foo.bar.exceptions.UniqueException;
import foo.bar.service.impl.ProductStockServiceImpl;
import foo.bar.service.utils.HqlConditions;

public class GivenProductStock extends Given<ProductStock, ProductStockServiceImpl> {

	public GivenProductStock(EntityManager entityManager) throws InstantiationException, IllegalAccessException {
		super(entityManager);
	}

	private static Logger LOGGER = Logger.getLogger(GivenProductStock.class);

	public ProductStock givenAProductStock(Product product, Integer quantity, CustomerOrder customerOrder)
			throws UniqueException {
		ProductStock result = givenObjectProductStock(product, quantity, customerOrder);
		ProductStockServiceImpl service = new ProductStockServiceImpl();
		service.setEntityManager(entityManager);
		service.save(result);
		LOGGER.info("GivenProductStock instance persisted " + productStockToString(result));
		return result;
	}

	public static ProductStock givenObjectProductStock(Product product, Integer quantity, CustomerOrder customerOrder) {
		ProductStock result = new ProductStock();
		result.setProduct(product);
		result.setQuantity(quantity);
		result.setCustomerOrder(customerOrder);
		LOGGER.info("GivenProductStock class instance " + productStockToString(result));
		return result;
	}

	public static String productStockToString(ProductStock productStock) {
		return "ProductStock [pk=" + productStock.getPk() + ", customerOrder=" + productStock.getCustomerOrder()
				+ ", product=" + productStock.getProduct() + ", quantity=" + productStock.getQuantity() + "]";
	}

	@Override
	public void givenExamplesEnviroment() throws InstantiationException, IllegalAccessException, UniqueException {
		GivenProduct givenProduct = new GivenProduct(entityManager);
		Product product = givenProduct.givenAProduct("Samsung", "tv");
		givenAProductStock(product, 6, null);
	}

	@Override
	public String[] initCustomFields() {
		String field1 = ProductStock.PK;
		String field2 = ProductStock.PRODUCT;
		String field3 = ProductStock.QUANTITY;
		String fields[] = { field1, field2, field3 };
		return fields;
	}

	@Override
	public String initTestUpdateField() {
		return ProductStock.QUANTITY;
	}

	@Override
	public Map<String, Object> initEntityFields() {
		Map<String, Object> mapValues = new HashMap<>();
		mapValues.put(ProductStock.PRODUCT_NAME, "LG");
		mapValues.put(ProductStock.PRODUCT + "." + Product.NAME, "Samsung");
		return mapValues;
	}

	@Override
	public Map<String, HqlConditions> initFilter() {
		Map<String, HqlConditions> filter = new HashMap<>();

		// example 1
		filter.put(ProductStock.PRODUCT_NAME, HqlConditions.LIKE_IGNORE_CASE);

		// example 2
		filter.put(ProductStock.MAX_QUANTITY, HqlConditions.LOWER_EQUALS);
		filter.put(ProductStock.MIN_QUANTITY, HqlConditions.GREATER_THAN);

		// example 3
		filter.put(ProductStock.PK, HqlConditions.NOT_EQUALS);
		filter.put(ProductStock.CUSTOMER_ORDER, HqlConditions.IS_NULL);
		return filter;
	}

	@Override
	public ProductStock[] initExamples() {

		ProductStock example1 = new ProductStock();
		example1.setProductName("Samsung");

		ProductStock example2 = new ProductStock();
		example2.setMaxQuantity(10);
		example2.setMinQuantity(3);

		ProductStock example3 = new ProductStock();
		example3.setPk(7L);

		ProductStock[] examples = { example1, example2, example3 };
		return examples;
	}

	@Override
	public ProductStock initTestSaveInstance() throws UniqueException, InstantiationException, IllegalAccessException {
		GivenProduct givenProduct = new GivenProduct(entityManager);
		Product product = givenProduct.givenAProduct("XBOX", "Video Game");
		return GivenProductStock.givenObjectProductStock(product, 20, null);
	}

	@Override
	public Object initTestUpdateValue() {
		return 500;
	}
}
