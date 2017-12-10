package foo.bar.domain;

import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import foo.bar.annotations.FilterForField;

@Entity
@Table(name = "PRODUCT_STOCK")
public class ProductStock extends BasicVO<Long> {

	public static final String PRODUCT = "product";

	public static final String PRICE = "price";

	public static final String MAX_PRICE = "maxPrice";

	public static final String MIN_PRICE = "minPrice";

	public static final String CUSTOMER_ORDER = "customerOrder";

	@Id
	@GeneratedValue(generator = "SQ_PRODUCT_STOCK")
	@SequenceGenerator(name = "SQ_PRODUCT_STOCK", sequenceName = "SQ_PRODUCT_STOCK")
	private Long pk;

	@ManyToOne
	@JoinColumn(name = CUSTOMER_ORDER, referencedColumnName = ProductStock.PK)
	private CustomerOrder customerOrder;

	@ManyToOne
	@JoinColumn(name = PRODUCT)
	private Product product;

	@Column(name = PRICE)
	private Integer price;

	@Transient
	@FilterForField(PRICE)
	private Integer maxPrice;

	@Transient
	@FilterForField(PRICE)
	private Integer minPrice;

	public ProductStock() {
		super();
	}

	public ProductStock(HashMap<String, Object> mapValues) {
		super(mapValues);
	}

	public Long getPk() {
		return pk;
	}

	public void setPk(Long pk) {
		this.pk = pk;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Integer getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(Integer maxPrice) {
		this.maxPrice = maxPrice;
	}

	public Integer getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(Integer minPrice) {
		this.minPrice = minPrice;
	}

	public CustomerOrder getCustomerOrder() {
		return customerOrder;
	}

	public void setCustomerOrder(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	@Override
	public String toStringDebug() {
		return "ProductStock [pk=" + pk + ", customerOrder=" + customerOrder + ", product=" + product + ", price="
				+ price + ", maxPrice=" + maxPrice + ", minPrice=" + minPrice + "]";
	}

}
