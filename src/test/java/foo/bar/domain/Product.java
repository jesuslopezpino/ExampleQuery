package foo.bar.domain;

import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.constraints.NotBlank;

import foo.bar.annotations.Reference;

@Entity
@Table(name = "PRODUCT", uniqueConstraints = {
		@UniqueConstraint(name = Product.PRODUCT_UK, columnNames = { Product.NAME }) })
public class Product extends BasicVO<Long> {

	public static final String PRODUCT_UK = "PRODUCT_UK";

	public static final String NAME = "name";

	public static final String DESCRIPTION = "description";

	public static final String PK_LIST = "pkList";

	@Id
	@GeneratedValue(generator = "SQ_PRODUCT")
	@SequenceGenerator(name = "SQ_PRODUCT", sequenceName = "SQ_PRODUCT")
	private Long pk;

	@NotBlank
	@Column(name = NAME)
	private String name;

	@NotBlank
	@Column(name = DESCRIPTION)
	private String description;

	@Transient
	@Reference(referenceFor = Product.PK)
	private List<Long> pkList;

	public Product() {
		super();
	}

	public Product(HashMap<String, Object> mapValues) {
		super(mapValues);
	}

	public Long getPk() {
		return pk;
	}

	public void setPk(Long pk) {
		this.pk = pk;
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
