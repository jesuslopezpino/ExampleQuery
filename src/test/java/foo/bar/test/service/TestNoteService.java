package foo.bar.test.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import foo.bar.domain.Customer;
import foo.bar.domain.Note;
import foo.bar.exceptions.UniqueException;
import foo.bar.service.impl.NoteServiceImpl;
import foo.bar.service.utils.HqlConditions;
import foo.bar.test.TestCommon;
import foo.bar.test.given.GivenCustomer;
import foo.bar.test.given.GivenNote;
import foo.bar.utils.Utils;

public class TestNoteService extends TestCommon<NoteServiceImpl, Note, GivenNote> {

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
		// filter.put(Note.CUSTOMER, HqlConditions.NOT_EQUALS);

		return filter;
	}

	@Override
	protected Note[] initExamples() throws UniqueException, InstantiationException, IllegalAccessException {
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
	protected Note initSaveEntity() throws UniqueException {
		return GivenNote.givenObjectNote(Utils.getDateTime("01/01/2017 00:00:00"), null, "First note");
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
