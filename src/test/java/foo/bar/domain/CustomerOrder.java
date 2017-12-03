package foo.bar.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity(name = "CUSTOMER_ORDER")
public class CustomerOrder extends BasicVO<Long> {

	public static final String CUSTOMER = "customer";

	public static final String DATE = "date";

	public static final String PRODUCTS_STOCK = "productsStock";

	@ManyToOne
	@JoinColumn(name = CUSTOMER)
	private Customer customer;

	// TODO: cambiar
	@Column(name = DATE)
	private Date date;

	@OneToMany(mappedBy = ProductStock.PRODUCT, targetEntity = ProductStock.class)
	private List<ProductStock> productsStock;

	public CustomerOrder() {
		super();
	}

	public CustomerOrder(Map<String, Object> mapValues) {
		super(mapValues);
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<ProductStock> getProductsStock() {
		return productsStock;
	}

	public void setProductsStock(List<ProductStock> productsStock) {
		this.productsStock = productsStock;
	}

}
