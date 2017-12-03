package foo.bar.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import foo.bar.annotations.Reference;
import foo.bar.annotations.readers.Range;

@Entity
public class Customer extends BasicVO<Long> {

	public static final String NAME = "name";

	public static final String LAST_NAME = "lastName";

	public static final String DOCUMENT = "document";

	public static final String DOCUMENT_TYPE = "documentType";

	public static final String DOCUMENT_TYPE_LIST = "documentTypeList";

	public static final String BIRTH_DATE = "birthDate";

	public static final String BIRTH_DATE_START = "birthDateStart";

	public static final String BIRTH_DATE_END = "birthDateEnd";

	public static final String CUSTOMER_ORDERS = "customerOrders";

	public static final String ORDERS_PRODUCTS_NAME = "ordersProductsName";

	@Column(name = NAME)
	private String name;

	@Column(name = LAST_NAME)
	private String lastName;

	@Column(name = DOCUMENT)
	private String document;

	@Column(name = DOCUMENT_TYPE)
	private String documentType;

	// @Range(startField = BIRTH_DATE_START, endField = BIRTH_DATE_END)
	@Column(name = BIRTH_DATE)
	private Date birthDate;

	@OneToMany(mappedBy = CustomerOrder.CUSTOMER, targetEntity = CustomerOrder.class)
	private List<CustomerOrder> customerOrders;

	// TODO: if we don't include the transient annotation que query will not
	// consider the Reference Annotation...
	@Transient
	@Reference(fieldName = ORDERS_PRODUCTS_NAME, referenceFor = Customer.CUSTOMER_ORDERS + "." + CustomerOrder.PRODUCTS_STOCK + "."
			+ ProductStock.PRODUCT + "." + Product.NAME)
	private String ordersProductsName;

	@Transient
	@Reference(fieldName = DOCUMENT_TYPE_LIST, referenceFor = DOCUMENT_TYPE)
	private List<String> documentTypeList;

	@Transient
	@Reference(fieldName = BIRTH_DATE_START, referenceFor = BIRTH_DATE)
	private Date birthDateStart;

	@Transient
	@Reference(fieldName = BIRTH_DATE_END, referenceFor = BIRTH_DATE)
	private Date birthDateEnd;

	public Customer() {
		super();
	}

	public Customer(Map<String, Object> mapValues) {
		super(mapValues);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Date getBirthDateStart() {
		return birthDateStart;
	}

	public void setBirthDateStart(Date birthDateStart) {
		this.birthDateStart = birthDateStart;
	}

	public Date getBirthDateEnd() {
		return birthDateEnd;
	}

	public void setBirthDateEnd(Date birthDateEnd) {
		this.birthDateEnd = birthDateEnd;
	}

	public List<String> getDocumentTypeList() {
		return documentTypeList;
	}

	public void setDocumentTypeList(List<String> documentTypeList) {
		this.documentTypeList = documentTypeList;
	}

	public String getOrdersProductsName() {
		return ordersProductsName;
	}

	public void setOrdersProductsName(String ordersProductsName) {
		this.ordersProductsName = ordersProductsName;
	}

	public List<CustomerOrder> getOrders() {
		return customerOrders;
	}

	public void setOrders(List<CustomerOrder> customerOrders) {
		this.customerOrders = customerOrders;
	}

}
