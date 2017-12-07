package foo.bar.test.given;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import foo.bar.domain.CustomerOrder;
import foo.bar.domain.Product;
import foo.bar.domain.ProductStock;
import foo.bar.exceptions.UniqueException;
import foo.bar.service.impl.ProductStockServiceImpl;

public class GivenProductStock extends Given<ProductStock, ProductStockServiceImpl> {

	public GivenProductStock(EntityManager entityManager) throws InstantiationException, IllegalAccessException {
		super(entityManager);
	}

	private static Logger LOGGER = Logger.getLogger(GivenProductStock.class);

	public ProductStock givenAProductStock(Product product, Integer quantity, CustomerOrder customerOrder)
			throws UniqueException {
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

	@Override
	public void givenExamplesEnviroment() throws InstantiationException, IllegalAccessException, UniqueException {
		GivenProduct givenProduct = new GivenProduct(entityManager);
		Product product = givenProduct.givenAProduct("Samsung", "tv");
		givenAProductStock(product, 6, null);
	}

	@Override
	public String[] initCustomFields() {
		String field1 = ProductStock.PK;
		String field2 = ProductStock.PRODUCT;
		String field3 = ProductStock.QUANTITY;
		String fields[] = { field1, field2, field3 };
		return fields;
	}
}
