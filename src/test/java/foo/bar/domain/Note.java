package foo.bar.domain;

import java.util.Date;
import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class Note extends BasicVO<Long> {

	public static final String CUSTOMER = "customer";

	public static final String DATE = "date";

	public static final String NOTE = "note";

	@NotNull
	@Column(name = DATE)
	private Date date;

	@NotEmpty
	@Column(name = NOTE)
	private String note;

	@NotNull
	@ManyToOne(targetEntity = Customer.class)
	private Customer customer;

	public Note() {
		super();
	}

	public Note(HashMap<String, Object> mapValues) {
		super(mapValues);
	}

	@Override
	public String toStringDebug() {
		// TODO Auto-generated method stub
		return null;
	}

}
