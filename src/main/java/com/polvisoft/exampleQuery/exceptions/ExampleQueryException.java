package com.polvisoft.exampleQuery.exceptions;

import org.apache.log4j.Logger;

public class ExampleQueryException extends Exception {

	/**
	 * serializable
	 */
	private static final long serialVersionUID = 5907339893739561101L;

	private final Exception exception;

	private static final Logger LOGGER = Logger.getLogger(ExampleQueryException.class);

	public ExampleQueryException(final Exception exception) {
		this.exception = exception;
		exception.printStackTrace();
	}

	public Exception getException() {
		return this.exception;
	}

}
