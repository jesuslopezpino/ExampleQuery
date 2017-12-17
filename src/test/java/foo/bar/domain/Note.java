package foo.bar.domain;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.polvisoft.domain.BasicVO;
import com.polvisoft.exceptions.ExampleQueryException;

@Entity
@Table(name = "NOTE")
public class Note extends BasicVO<Long> {

	public static final String CUSTOMER = "customer";

	public static final String DATE = "date";

	public static final String NOTE = "note";

	@Id
	@GeneratedValue(generator = "SQ_NOTE")
	@SequenceGenerator(name = "SQ_NOTE", sequenceName = "SQ_NOTE")
	private Long pk;
	
	@NotNull
	@Column(name = DATE)
	private Date date;

	@NotEmpty
	@Column(name = NOTE)
	private String note;

	@ManyToOne(targetEntity = Customer.class)
	private Customer customer;

	public Note() {
		super();
	}

	public Note(Map<String, Object> mapValues) throws ExampleQueryException {
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
	
	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Customer getCustomer() {
		return this.customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@Override
	public String toStringDebug() {
		return "Note [pk=" + this.pk + ", date=" + this.date + ", note=" + this.note + ", customer=" + this.customer + "]";
	}

}
