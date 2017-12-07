package foo.bar.test.given;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import foo.bar.domain.Product;
import foo.bar.exceptions.UniqueException;
import foo.bar.service.impl.ProductServiceImpl;

public class GivenProduct extends Given<Product, ProductServiceImpl> {

	public GivenProduct(EntityManager entityManager) throws InstantiationException, IllegalAccessException {
		super(entityManager);
	}

	private static Logger LOGGER = Logger.getLogger(GivenProduct.class);

	public Product givenAProduct(String name, String description) throws UniqueException {
		Product result = GivenProduct.givenObjectProduct(name, description);
		ProductServiceImpl service = new ProductServiceImpl();
		service.setEntityManager(entityManager);
		service.save(result);
		LOGGER.info("GivenProduct instance persisted " + GivenProduct.productToString(result));
		return result;
	}

	public static Product givenObjectProduct(String name, String description) {
		Product result = new Product();
		result.setName(name);
		result.setDescription(description);
		LOGGER.info("GivenProduct class instance " + GivenProduct.productToString(result));
		return result;
	}

	public static String productToString(Product product) {
		return "Product [pk=" + product.getPk() + ", name=" + product.getName() + ", description="
				+ product.getDescription() + "]";
	}

	@Override
	public void givenExamplesEnviroment() throws UniqueException {
		givenAProduct("Samsung", "television");
		givenAProduct("Apple", "fruit");
	}

	@Override
	public String[] initCustomFields() {
		String field1 = Product.PK;
		String field2 = Product.NAME;
		String field3 = Product.DESCRIPTION;
		String fields[] = { field1, field2, field3 };
		return fields;
	}
}
