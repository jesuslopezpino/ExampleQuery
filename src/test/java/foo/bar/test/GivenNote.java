package foo.bar.test;

import java.util.Date;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import foo.bar.domain.Customer;
import foo.bar.domain.Note;
import foo.bar.exceptions.UniqueException;
import foo.bar.service.impl.NoteServiceImpl;

public class GivenNote {

	private static Logger LOGGER = Logger.getLogger(GivenNote.class);

	public static Note givenANote(Date date, Customer customer, String note, EntityManager entityManager)
			throws UniqueException {
		Note result = GivenNote.givenObjectNote(date, customer, note);
		NoteServiceImpl service = new NoteServiceImpl();
		service.setEntityManager(entityManager);
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

}
