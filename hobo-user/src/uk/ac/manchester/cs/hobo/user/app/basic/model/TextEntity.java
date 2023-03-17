package uk.ac.manchester.cs.hobo.user.app.basic.model;

import uk.ac.manchester.cs.hobo.model.*;
import uk.ac.manchester.cs.hobo.modeller.*;

/**
 * @author Colin Puleston
 */
public abstract class TextEntity extends CustomValue {

	static private final String QUERY_DISPLAY_STRING_FORMAT = "CONTAINS %s";

	public final DCell<String> text;

	public TextEntity(DObjectBuilder builder) {

		super(builder);

		text = builder.addStringCell();

		initialise(builder, text);
	}

	public String toDisplayString() {

		if (text.isSet()) {

			String value = text.get();

			return assertionObject()
					? toAssertionDisplayString(value)
					: toQueryDisplayString(value);
		}

		return "";
	}

	public void setQueryExpression(TextExpression expr) {

		checkQueryObjectAccess();

		text.set(expr.toQueryString());
	}

	public TextExpression getQueryExpression() {

		checkQueryObjectAccess();

		return TextExpression.fromQueryString(text.get());
	}

	abstract String toAssertionDisplayString(String value);

	private String toQueryDisplayString(String value) {

		String expr = getQueryExpression().toDisplayString();

		return String.format(QUERY_DISPLAY_STRING_FORMAT, expr);
	}

	private void checkQueryObjectAccess() {

		if (assertionObject()) {

			throw new RuntimeException("Cannot perform operation on assertion object!");
		}
	}

	private boolean assertionObject() {

		return getFrame().getFunction().assertion();
	}
}