package foo.bar.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import foo.bar.annotations.Reference;

@Entity(name = "CUSTOMER_ORDER")
public class CustomerOrder extends BasicVO<Long> {

	public static final String CUSTOMER = "customer";

	public static final String DATE = "date";

	public static final String PRODUCTS_STOCK = "productsStock";

	public static final String PRODUCTS_STOCK_IDS = "productsStockIds";

	@NotNull
	@ManyToOne
	@JoinColumn(name = CUSTOMER, referencedColumnName = CustomerOrder.PK)
	private Customer customer;

	@NotNull
	@Column(name = DATE)
	private Date date;

	@OneToMany(mappedBy = ProductStock.CUSTOMER_ORDER, targetEntity = ProductStock.class)
	private List<ProductStock> productsStock;

	@Transient
	@Reference(fieldName = ProductStock.PRODUCT_STOCK_IDS, referenceFor = PRODUCTS_STOCK + "." + ProductStock.PK)
	private List<Long> productsStockIds;

	public CustomerOrder() {
		super();
	}

	public CustomerOrder(HashMap<String, Object> mapValues) {
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

	public List<Long> getProductsStockIds() {
		return productsStockIds;
	}

	public void setProductsStockIds(List<Long> productsStockIds) {
		this.productsStockIds = productsStockIds;
	}

	@Override
	public String toStringDebug() {
		return "CustomerOrder [pk=" + pk + ", customer=" + customer + ", date=" + date + ", productsStock="
				+ productsStock + ", productsStockIds=" + productsStockIds + "]";
	}

//	@Override
	public String toStringNormal() {
		return "CustomerOrder [pk=" + pk + ", date=" + date + ", customer=" + customer + ", productsStock="
				+ productsStock + "]";
	}

}
