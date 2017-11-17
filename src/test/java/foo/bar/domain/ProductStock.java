package foo.bar.domain;

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

	@ManyToOne
	@JoinColumn(name = PRODUCT)
	public Product product;

	@Column(name = QUANTITY)
	public Integer quantity;

	@Transient
	@Reference(fieldName = MAX_QUANTITY, referenceFor = QUANTITY)
	public Integer maxQuantity;

	@Transient
	@Reference(fieldName = MIN_QUANTITY, referenceFor = QUANTITY)
	public Integer minQuantity;
	
	@Transient
	@Reference(fieldName = PRODUCT_NAME, referenceFor = PRODUCT + "." + Product.NAME)
	public String productName;
	
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
	
}
