package foo.bar.test.given;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import foo.bar.domain.Customer;
import foo.bar.domain.CustomerOrder;
import foo.bar.domain.Product;
import foo.bar.domain.ProductStock;
import foo.bar.exceptions.UniqueException;
import foo.bar.service.impl.CustomerOrderServiceImpl;
import foo.bar.service.impl.ProductStockServiceImpl;
import foo.bar.service.utils.HqlConditions;
import foo.bar.utils.Utils;

public class GivenCustomerOrder extends Given<CustomerOrder, CustomerOrderServiceImpl> {

	public GivenCustomerOrder(EntityManager entityManager) throws InstantiationException, IllegalAccessException {
		super(entityManager);
	}

	private static Logger LOGGER = Logger.getLogger(GivenCustomerOrder.class);

	@Override
	public String[] initCustomFields() {
		String field1 = CustomerOrder.PK;
		String field2 = CustomerOrder.DATE;
		String field3 = CustomerOrder.CUSTOMER + "." + Customer.PK;
		String field4 = CustomerOrder.CUSTOMER + "." + Customer.NAME;
		String field5 = CustomerOrder.CUSTOMER + "." + Customer.LAST_NAME;
		String fields[] = { field1, field2, field3, field4, field5 };
		return fields;
	}

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
	public void givenExamplesEnvironment() throws UniqueException, InstantiationException, IllegalAccessException {
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

	@Override
	public Map<String, Object> initEntityFields() {
		Map<String, Object> mapValues = new HashMap<>();
		mapValues.put(CustomerOrder.DATE, new Date());
		mapValues.put(CustomerOrder.CUSTOMER + "." + Customer.NAME, "Jesus");
		return mapValues;
	}

	@Override
	public Map<String, HqlConditions> initFilter() {
		Map<String, HqlConditions> filter = new HashMap<String, HqlConditions>();

		// all examples
		filter.put(CustomerOrder.PRODUCTS_STOCK, HqlConditions.IS_NOT_EMPTY);

		// example 1
		filter.put(CustomerOrder.CUSTOMER, HqlConditions.NOT_EQUALS);

		// example 2
		filter.put(CustomerOrder.DATE, HqlConditions.LOWER_EQUALS);

		// example 3
		filter.put(CustomerOrder.PRODUCTS_STOCK_IDS, HqlConditions.IN);
		return filter;
	}

	@Override
	public CustomerOrder[] initExamples() throws UniqueException, InstantiationException, IllegalAccessException {
		Customer customerExample = new Customer();
		customerExample.setPk(100L);

		CustomerOrder example1 = new CustomerOrder();
		example1.setCustomer(customerExample);

		CustomerOrder example2 = new CustomerOrder();
		example2.setDate(new Date());

		CustomerOrder example3 = new CustomerOrder();
		GivenProduct givenProduct = new GivenProduct(entityManager);
		Product product = givenProduct.givenAProduct("Orange", "Color");
		List<Long> productStockIds = new ArrayList<>();
		GivenProductStock givenProductStock = new GivenProductStock(entityManager);
		ProductStock productStock = givenProductStock.givenAProductStock(product, 10, null);
		productStockIds.add(productStock.getPk());
		List<ProductStock> productsStockList = new ArrayList<>();
		productsStockList.add(productStock);
		GivenCustomer givenCustomer = new GivenCustomer(entityManager);
		Customer customer = givenCustomer.givenACustomer("Buyer", "cutomer", new Date(), "NO-ID", "DNI");
		GivenCustomerOrder givenCustomerOrder = new GivenCustomerOrder(entityManager);
		givenCustomerOrder.givenACustomerOrder(customer, new Date(), productsStockList);
		example3.setProductsStockIds(productStockIds);

		CustomerOrder[] examples = { example1, example2, example3 };
		return examples;
	}

	@Override
	public CustomerOrder initTestSaveInstance() throws UniqueException, InstantiationException, IllegalAccessException {
		Customer customer = null;
		GivenCustomer givenCustomer = new GivenCustomer(entityManager);
		customer = givenCustomer.givenACustomer("User", "Saved", new Date(), "LKKJHK", "DNI");
		GivenCustomerOrder givenCustomerOrder = new GivenCustomerOrder(entityManager);
		return givenCustomerOrder.givenObjectCustomerOrder(customer, new Date(), null);
	}

	@Override
	public Map<String, Object> initTestUpdateValues() {
		Map<String, Object> result = new HashMap<>();
		result.put(CustomerOrder.DATE, new Date());
		return result;
	}
}
