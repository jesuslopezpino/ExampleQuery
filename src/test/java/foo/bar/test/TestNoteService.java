package foo.bar.test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import foo.bar.domain.Customer;
import foo.bar.domain.Note;
import foo.bar.exceptions.UniqueException;
import foo.bar.service.impl.NoteServiceImpl;
import foo.bar.service.utils.HqlConditions;
import foo.bar.utils.Utils;

public class TestNoteService extends TestCommon<NoteServiceImpl, Note> {

	@Override
	protected String[] initCustomFields() {
		String[] customFields = { Note.DATE, Note.NOTE };
		return customFields;
	}

	@Override
	protected Map<String, HqlConditions> initFilter() {

		Map<String, HqlConditions> filter = new HashMap<>();

		// example 1
		filter.put(Note.NOTE, HqlConditions.LIKE_IGNORE_CASE);

		// Example2
//		filter.put(Note.CUSTOMER, HqlConditions.NOT_EQUALS);

		return filter;
	}

	@Override
	protected Note[] initExamples() {
		try {
			Note example1 = new Note();
			example1.setNote("e");

			Note example2 = new Note();
			Customer customer = Given.givenADefaultCustomer(entityManager);
			example2.setCustomer(customer);
			Note[] examples = { example1, example2 };
			return examples;
		} catch (UniqueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	protected void givenExamplesEnviroment() {
		super.logGivenEnviromentStart();
		Customer realCustomer;
		Customer realCustomer2;
		try {
			realCustomer = Given.givenACustomer(11123L, "Real", "Customer", new Date(), "REALDOC", "IDENTIFICATION",
					entityManager);
			realCustomer2 = Given.givenACustomer(24324L, "Real2", "Customer2", new Date(), "REALDOC2", "IDENTIFICATION2",
					entityManager);
			Given.givenANote(6541L, new Date(), realCustomer, "text note", entityManager);
			Given.givenANote(25322L, new Date(), realCustomer2, "text note 2", entityManager);
		} catch (UniqueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.logGivenEnviromentEnd();
	}

	@Override
	protected Note initSaveEntity() throws UniqueException {
		return Given.givenObjectNote(199L, Utils.getDate("01/01/2017 00:00:00", TIME_FORMAT), null, "First note");
	}

	@Override
	protected Map<String, Object> initEntityFields() {
		Map<String, Object> initFields = new HashMap<>();
		initFields.put(Note.PK, 1l);
		initFields.put(Note.DATE, new Date());
		initFields.put(Note.CUSTOMER, new Customer());
		initFields.put(Note.NOTE, "note body");
		return initFields;
	}

	@Override
	protected String initUpdateField() {
		return Note.NOTE;
	}

	@Override
	protected Object initUpdateValue() {
		return "Updated note";
	}

}
