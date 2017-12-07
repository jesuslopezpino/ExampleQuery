package foo.bar.test.given;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import foo.bar.domain.Product;
import foo.bar.exceptions.UniqueException;
import foo.bar.service.impl.ProductServiceImpl;

public class GivenProduct extends Given<Product, ProductServiceImpl>{

	private static Logger LOGGER = Logger.getLogger(GivenProduct.class);

	public static Product givenAProduct(String name, String description, EntityManager entityManager)
			throws UniqueException {
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
	public void givenExamplesEnviroment() {
		// TODO Auto-generated method stub
		
	}

}
