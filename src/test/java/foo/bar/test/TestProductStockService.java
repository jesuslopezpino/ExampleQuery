package foo.bar.test;

import java.util.Map;

import foo.bar.domain.ProductStock;
import foo.bar.service.impl.ProductStockServiceImpl;

public class TestProductStockService extends TestCommon<ProductStockServiceImpl, ProductStock>{

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
	protected ProductStock[] initExamples() {
		// TODO Auto-generated method stub
		return null;
	}

}