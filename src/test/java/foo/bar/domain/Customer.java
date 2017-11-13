package foo.bar.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import foo.bar.annotations.Reference;
import foo.bar.annotations.readers.DateRange;

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

	
	public Customer() {
		super();
	}

	public Customer(Map<String, Object> mapValues) {
		super(mapValues);
	}

	@Column(name = NAME)
	public String name;

	@Column(name = LAST_NAME)
	public String lastName;

	@Column(name = DOCUMENT)
	public String document;

	@Reference(DOCUMENT_TYPE_LIST)
	@Column(name = DOCUMENT_TYPE)
	public String documentType;

	@Transient
	public List<String> documentTypeList;

	
	@DateRange(startField = BIRTH_DATE_START, endField = BIRTH_DATE_END)
	@Column(name = BIRTH_DATE)
	public Date birthDate;

	@Transient
	public Date birthDateStart;

	@Transient
	public Date birthDateEnd;

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
	
	
}
