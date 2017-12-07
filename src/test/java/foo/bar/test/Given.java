package foo.bar.test;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import foo.bar.domain.Customer;
import foo.bar.domain.CustomerOrder;
import foo.bar.domain.Note;
import foo.bar.domain.Product;
import foo.bar.domain.ProductStock;
import foo.bar.exceptions.UniqueException;
import foo.bar.service.impl.CustomerOrderServiceImpl;
import foo.bar.service.impl.CustomerServiceImpl;
import foo.bar.service.impl.NoteServiceImpl;
import foo.bar.service.impl.ProductServiceImpl;
import foo.bar.service.impl.ProductStockServiceImpl;
import foo.bar.utils.Utils;

public class Given {

	private static Logger LOGGER = Logger.getLogger(Given.class);

	public static ProductStock givenAProductStock(Product product, Integer quantity, CustomerOrder customerOrder,
			EntityManager entityManager) throws UniqueException {
		ProductStock result = givenObjectProductStock(product, quantity, customerOrder);
		ProductStockServiceImpl service = new ProductStockServiceImpl();
		service.setEntityManager(entityManager);
		service.save(result);
		LOGGER.info("Given instance persisted " + productStockToString(result));
		return result;
	}

	public static ProductStock givenObjectProductStock(Product product, Integer quantity, CustomerOrder customerOrder) {
		ProductStock result = new ProductStock();
		result.setProduct(product);
		result.setQuantity(quantity);
		result.setCustomerOrder(customerOrder);
		LOGGER.info("Given class instance " + productStockToString(result));
		return result;
	}

	public static String productStockToString(ProductStock productStock) {
		return "ProductStock [pk=" + productStock.getPk() + ", customerOrder=" + productStock.getCustomerOrder()
				+ ", product=" + productStock.getProduct() + ", quantity=" + productStock.getQuantity() + "]";
	}

	public static Product givenAProduct(String name, String description, EntityManager entityManager)
			throws UniqueException {
		Product result = givenObjectProduct(name, description);
		ProductServiceImpl service = new ProductServiceImpl();
		service.setEntityManager(entityManager);
		service.save(result);
		LOGGER.info("Given instance persisted " + productToString(result));
		return result;
	}

	public static Product givenObjectProduct(String name, String description) {
		Product result = new Product();
		result.setName(name);
		result.setDescription(description);
		LOGGER.info("Given class instance " + productToString(result));
		return result;
	}

	public static String productToString(Product product) {
		return "Product [pk=" + product.getPk() + ", name=" + product.getName() + ", description="
				+ product.getDescription() + "]";
	}

	public static Customer givenADefaultCustomer(EntityManager entityManager) throws UniqueException {
		return givenACustomer("Jesus", "Lopez", Utils.getDateTime("10/12/1983 12:00:00"), "XXXXXXX", "DNI",
				entityManager);
	}

	public static Customer givenACustomer(String name, String lastName, Date birthDate, String document,
			String documentType, EntityManager entityManager) throws UniqueException {
		Customer result = givenObjectCustomer(name, lastName, birthDate, document, documentType);
		CustomerServiceImpl service = new CustomerServiceImpl();
		service.setEntityManager(entityManager);
		result = service.save(result);
		LOGGER.info("Given instance persisted " + customerToString(result));
		return result;
	}

	public static Customer givenObjectCustomer(String name, String lastName, Date birthDate, String document,
			String documentType) {
		Customer result = new Customer();
		result.setName(name);
		result.setLastName(lastName);
		result.setBirthDate(birthDate);
		result.setDocument(document);
		result.setDocumentType(documentType);
		LOGGER.info("Given class instance " + customerToString(result));
		return result;
	}

	public static String customerToString(Customer customer) {
		return "Customer [pk=" + customer.getPk() + ", name=" + customer.getName() + ", lastName="
				+ customer.getLastName() + ", document=" + customer.getDocument() + ", documentType="
				+ customer.getDocumentType() + ", birthDate=" + customer.getBirthDate() + ", customerOrders="
				+ customer.getCustomerOrders() + "]";
	}

	public static CustomerOrder givenACustomerOrder(Customer customer, Date date, List<ProductStock> productsStock,
			EntityManager entityManager) throws UniqueException {
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
		LOGGER.info("Given instance persisted " + customerOrderToString(result));
		return result;
	}

	public static CustomerOrder givenObjectCustomerOrder(Customer customer, Date date,
			List<ProductStock> productsStock) {
		CustomerOrder result = new CustomerOrder();
		result.setCustomer(customer);
		result.setDate(date);
		result.setProductsStock(productsStock);
		LOGGER.info("Given class instance " + customerOrderToString(result));
		return result;
	}

	public static String customerOrderToString(CustomerOrder customerOrder) {
		return "CustomerOrder [pk=" + customerOrder.getPk() + ", date=" + customerOrder.getDate() + ", customer="
				+ customerOrder.getCustomer() + ", productsStock=" + customerOrder.getProductsStock() + "]";
	}

	public static String noteToString(Note note) {
		return "Note [pk=" + note.getPk() + ", date=" + note.getDate() + ", note=" + note.getNote() + ", customer="
				+ note.getCustomer() + "]";
	}

	public static Note givenANote(Date date, Customer customer, String note, EntityManager entityManager)
			throws UniqueException {
		Note result = givenObjectNote(date, customer, note);
		NoteServiceImpl service = new NoteServiceImpl();
		service.setEntityManager(entityManager);
		result = service.save(result);
		LOGGER.info("Given instance persisted " + noteToString(result));
		return result;
	}

	public static Note givenObjectNote(Date date, Customer customer, String note) {
		Note result = new Note();
		result.setDate(date);
		result.setCustomer(customer);
		result.setNote(note);
		LOGGER.info("Given class instance " + noteToString(result));
		return result;
	}

}
