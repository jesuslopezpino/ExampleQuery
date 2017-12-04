package foo.bar.domain;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import foo.bar.annotations.Reference;

@Entity
public class ProductStock extends BasicVO<Long> {

	public static final String PRODUCT = "product";

	public static final String PRODUCT_NAME = "productName";

	public static final String QUANTITY = "quantity";

	public static final String MAX_QUANTITY = "maxQuantity";

	public static final String MIN_QUANTITY = "minQuantity";

	public static final String CUSTOMER_ORDER = "customerOrder";

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
	public Integer maxQuantity;

	@Transient
	@Reference(fieldName = MIN_QUANTITY, referenceFor = QUANTITY)
	public Integer minQuantity;

	@Transient
	@Reference(fieldName = PRODUCT_NAME, referenceFor = PRODUCT + "." + Product.NAME)
	public String productName;

	public ProductStock() {

	}

	public ProductStock(Map<String, Object> mapValues) {
		super(mapValues);
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

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
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

}
