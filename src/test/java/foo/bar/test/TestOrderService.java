package foo.bar.test;

import java.util.Map;

import foo.bar.domain.Order;
import foo.bar.service.impl.OrderServiceImpl;

public class TestOrderService extends TestCommon<OrderServiceImpl, Order>{

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
	protected Order[] initExamples() {
		// TODO Auto-generated method stub
		return null;
	}

}
