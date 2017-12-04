package foo.bar.test;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import foo.bar.domain.Customer;
import foo.bar.domain.CustomerOrder;
import foo.bar.domain.Product;
import foo.bar.domain.ProductStock;
import foo.bar.exceptions.UniqueException;
import foo.bar.service.impl.CustomerOrderServiceImpl;
import foo.bar.service.impl.CustomerServiceImpl;
import foo.bar.service.impl.ProductServiceImpl;
import foo.bar.service.impl.ProductStockServiceImpl;

public class Given {

	public static ProductStock givenAProductStock(Long pk, Product product, Integer quantity,
			EntityManager entityManager) throws UniqueException {
		ProductStock result = new ProductStock();
		result.setPk(pk);
		result.setProduct(product);
		result.setQuantity(quantity);
		ProductStockServiceImpl service = new ProductStockServiceImpl();
		service.setEntityManager(entityManager);
		service.save(result);
		return result;
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
		return result;
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
		return result;
	}

	public static CustomerOrder givenACustomerOrder(Long pk, Customer customer, Date date, List<ProductStock> productsStock, EntityManager entityManager) throws UniqueException {
		CustomerOrder result = new CustomerOrder();
		result.setCustomer(customer);
		result.setDate(date);
		result.setPk(pk);
		result.setProductsStock(productsStock);
		CustomerOrderServiceImpl service = new CustomerOrderServiceImpl();
		service.setEntityManager(entityManager);
		service.save(result);
		return result;
	}

	public static Customer givenACustomer(Long pk, String name, EntityManager entityManager) throws UniqueException {
		return givenACustomer(pk, name, null, null, null, null, entityManager);
	}

}
