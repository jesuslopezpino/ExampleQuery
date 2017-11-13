package foo.bar.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Order extends BasicVO<Long>{

	public static final String CUSTOMER = "customer";

	public static final String DATE = "date";

	public static final String PRODUCTS = "products";

	@ManyToOne
	@JoinColumn(name = CUSTOMER)
	private Customer customer;
	
	@Column(name = DATE)
	private Date date;
	
	@OneToMany(mappedBy = PRODUCTS)
	private List<ProductStock> products;

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

	public List<ProductStock> getProducts() {
		return products;
	}

	public void setProducts(List<ProductStock> products) {
		this.products = products;
	}
	
	
}
