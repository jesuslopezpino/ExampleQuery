package foo.bar.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ProductStock extends BasicVO<Long> {

	public static final String PRODUCT = "product";
	
	public static final String QUANTITY = "quantity";

	@ManyToOne
	@JoinColumn(name = PRODUCT)
	public Product product;

	@Column(name = QUANTITY)
	public Integer quantity;

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

	
}
