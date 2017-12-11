package foo.bar.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import foo.bar.annotations.FilterForField;

@Entity
@Table(name = "CUSTOMER_ORDER")
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
	@FilterForField(PRODUCTS_STOCK + "." + ProductStock.PK)
	private List<Long> productsStockIds;

	public CustomerOrder() {
		super();
	}

	public CustomerOrder(Map<String, Object> mapValues) {
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

	public Customer getCustomer() {
		return this.customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<ProductStock> getProductsStock() {
		return this.productsStock;
	}

	public void setProductsStock(List<ProductStock> productsStock) {
		this.productsStock = productsStock;
	}

	public List<Long> getProductsStockIds() {
		return this.productsStockIds;
	}

	public void setProductsStockIds(List<Long> productsStockIds) {
		this.productsStockIds = productsStockIds;
	}

	@Override
	public String toStringDebug() {
		return "CustomerOrder [pk=" + this.pk + ", customer=" + this.customer + ", date=" + this.date + ", productsStock="
				+ this.productsStock + ", productsStockIds=" + this.productsStockIds + "]";
	}

}
