package foo.bar.domain;

import java.util.Map;

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
import foo.bar.exceptions.ExampleQueryException;

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

	public ProductStock(Map<String, Object> mapValues) throws ExampleQueryException {
		super(mapValues);
	}

	@Override
	public Long getPk() {
		return this.pk;
	}

	@Override
	public void setPk(Long pk) {
		this.pk = pk;
	}

	public Product getProduct() {
		return this.product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Integer getPrice() {
		return this.price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Integer getMaxPrice() {
		return this.maxPrice;
	}

	public void setMaxPrice(Integer maxPrice) {
		this.maxPrice = maxPrice;
	}

	public Integer getMinPrice() {
		return this.minPrice;
	}

	public void setMinPrice(Integer minPrice) {
		this.minPrice = minPrice;
	}

	public CustomerOrder getCustomerOrder() {
		return this.customerOrder;
	}

	public void setCustomerOrder(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	@Override
	public String toStringDebug() {
		return "ProductStock [pk=" + this.pk + ", customerOrder=" + this.customerOrder + ", product=" + this.product + ", price="
				+ this.price + ", maxPrice=" + this.maxPrice + ", minPrice=" + this.minPrice + "]";
	}

}
