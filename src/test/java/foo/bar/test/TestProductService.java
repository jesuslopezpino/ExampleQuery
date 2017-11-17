package foo.bar.test;

import java.util.HashMap;
import java.util.Map;

import foo.bar.domain.Order;
import foo.bar.domain.Product;
import foo.bar.service.impl.ProductServiceImpl;
import foo.bar.service.utils.HqlConditions;

public class TestProductService extends TestCommon<ProductServiceImpl, Product> {

	@Override
	protected Map<String, HqlConditions> initFilter() {
		Map<String, HqlConditions> filter = new HashMap<String, HqlConditions>();
		filter.put(Product.DESCRIPTION, HqlConditions.LIKE_IGNORE_CASE);
		filter.put(Product.NAME, HqlConditions.LIKE_IGNORE_CASE);
		return filter;
	}

	@Override
	protected Product[] initExamples() {
		Product example1 = new Product();
		example1.setName("Apple");
		Product example2 = new Product();
		example2.setDescription("fruit");
		Product[] examples = { example1, example2 };
		return examples;
	}

	@Override
	protected String[] initCustomFields() {
		String field1 = Product.PK;
		String field2 = Product.NAME;
		String field3 = Product.DESCRIPTION;
		String fields[] = {field1, field2, field3};
		return fields;
	}

}