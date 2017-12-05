package foo.bar.test;

import java.util.HashMap;
import java.util.Map;

import foo.bar.domain.Product;
import foo.bar.domain.ProductStock;
import foo.bar.exceptions.UniqueException;
import foo.bar.service.impl.ProductStockServiceImpl;
import foo.bar.service.utils.HqlConditions;

public class TestProductStockService extends TestCommon<ProductStockServiceImpl, ProductStock> {

	@Override
	public void setUp() throws InstantiationException, IllegalAccessException {
		super.setUp();
		Product product;
		try {
			product = Given.givenAProduct(1L, "Samsung", "tv", entityManager);
			ProductStock productStock = Given.givenAProductStock(1L, product, 6, null, entityManager);
		} catch (UniqueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected Map<String, Object> initEntityFields() {
		Map<String, Object> mapValues = new HashMap<>();
		mapValues.put(ProductStock.PRODUCT_NAME, "LG");
		mapValues.put(ProductStock.PRODUCT + "." + Product.NAME, "Samsung");
		return mapValues;
	}

	@Override
	protected Map<String, HqlConditions> initFilter() {
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
	protected ProductStock[] initExamples() {

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
	protected String[] initCustomFields() {
		String field1 = ProductStock.PK;
		String field2 = ProductStock.PRODUCT;
		String field3 = ProductStock.QUANTITY;
		String fields[] = { field1, field2, field3 };
		return fields;
	}

	@Override
	protected ProductStock initSaveEntity() throws UniqueException {
		Product product = Given.givenAProduct(45L, "XBOX", "Video Game", entityManager);
		ProductStock result = Given.givenObjectProductStock(44L, product, 20, null);
		return result;
	}
}