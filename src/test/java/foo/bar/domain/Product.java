package foo.bar.domain;

import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

@Entity
public class Product extends BasicVO<Long> {

	public static final String NAME = "name";

	public static final String DESCRIPTION = "description";

	public Product() {
		super();
	}

	public Product(HashMap<String, Object> mapValues) {
		super(mapValues);
	}

	@NotBlank
	@Column(name = NAME)
	private String name;

	@NotBlank
	@Column(name = DESCRIPTION)
	private String description;

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

	@Override
	public String toStringDebug() {
		return "Product [pk=" + pk + ", name=" + name + ", description=" + description + "]";
	}

//	@Override
	public String toStringNormal() {
		return "Product [pk=" + pk + ", name=" + name + ", description=" + description + "]";
	}

	
}
