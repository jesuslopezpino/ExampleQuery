package foo.bar.test.service;

import java.util.HashMap;
import java.util.Map;

import foo.bar.domain.Product;
import foo.bar.domain.ProductStock;
import foo.bar.exceptions.UniqueException;
import foo.bar.service.impl.ProductStockServiceImpl;
import foo.bar.service.utils.HqlConditions;
import foo.bar.test.TestCommon;
import foo.bar.test.given.GivenProduct;
import foo.bar.test.given.GivenProductStock;

public class TestProductStockService extends TestCommon<ProductStockServiceImpl, ProductStock, GivenProductStock> {

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
	protected ProductStock initSaveEntity() throws UniqueException, InstantiationException, IllegalAccessException {
		GivenProduct givenProduct = new GivenProduct(entityManager);
		Product product = givenProduct.givenAProduct("XBOX", "Video Game");
		return GivenProductStock.givenObjectProductStock(product, 20, null);
	}

	@Override
	protected String initUpdateField() {
		return ProductStock.QUANTITY;
	}

	@Override
	protected Object initUpdateValue() {
		return 500;
	}

}