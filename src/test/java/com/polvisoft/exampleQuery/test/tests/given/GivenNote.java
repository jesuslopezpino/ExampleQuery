package com.polvisoft.exampleQuery.test.tests.given;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.polvisoft.exampleQuery.enums.HqlConditions;
import com.polvisoft.exampleQuery.exceptions.ExampleQueryException;
import com.polvisoft.exampleQuery.exceptions.UniqueException;
import com.polvisoft.exampleQuery.filter.FilterMap;
import com.polvisoft.exampleQuery.test.common.Given;
import com.polvisoft.exampleQuery.test.domain.Customer;
import com.polvisoft.exampleQuery.test.domain.Note;
import com.polvisoft.exampleQuery.test.repository.impl.NoteRepositoryImpl;
import com.polvisoft.exampleQuery.utils.Utils;

public class GivenNote extends Given<Note, NoteRepositoryImpl> {

	private static Logger LOGGER = Logger.getLogger(GivenNote.class);

	public GivenNote(EntityManager entityManager) throws ExampleQueryException {
		super(entityManager);
	}

	public Note givenANote(Date date, Customer customer, String note) throws UniqueException {
		Note result = GivenNote.givenObjectNote(date, customer, note);
		NoteRepositoryImpl repository = new NoteRepositoryImpl();
		repository.setEntityManager(this.entityManager);
		result = repository.save(result);
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
