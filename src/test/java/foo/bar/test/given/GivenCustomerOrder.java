package foo.bar.test.given;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import foo.bar.domain.Customer;
import foo.bar.domain.CustomerOrder;
import foo.bar.domain.Product;
import foo.bar.domain.ProductStock;
import foo.bar.exceptions.UniqueException;
import foo.bar.service.impl.CustomerOrderServiceImpl;
import foo.bar.service.impl.ProductStockServiceImpl;
import foo.bar.utils.Utils;

public class GivenCustomerOrder extends Given<CustomerOrder, CustomerOrderServiceImpl> {

	public GivenCustomerOrder(EntityManager entityManager) throws InstantiationException, IllegalAccessException {
		super(entityManager);
	}

	private static Logger LOGGER = Logger.getLogger(GivenCustomerOrder.class);

	public static String customerOrderToString(CustomerOrder customerOrder) {
		return "CustomerOrder [pk=" + customerOrder.getPk() + ", date=" + customerOrder.getDate() + ", customer="
				+ customerOrder.getCustomer() + ", productsStock=" + customerOrder.getProductsStock() + "]";
	}

	public CustomerOrder givenACustomerOrder(Customer customer, Date date, List<ProductStock> productsStock)
			throws UniqueException {
		CustomerOrder result = givenObjectCustomerOrder(customer, date, productsStock);
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
		LOGGER.info("GivenCustomerOrder instance persisted " + customerOrderToString(result));
		return result;
	}

	public CustomerOrder givenObjectCustomerOrder(Customer customer, Date date, List<ProductStock> productsStock) {
		CustomerOrder result = new CustomerOrder();
		result.setCustomer(customer);
		result.setDate(date);
		result.setProductsStock(productsStock);
		LOGGER.info("GivenCustomerOrder class instance " + customerOrderToString(result));
		return result;
	}

	@Override
	public void givenExamplesEnviroment() throws UniqueException, InstantiationException, IllegalAccessException {
		GivenCustomer givenCustomer = new GivenCustomer(entityManager);
		Customer customer = givenCustomer.givenADefaultCustomer();
		GivenProduct givenProduct = new GivenProduct(entityManager);
		Product product = givenProduct.givenAProduct("CocaCola", "Lata");
		Date dateOrder = Utils.getDate("01/01/2017");
		GivenProductStock givenProductStock = new GivenProductStock(entityManager);
		ProductStock productsStock = givenProductStock.givenAProductStock(product, 100, null);
		List<ProductStock> productsStockList = new ArrayList<>();
		productsStockList.add(productsStock);
		givenACustomerOrder(customer, dateOrder, productsStockList);
	}

}
