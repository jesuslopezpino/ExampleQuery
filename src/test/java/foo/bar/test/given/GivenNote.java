package foo.bar.test.given;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import foo.bar.domain.Customer;
import foo.bar.domain.Note;
import foo.bar.exceptions.ExampleQueryException;
import foo.bar.exceptions.UniqueException;
import foo.bar.filter.FilterMap;
import foo.bar.service.impl.NoteServiceImpl;
import foo.bar.service.utils.HqlConditions;
import foo.bar.test.common.Given;
import foo.bar.utils.Utils;

public class GivenNote extends Given<Note, NoteServiceImpl> {

	private static Logger LOGGER = Logger.getLogger(GivenNote.class);

	public GivenNote(EntityManager entityManager) throws ExampleQueryException {
		super(entityManager);
	}

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

	@Override
	public void givenExamplesEnvironment() throws UniqueException, ExampleQueryException {
		GivenCustomer givenCustomer = new GivenCustomer(this.entityManager);
		Customer customer = givenCustomer.givenACustomer("Real", "Customer", new Date(), "REALDOC", "ID");
		Customer customer2 = givenCustomer.givenACustomer("Real2", "Customer2", new Date(), "REALDOC2", "ID");
		this.givenANote(new Date(), customer, "text note");
		this.givenANote(new Date(), customer, "second user note");
		this.givenANote(new Date(), customer, "another user note");
		this.givenANote(new Date(), customer2, "text note customer 2");
	}

	@Override
	public String[] initCustomFields() {
		String[] customFields = { Note.DATE, Note.NOTE };
		return customFields;
	}

	@Override
	public FilterMap initFilter() {

		FilterMap filter = new FilterMap();

		// example 1
		filter.put(Note.NOTE, HqlConditions.LIKE_IGNORE_CASE);

		// Example2
		// filter.put(Note.CUSTOMER, HqlConditions.NOT_EQUALS);

		return filter;
	}

	@Override
	public Note[] initExamples() {
		Note example1 = new Note();
		example1.setNote("e");

		Note example2 = new Note();
		Customer customer = new Customer();
		customer.setPk(1L);
		example2.setCustomer(customer);

		Note[] examples = { example1, example2 };
		return examples;
	}

	@Override
	public Note initTestSaveInstance() throws UniqueException, ExampleQueryException {
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
		return result;
	}

	public static String noteToString(Note note) {
		return "Note [pk=" + note.getPk() + ", date=" + note.getDate() + ", note=" + note.getNote() + ", customer="
				+ note.getCustomer() + "]";
	}

	@Override
	public int initPageNumber() {
		return 0;
	}

	@Override
	public int initPageSize() {
		return 10;
	}

}
