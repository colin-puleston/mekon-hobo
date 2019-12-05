package uk.ac.manchester.cs.mekon.appmodeller.io;

/**
 * @author Colin Puleston
 */
public class BadContentOntologyException extends Exception {

	static private final long serialVersionUID = -1;

	BadContentOntologyException(RuntimeException origin) {

		super("Bad content-ontology: " + origin.getMessage());
	}
}
