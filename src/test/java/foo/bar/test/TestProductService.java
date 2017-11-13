package foo.bar.test;

import java.util.List;
import java.util.Map;

import foo.bar.domain.Product;
import foo.bar.service.impl.ProductServiceImpl;

public class TestProductService extends TestCommon<ProductServiceImpl, Product>{

	@Override
	public void testFindByExample() {
		// TODO Auto-generated method stub
	}

	@Override
	protected Map<String, String> initFilter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Product[] initExamples() {
		// TODO Auto-generated method stub
		return null;
	}

}