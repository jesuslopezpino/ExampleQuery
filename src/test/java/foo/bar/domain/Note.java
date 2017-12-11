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

	public Note(Map<String, Object> mapValues) {
		super(mapValues);
	}

	public Long getPk() {
		return pk;
	}

	public void setPk(Long pk) {
		this.pk = pk;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@Override
	public String toStringDebug() {
		return "Note [pk=" + pk + ", date=" + date + ", note=" + note + ", customer=" + customer + "]";
	}

}
