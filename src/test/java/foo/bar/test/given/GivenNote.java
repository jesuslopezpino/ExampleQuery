package foo.bar.test.given;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import foo.bar.domain.Customer;
import foo.bar.domain.Note;
import foo.bar.exceptions.UniqueException;
import foo.bar.service.impl.NoteServiceImpl;
import foo.bar.service.utils.HqlConditions;
import foo.bar.utils.Utils;

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
	public void givenExamplesEnvironment() throws UniqueException, InstantiationException, IllegalAccessException {
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

	@Override
	public Map<String, HqlConditions> initFilter() {

		Map<String, HqlConditions> filter = new HashMap<>();

		// example 1
		filter.put(Note.NOTE, HqlConditions.LIKE_IGNORE_CASE);

		// Example2
		// filter.put(Note.CUSTOMER, HqlConditions.NOT_EQUALS);

		return filter;
	}

	@Override
	public Note[] initExamples() throws UniqueException, InstantiationException, IllegalAccessException {
		Note example1 = new Note();
		example1.setNote("e");

		Note example2 = new Note();
		GivenCustomer givenCustomer = new GivenCustomer(entityManager);
		Customer customer = givenCustomer.givenADefaultCustomer();
		example2.setCustomer(customer);

		Note[] examples = { example1, example2 };
		return examples;
	}

	@Override
	public Note initTestSaveInstance() throws UniqueException {
		return GivenNote.givenObjectNote(Utils.getDateTime("01/01/2017 00:00:00"), null, "First note");
	}

	@Override
	public Map<String, Object> initEntityFields() {
		Map<String, Object> initFields = new HashMap<>();
		initFields.put(Note.PK, 1l);
		initFields.put(Note.DATE, new Date());
		initFields.put(Note.CUSTOMER, new Customer());
		initFields.put(Note.NOTE, "note body");
		return initFields;
	}

	@Override
	public Map<String, Object> initTestUpdateValues() {
		Map<String, Object> result = new HashMap<>();
		result.put(Note.NOTE, "Updated note");
		result.put(Note.DATE, new Date());
//		result.put(Note.NOTE, "Updated note");
		return result;
	}
}
