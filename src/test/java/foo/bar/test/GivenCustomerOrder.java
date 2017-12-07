package foo.bar.test;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import foo.bar.domain.Customer;
import foo.bar.domain.CustomerOrder;
import foo.bar.domain.ProductStock;
import foo.bar.exceptions.UniqueException;
import foo.bar.service.impl.CustomerOrderServiceImpl;
import foo.bar.service.impl.ProductStockServiceImpl;

public class GivenCustomerOrder {

	private static Logger LOGGER = Logger.getLogger(GivenCustomerOrder.class);

	public static String customerOrderToString(CustomerOrder customerOrder) {
		return "CustomerOrder [pk=" + customerOrder.getPk() + ", date=" + customerOrder.getDate() + ", customer="
				+ customerOrder.getCustomer() + ", productsStock=" + customerOrder.getProductsStock() + "]";
	}

	public static CustomerOrder givenACustomerOrder(Customer customer, Date date, List<ProductStock> productsStock,
			EntityManager entityManager) throws UniqueException {
		CustomerOrder result = GivenCustomerOrder.givenObjectCustomerOrder(customer, date, productsStock);
		CustomerOrderServiceImpl service = new CustomerOrderServiceImpl();
		service.setEntityManager(entityManager);
		service.save(result);
		if (productsStock.size() > 0) {
			ProductStockServiceImpl productStockServiceImpl = new ProductStockServiceImpl();
			productStockServiceImpl.setEntityManager(entityManager);
			// TODO: change for list save
			for (ProductStock productStock : productsStock) {
				productStock.setCustomerOrder(result);
				productStockServiceImpl.update(productStock);
			}
		}
		LOGGER.info("GivenProductStock instance persisted " + customerOrderToString(result));
		return result;
	}

	public static CustomerOrder givenObjectCustomerOrder(Customer customer, Date date,
			List<ProductStock> productsStock) {
		CustomerOrder result = new CustomerOrder();
		result.setCustomer(customer);
		result.setDate(date);
		result.setProductsStock(productsStock);
		LOGGER.info("GivenProductStock class instance " + customerOrderToString(result));
		return result;
	}

}
