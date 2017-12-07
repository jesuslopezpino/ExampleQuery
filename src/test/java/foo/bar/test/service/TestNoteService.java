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

	
}
