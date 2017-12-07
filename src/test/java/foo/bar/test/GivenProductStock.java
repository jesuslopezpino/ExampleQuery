package foo.bar.test;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import foo.bar.domain.CustomerOrder;
import foo.bar.domain.Product;
import foo.bar.domain.ProductStock;
import foo.bar.exceptions.UniqueException;
import foo.bar.service.impl.ProductStockServiceImpl;

public class GivenProductStock {

	private static Logger LOGGER = Logger.getLogger(GivenProductStock.class);

	public static ProductStock givenAProductStock(Product product, Integer quantity, CustomerOrder customerOrder,
			EntityManager entityManager) throws UniqueException {
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

}
