package foo.bar.test.given;

import java.util.Date;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import foo.bar.domain.Customer;
import foo.bar.domain.Note;
import foo.bar.exceptions.UniqueException;
import foo.bar.service.impl.NoteServiceImpl;

public class GivenNote extends Given<Note, NoteServiceImpl> {

	public GivenNote(EntityManager entityManager) throws InstantiationException, IllegalAccessException {
		super(entityManager);
	}

	private static Logger LOGGER = Logger.getLogger(GivenNote.class);

	public Note givenANote(Date date, Customer customer, String note) throws UniqueException {
		Note result = GivenNote.givenObjectNote(date, customer, note);
		NoteServiceImpl service = new NoteServiceImpl();
		service.setEntityManager(this.entityManager);
		result = service.save(result);
		LOGGER.info("GivenNote instance persisted " + GivenNote.noteToString(result));
		return result;
	}

	public static Note givenObjectNote(Date date, Customer customer, String note) {
		Note result = new Note();
		result.setDate(date);
		result.setCustomer(customer);
		result.setNote(note);
		LOGGER.info("GivenNote class instance " + GivenNote.noteToString(result));
		return result;
	}

	public static String noteToString(Note note) {
		return "Note [pk=" + note.getPk() + ", date=" + note.getDate() + ", note=" + note.getNote() + ", customer="
				+ note.getCustomer() + "]";
	}

	@Override
	public void givenExamplesEnviroment() throws UniqueException, InstantiationException, IllegalAccessException {
		GivenCustomer givenCustomer = new GivenCustomer(entityManager);
		Customer customer = givenCustomer.givenACustomer("Real", "Customer", new Date(), "REALDOC", "ID");
		Customer customer2 = givenCustomer.givenACustomer("Real2", "Customer2", new Date(), "REALDOC2", "ID");
		givenANote(new Date(), customer, "text note");
		givenANote(new Date(), customer, "second user note");
		givenANote(new Date(), customer, "another user note");
		givenANote(new Date(), customer2, "text note customer 2");
	}

	@Override
	public String[] initCustomFields() {
		String[] customFields = { Note.DATE, Note.NOTE };
		return customFields;
	}
}
