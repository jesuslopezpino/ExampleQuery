package com.polvisoft.exampleQuery.test.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.polvisoft.exampleQuery.annotations.FilterForField;
import com.polvisoft.exampleQuery.domain.BasicDTO;

@Entity
@Table(name = "CUSTOMER", uniqueConstraints = {
		@UniqueConstraint(name = "DOCUMENT_UK", columnNames = { Customer.DOCUMENT }) })
public class Customer extends BasicDTO<Long> {

	public static final String NAME = "name";

	public static final String LAST_NAME = "lastName";

	public static final String DOCUMENT = "document";

	public static final String DOCUMENT_TYPE = "documentType";

	public static final String DOCUMENT_TYPE_LIST = "documentTypeList";

	public static final String BIRTH_DATE = "birthDate";

	public static final String BIRTH_DATE_START = "birthDateStart";

	public static final String BIRTH_DATE_END = "birthDateEnd";

	public static final String CUSTOMER_ORDERS = "customerOrders";

	public static final String CUSTOMER_ORDERS_PRODUCTS_NAME = "customerOrdersProductName";

	public static final String NOTES = "notes";

	@Id
	@GeneratedValue(generator = "SQ_CUSTOMER")
	@SequenceGenerator(name = "SQ_CUSTOMER", sequenceName = "SQ_CUSTOMER")
	private Long pk;

	@NotBlank
	@Column(name = NAME)
	private String name;

	@NotBlank
	@Column(name = LAST_NAME)
	private String lastName;

	@NotBlank
	@Column(name = DOCUMENT)
	private String document;

	@NotBlank
	@Column(name = DOCUMENT_TYPE)
	private String documentType;

	@NotNull
	@Column(name = BIRTH_DATE)
	private Date birthDate;

	@OneToMany(mappedBy = CustomerOrder.CUSTOMER, targetEntity = CustomerOrder.class)
	private List<CustomerOrder> customerOrders;

	@OneToMany(mappedBy = Note.CUSTOMER, targetEntity = Note.class)
	private List<Note> notes;

	@Transient
	@FilterForField(Customer.CUSTOMER_ORDERS + "." + CustomerOrder.PRODUCTS_STOCK + "." + ProductStock.PRODUCT + "."
			+ Product.NAME)
	private String customerOrdersProductName;

	@Transient
	@FilterForField(DOCUMENT_TYPE)
	private List<String> documentTypeList;

	@Transient
	@FilterForField(BIRTH_DATE)
	private Date birthDateStart;

	@Transient
	@FilterForField(BIRTH_DATE)
	private Date birthDateEnd;

	public Customer() {
		super();
	}

	@Override
	public Long getPk() {
		return this.pk;
	}

	@Override
	public void setPk(Long pk) {
		this.pk = pk;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDocument() {
		return this.document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public String getDocumentType() {
		return this.documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public Date getBirthDate() {
		return this.birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Date getBirthDateStart() {
		return this.birthDateStart;
	}

	public void setBirthDateStart(Date birthDateStart) {
		this.birthDateStart = birthDateStart;
	}

	public Date getBirthDateEnd() {
		return this.birthDateEnd;
	}

	public void setBirthDateEnd(Date birthDateEnd) {
		this.birthDateEnd = birthDateEnd;
	}

	public List<String> getDocumentTypeList() {
		return this.documentTypeList;
	}

	public void setDocumentTypeList(List<String> documentTypeList) {
		this.documentTypeList = documentTypeList;
	}

	public String getCustomerOrdersProductName() {
		return this.customerOrdersProductName;
	}

	public void setCustomerOrdersProductName(String customerOrdersProductName) {
		this.customerOrdersProductName = customerOrdersProductName;
	}

	public List<CustomerOrder> getCustomerOrders() {
		return this.customerOrders;
	}

	public void setCustomerOrders(List<CustomerOrder> customerOrders) {
		this.customerOrders = customerOrders;
	}

	public List<Note> getNotes() {
		return this.notes;
	}

	public void setNotes(List<Note> notes) {
		this.notes = notes;
	}

	@Override
	public String toStringDebug() {
		return "Customer [pk=" + this.pk + ", name=" + this.name + ", lastName=" + this.lastName + ", document=" + this.document
				+ ", documentType=" + this.documentType + ", birthDate=" + this.birthDate + ", customerOrders=" + this.customerOrders
				+ ", customerOrdersProductName=" + this.customerOrdersProductName + ", documentTypeList=" + this.documentTypeList
				+ ", birthDateStart=" + this.birthDateStart + ", birthDateEnd=" + this.birthDateEnd + ", notes=" + this.notes + "]";
	}

}
