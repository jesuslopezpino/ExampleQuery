package foo.bar.domain;

import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.NotBlank;

import foo.bar.annotations.Reference;

@Entity
public class Product extends BasicVO<Long> {

	public static final String NAME = "name";

	public static final String DESCRIPTION = "description";

	public static final String PK_LIST = "pkList";

	@Transient
	@Reference(fieldName = PK_LIST, referenceFor = Product.PK)
	private List<Long> pkList;

	@NotBlank
	@Column(name = NAME)
	private String name;

	@NotBlank
	@Column(name = DESCRIPTION)
	private String description;

	public Product() {
		super();
	}

	public Product(HashMap<String, Object> mapValues) {
		super(mapValues);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Long> getPkList() {
		return pkList;
	}

	public void setPkList(List<Long> pkList) {
		this.pkList = pkList;
	}

	@Override
	public String toStringDebug() {
		return "Product [pk=" + pk + ", name=" + name + ", description=" + description + ", pkList=" + pkList + "]";
	}

}
