package foo.bar.test.given;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import foo.bar.domain.Customer;
import foo.bar.domain.Product;
import foo.bar.domain.ProductStock;
import foo.bar.exceptions.UniqueException;
import foo.bar.service.impl.CustomerServiceImpl;
import foo.bar.utils.Utils;

public class GivenCustomer extends Given<Customer, CustomerServiceImpl> {

	public GivenCustomer(EntityManager entityManager) throws InstantiationException, IllegalAccessException {
		super(entityManager);
	}

	private static Logger LOGGER = Logger.getLogger(GivenCustomer.class);

	public Customer givenADefaultCustomer(EntityManager entityManager) throws UniqueException {
		return givenACustomer("Jesus", "Lopez", Utils.getDateTime("10/12/1983 12:00:00"), "XXXXXXX", "DNI");
	}

	public Customer givenACustomer(String name, String lastName, Date birthDate, String document, String documentType)
			throws UniqueException {
		Customer result = givenObjectCustomer(name, lastName, birthDate, document, documentType);
		result = service.save(result);
		LOGGER.info("GivenCustomer instance persisted " + customerToString(result));
		return result;
	}

	public Customer givenObjectCustomer(String name, String lastName, Date birthDate, String document,
			String documentType) {
		Customer result = new Customer();
		result.setName(name);
		result.setLastName(lastName);
		result.setBirthDate(birthDate);
		result.setDocument(document);
		result.setDocumentType(documentType);
		LOGGER.info("GivenCustomer class instance " + customerToString(result));
		return result;
	}

	public static String customerToString(Customer customer) {
		return "Customer [pk=" + customer.getPk() + ", name=" + customer.getName() + ", lastName="
				+ customer.getLastName() + ", document=" + customer.getDocument() + ", documentType="
				+ customer.getDocumentType() + ", birthDate=" + customer.getBirthDate() + ", customerOrders="
				+ customer.getCustomerOrders() + "]";
	}

	@Override
	public void givenExamplesEnviroment() throws UniqueException, InstantiationException, IllegalAccessException {
		// TODO Auto-generated method stub
		Customer customer = givenADefaultCustomer(entityManager);
		List<ProductStock> productsStock = new ArrayList<>();
		GivenProduct givenProduct = new GivenProduct(entityManager);
		Product product = givenProduct.givenAProduct("Pizza", "Pizza");
		GivenProductStock givenProductStock = new GivenProductStock(entityManager);
		ProductStock productStock = givenProductStock.givenAProductStock(product, 7, null);
		productsStock.add(productStock);
		GivenCustomerOrder givenCustomerOrder = new GivenCustomerOrder(entityManager);
		givenCustomerOrder.givenACustomerOrder(customer, Utils.getDateTime("01/01/2017 00:00:00"), productsStock);
		givenACustomer("One", "Customer", new Date(), "DNIXXX", "TYPE");
		givenACustomer("Two", "Customer", new Date(), "DNIXXX", "TYPE");
		givenACustomer("Three", "Customer", new Date(), "DNIXXX", "TYPE");
	}

}
