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
	public void setUp() throws InstantiationException, IllegalAccessException{
		super.setUp();
		Product product;
		try {
			product = Given.givenAProduct(1L, "Samsung", "tv", entityManager);
			ProductStock productStock = Given.givenAProductStock(1L, product, 200, entityManager);
		} catch (UniqueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	protected Map<String, Object> initEntityFields() {
		Map<String, Object> mapValues = new HashMap<>();
		mapValues.put(ProductStock.PRODUCT_NAME, "LG");
//		mapValues.put(ProductStock.PRODUCT+ "." + Product.NAME, "Samsung");
		return mapValues;
	}
	
	@Override
	protected Map<String, HqlConditions> initFilter() {
		Map<String, HqlConditions> filter = new HashMap<>();
//		filter.put(ProductStock.PRODUCT, HqlConditions.NOT_IN);
////		filter.put(ProductStock.QUANTITY, HqlConditions.BETWEEN);
//		filter.put(ProductStock.PRODUCT_NAME, HqlConditions.LIKE);
//		filter.put(ProductStock.MAX_QUANTITY, HqlConditions.LOWER_EQUALS);
//		filter.put(ProductStock.MIN_QUANTITY, HqlConditions.GREATER_THAN);
		return filter;
	}

	@Override
	protected ProductStock[] initExamples() {
		
		ProductStock example1 = new ProductStock();
		example1.setProductName("Cookies");
		ProductStock example2 = new ProductStock();
		example2.setMaxQuantity(10);
		example2.setMinQuantity(3);
		ProductStock[] examples = { example1, example2 };
		return examples;
	}

	@Override
	protected String[] initCustomFields() {
		String field1 = ProductStock.PK;
		String field2 = ProductStock.PRODUCT;
		String field3 = ProductStock.QUANTITY;
		String fields[] = {field1, field2, field3};
		return fields;
	}
}