package uk.ac.manchester.cs.hobo.user.app.basic.model;

import uk.ac.manchester.cs.hobo.modeller.*;

/**
 * @author Colin Puleston
 */
public class TextLine extends TextEntity {

	public TextLine(DObjectBuilder builder) {

		super(builder);
	}

	String toAssertionDisplayString(String value) {

		return value;
	}
}