package foo.bar.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import foo.bar.annotations.Reference;

@Entity(name = "CUSTOMER_ORDER")
public class CustomerOrder extends BasicVO<Long> {

	public static final String CUSTOMER = "customer";

	public static final String DATE = "date";

	public static final String PRODUCTS_STOCK = "productsStock";

	public static final String PRODUCTS_STOCK_IDS = "productsStockIds";

	@Id
	@GeneratedValue(generator = "SQ_CUSTOMER_ORDER")
	@SequenceGenerator(name = "SQ_CUSTOMER_ORDER", sequenceName = "SQ_CUSTOMER_ORDER")
	private Long pk;
	
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
	@Reference(fieldName = PRODUCTS_STOCK_IDS, referenceFor = PRODUCTS_STOCK + "." + ProductStock.PK)
	private List<Long> productsStockIds;

	public CustomerOrder() {
		super();
	}

	public CustomerOrder(HashMap<String, Object> mapValues) {
		super(mapValues);
	}

	public Long getPk() {
		return pk;
	}

	public void setPk(Long pk) {
		this.pk = pk;
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

}
