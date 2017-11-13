package foo.bar.exceptions;

import org.apache.log4j.Logger;

public class ExampleQueryException extends Exception {

	private static final Logger LOGGER = Logger.getLogger(ExampleQueryException.class);

	public ExampleQueryException(String message) {
		LOGGER.error(message);
		this.printStackTrace();
	}

	/**
	 * serializable
	 */
	private static final long serialVersionUID = 5907339893739561101L;

}
