package foo.bar.test;

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
import foo.bar.service.impl.CustomerServiceImpl;
import foo.bar.service.impl.ProductServiceImpl;
import foo.bar.service.impl.ProductStockServiceImpl;
import foo.bar.utils.Utils;

public class Given {

	private static Logger LOGGER = Logger.getLogger(Given.class);

	public static ProductStock givenAProductStock(Long pk, Product product, Integer quantity,
			CustomerOrder customerOrder, EntityManager entityManager) throws UniqueException {
		ProductStock result = new ProductStock();
		result.setPk(pk);
		result.setProduct(product);
		result.setQuantity(quantity);
		result.setCustomerOrder(customerOrder);
		ProductStockServiceImpl service = new ProductStockServiceImpl();
		service.setEntityManager(entityManager);
		service.save(result);
		LOGGER.info("Given " + productStockToString(result));
		return result;
	}

	public static String productStockToString(ProductStock productStock) {
		return "ProductStock [pk=" + productStock.getPk() + ", customerOrder=" + productStock.getCustomerOrder()
				+ ", product=" + productStock.getProduct() + ", quantity=" + productStock.getQuantity() + "]";
	}

	public static Product givenAProduct(Long pk, String name, String description, EntityManager entityManager)
			throws UniqueException {
		Product result = new Product();
		result.setPk(pk);
		result.setName(name);
		result.setDescription(description);
		ProductServiceImpl service = new ProductServiceImpl();
		service.setEntityManager(entityManager);
		service.save(result);
		LOGGER.info("Given " + productToString(result));
		return result;
	}

	public static String productToString(Product product) {
		return "Product [pk=" + product.getPk() + ", name=" + product.getName() + ", description="
				+ product.getDescription() + "]";
	}

	public static Customer givenADefaultCustomer(EntityManager entityManager) throws UniqueException {
		return givenACustomer(1L, "Jesus", "Lopez", Utils.getDate("10/12/1983 12:00:00", TestCommon.TIME_FORMAT),
				"XXXXXXX", "DNI", entityManager);
	}

	public static Customer givenACustomer(Long pk, String name, String lastName, Date birthDate, String document,
			String documentType, EntityManager entityManager) throws UniqueException {
		Customer result = new Customer();
		result.setPk(pk);
		result.setName(name);
		result.setLastName(lastName);
		result.setBirthDate(birthDate);
		result.setDocument(document);
		result.setDocumentType(documentType);
		CustomerServiceImpl service = new CustomerServiceImpl();
		service.setEntityManager(entityManager);
		service.save(result);
		LOGGER.info("Given " + customerToString(result));
		return result;
	}

	public static String customerToString(Customer customer) {
		return "Customer [pk=" + customer.getPk() + ", name=" + customer.getName() + ", lastName="
				+ customer.getLastName() + ", document=" + customer.getDocument() + ", documentType="
				+ customer.getDocumentType() + ", birthDate=" + customer.getBirthDate() + ", customerOrders="
				+ customer.getCustomerOrders() + "]";
	}

	public static CustomerOrder givenACustomerOrder(Long pk, Customer customer, Date date,
			List<ProductStock> productsStock, EntityManager entityManager) throws UniqueException {
		CustomerOrder result = new CustomerOrder();
		result.setCustomer(customer);
		result.setDate(date);
		result.setPk(pk);
		result.setProductsStock(productsStock);
		CustomerOrderServiceImpl service = new CustomerOrderServiceImpl();
		service.setEntityManager(entityManager);
		service.save(result);
		if (productsStock.size() > 0) {
			ProductStockServiceImpl productStockServiceImpl = new ProductStockServiceImpl();
			productStockServiceImpl.setEntityManager(entityManager);
			for (ProductStock productStock : productsStock) {
				productStock.setCustomerOrder(result);
				productStockServiceImpl.update(productStock);
			}
		}
		LOGGER.info("Given " + customerOrderToString(result));
		return result;
	}

	public static String customerOrderToString(CustomerOrder customerOrder) {
		return "CustomerOrder [pk=" + customerOrder.getPk() + ", date=" + customerOrder.getDate() + ", customer="
				+ customerOrder.getCustomer() + ", productsStock=" + customerOrder.getProductsStock() + "]";
	}
}
