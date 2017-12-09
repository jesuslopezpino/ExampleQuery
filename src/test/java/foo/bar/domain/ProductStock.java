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

import foo.bar.annotations.Reference;

@Entity
@Table(name = "PRODUCT_STOCK")
public class ProductStock extends BasicVO<Long> {

	public static final String PRODUCT = "product";

	public static final String QUANTITY = "quantity";

	public static final String MAX_QUANTITY = "maxQuantity";

	public static final String MIN_QUANTITY = "minQuantity";

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

	@Column(name = QUANTITY)
	private Integer quantity;

	@Transient
	@Reference(fieldName = MAX_QUANTITY, referenceFor = QUANTITY)
	private Integer maxQuantity;

	@Transient
	@Reference(fieldName = MIN_QUANTITY, referenceFor = QUANTITY)
	private Integer minQuantity;

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

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getMaxQuantity() {
		return maxQuantity;
	}

	public void setMaxQuantity(Integer maxQuantity) {
		this.maxQuantity = maxQuantity;
	}

	public Integer getMinQuantity() {
		return minQuantity;
	}

	public void setMinQuantity(Integer minQuantity) {
		this.minQuantity = minQuantity;
	}

	public CustomerOrder getCustomerOrder() {
		return customerOrder;
	}

	public void setCustomerOrder(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	@Override
	public String toStringDebug() {
		return "ProductStock [pk=" + pk + ", customerOrder=" + customerOrder + ", product=" + product + ", quantity="
				+ quantity + ", maxQuantity=" + maxQuantity + ", minQuantity=" + minQuantity + "]";
	}

}
