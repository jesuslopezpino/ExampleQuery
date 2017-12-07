package foo.bar.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import foo.bar.domain.Product;
import foo.bar.exceptions.UniqueException;
import foo.bar.service.impl.ProductServiceImpl;
import foo.bar.service.utils.HqlConditions;

public class TestProductService extends TestCommon<ProductServiceImpl, Product> {

	protected void givenExamplesEnviroment() throws UniqueException {
		super.logGivenEnviromentStart();
		GivenProduct.givenAProduct("Samsung", "television", entityManager);
		GivenProduct.givenAProduct("Apple", "fruit", entityManager);
	}

	@Override
	protected Map<String, Object> initEntityFields() {
		Map<String, Object> mapValues = new HashMap<>();
		mapValues.put(Product.DESCRIPTION, "television");
		mapValues.put(Product.NAME, "Samsung");
		return mapValues;
	}

	@Override
	protected Map<String, HqlConditions> initFilter() {
		Map<String, HqlConditions> filter = new HashMap<String, HqlConditions>();

		// example 1
		filter.put(Product.NAME, HqlConditions.LIKE_IGNORE_CASE);

		// example 2
		filter.put(Product.DESCRIPTION, HqlConditions.LIKE_IGNORE_CASE);

		// example 3
		filter.put(Product.PK_LIST, HqlConditions.NOT_IN);

		return filter;
	}

	@Override
	protected Product[] initExamples() {

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
	protected String[] initCustomFields() {
		String field1 = Product.PK;
		String field2 = Product.NAME;
		String field3 = Product.DESCRIPTION;
		String fields[] = { field1, field2, field3 };
		return fields;
	}

	@Override
	protected Product initSaveEntity() throws UniqueException {
		return GivenProduct.givenObjectProduct("P NAME", "P DESC");
	}

	@Override
	protected String initUpdateField() {
		return Product.DESCRIPTION;
	}

	@Override
	protected Object initUpdateValue() {
		return "new description";
	}

}