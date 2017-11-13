package foo.bar.test;

import java.util.HashMap;
import java.util.Map;

import foo.bar.domain.Product;
import foo.bar.domain.ProductStock;
import foo.bar.service.impl.ProductStockServiceImpl;
import foo.bar.service.utils.HqlConditions;

public class TestProductStockService extends TestCommon<ProductStockServiceImpl, ProductStock> {

	@Override
	protected Map<String, String> initFilter() {
		Map<String, String> filter = new HashMap<>();
		filter.put(ProductStock.PRODUCT, HqlConditions.NOT_IN);
		filter.put(ProductStock.QUANTITY, HqlConditions.BETWEEN);
		return filter;
	}

	@Override
	protected ProductStock[] initExamples() {
		ProductStock example1 = new ProductStock();
		ProductStock example2 = new ProductStock();
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